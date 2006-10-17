/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;
import edu.colorado.phet.ec3.model.spline.SegmentPath;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:54 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeSplineMode extends ForceMode {
    private EnergyConservationModel model;
    private AbstractSpline spline;
    private Body body;
    private double lastDA;
    private boolean lastGrabState = false;
    private boolean bounced = false;
    private boolean grabbed = false;
    private Segment lastSegment;
    private double bounceThreshold = 4;
    private final double flipTimeThreshold = 1.0;
    private static boolean errorYetThisStep = false;
    private static boolean debug = true;
    //    private static boolean debug = true;
    private boolean debugState = false;

    public FreeSplineMode( EnergyConservationModel model, AbstractSpline spline, Body body ) {
        this.model = model;
        this.spline = spline;
        this.body = body;
    }

    public static void debug( String type, State origState, Body body ) {
        if( debug ) {
            double dE = new State( body ).getTotalEnergy() - origState.getTotalEnergy();
            if( Math.abs( dE ) > 100 ) {
                if( !errorYetThisStep ) {
                    errorYetThisStep = true;
                    debug( "Step Started---------" );
                }
                debug( type + ", dE = " + dE );
            }
            double dx = body.getCenterOfMass().distance( origState.getBody().getCenterOfMass() );
            if( dx > 1 ) {
                debug( "CM Moved: " + dx );
                new RuntimeException( "CM Moved: " + dx ).printStackTrace();
            }
        }
    }

    private static void debug( String s ) {
        if( debug ) {
            System.out.println( s );
        }
    }

    public void debug2( String type, double desiredEnergy, Body body ) {
        if( debug ) {
            double dE = new State( body ).getTotalEnergy() - desiredEnergy;
//            if( Math.abs( dE ) > 1E-6 ) {
            if( Math.abs( dE ) > 1E1 ) {
                System.out.println( type + ", dE = " + dE );
            }
        }
    }

    private ArrayList speedHistory = new ArrayList();

    public void stepInTime( Body body, double dt ) {
        stepStarted();
        State originalState = new State( body );
        if( body.isUserControlled() ) {
            speedHistory.clear();
        }
        speedHistory.add( new Double( body.getSpeed() ) );
        if( speedHistory.size() > 30 ) {
            speedHistory.remove( 0 );
        }

        Segment segment = getSegment( body );
        if( okayToStop( model, segment, body ) ) {//hack to stop when almost stopped near the bottom of a potential well.
            double ke = body.getKineticEnergy();
            body.setVelocity( new Vector2D.Double( 0, 0 ) );
            body.addThermalEnergy( ke );
            debugState( "Stopping" );
            return;
        }

        if( segment == null ) {
            flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
            debugState( "Fly off surface1" );
            return;
        }
        rotateBody( body, segment, dt, getMaxRotDTheta( dt ) );
        setNetForce( computeNetForce( model, segment ) );
        super.stepInTime( body, dt ); //apply newton's laws       

        segment = getSegment( body );
        if( segment == null ) {
            flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
            debugState( "Fly off surface 2" );
            return;
        }
        Body preBounce = body.copyState();
        setupBounce( body, segment );
        debug( "bounce", preBounce, body );
        AbstractVector2D dx = body.getPositionVector().getSubtractedInstance( new Vector2D.Double( originalState.getPosition() ) );
        double frictiveWork = bounced ? 0.0 : Math.abs( getFrictionForce( model, segment, body ).dot( dx ) );
        if( frictiveWork == 0 && body.getFrictionCoefficient() > 0 ) {
            new RuntimeException( "weird friction values" ).printStackTrace();
        }
        body.addThermalEnergy( frictiveWork );
        debug( "newton or maybe setup bounce", originalState, body );
//        if( bounced && !grabbed && !lastGrabState ) {
        if( bounced && !grabbed ) {
            handleBounceAndFlyOff( body, model, dt, originalState );
            debugState( "Handle bounce & Fly off" );
            return;
        }
        else {
            double v = body.getVelocity().dot( segment.getUnitNormalVector() );
            if( v > 0.01 ) {
                flyOffSurface( body, model, dt, originalState.getTotalEnergy() );
                if( getCollisionSegment( body ) != null ) {
                    body.setSplineMode( model, spline );
                }
                debugState( "Fly off surface 3" );
                return;
            }
            if( getSegment( body ) != getCollisionSegment( body ) || model.isSplineUserControlled() ) {
                //todo choose whether this smoothness is desired for the simulation, or if Double.POSITIVE_INFINITY is better
//                rotateBody( body, segment, dt, Math.PI / 64 );
//                rotateBody( body, segment, dt, Math.PI / 64 );
                rotateBody( body, segment, dt, Double.POSITIVE_INFINITY );
                debug( "We just rotated body", originalState, body );
                setBottomAtZero( segment, body );//can we find another implementation of this that preserves energy better?
                debug( "set bottom to zero", originalState, body );
            }
            segment = getSegment( body );//need to find our new segment after rotation.
            if( segment == null ) {
                return;
            }

            if( frictiveWork == 0 ) {//can't manipulate friction, so just modify v/h
                new EnergyConserver().fixEnergy( body, originalState.getTotalEnergy() );//todo shouldn't this be origState.getTotalEnergy()?
                debug( "fixenergy for frictive work=0", originalState, body );
            }
            else {
                patchEnergyInclThermal( frictiveWork, model, body, originalState.getTotalEnergy(), originalState );
                debug( "patch energy incl thermal", originalState, body );
            }
        }
        debug2( "after everything", originalState.getTotalEnergy(), body );
        //we want the total energy to be origState.getTotalEnergy().
        //currently the energy is new State(model,body).getTotalEnergy().
        //we can easily edit the mechanical energy...

        if( Math.abs( originalState.getTotalEnergy() - new State( body ).getTotalEnergy() ) > 0 ) {
            new EnergyConserver().fixEnergy( body, originalState.getTotalEnergy() - new State( body ).getHeat() );
        }
        debug2( "after everything2", originalState.getTotalEnergy(), body );

        lastGrabState = grabbed;
        lastSegment = segment;
        stepFinished();
    }

    private void debug( String type, Body before, Body after ) {
        if( before.getEnergyDifferenceAbs( after ) > 100 ) {
            System.out.println( type + " " + before.getEnergyDifferenceAbs( after ) );
        }
    }

    private void debugState( String s ) {
        if( debugState ) {
            System.out.println( s );
        }
    }

    private boolean okayToStop( EnergyConservationModel model, Segment segment, Body body ) {
        double sum = 0;
        for( int i = 0; i < speedHistory.size(); i++ ) {
            java.lang.Double aDouble = (java.lang.Double)speedHistory.get( i );
            sum += aDouble.doubleValue();
        }
        double avgSpeed = 1.0 / speedHistory.size() * sum;
//        System.out.println( "avgSpeed="+avgSpeed+", getnetforce.getm="+getNetForce().getMagnitude() );
//        System.out.println( "getNetForce().getMagnitude() = " + computeNetForce( model, segment ).getMagnitude() );
//        return body.getFrictionCoefficient() > 0 && avgSpeed < 1 && getNetForce().getMagnitude() < 100&&speedHistory.size()==30;
        return body.getFrictionCoefficient() > 0 && avgSpeed < 0.2 && getNetForce().getMagnitude() < 100 && speedHistory.size() == 30 && !body.isUserControlled() && !model.isSplineUserControlled();
    }

    public void init( Body body ) {
        body.convertToSpline();
    }

    private void stepFinished() {
        errorYetThisStep = false;
    }

    private void stepStarted() {
        errorYetThisStep = false;
    }

    private void patchEnergyInclThermal( double frictiveWork, EnergyConservationModel model, Body body, double desiredEnergy, State origState ) {
//        originalState=model.getDesiredEnergy(body);
        //modify the frictive work slightly so we don't have to account for all error energy in V and H.
        double allowedToModifyHeat = Math.abs( frictiveWork * 0.2 );

        debug( "Added thermal energy", origState, body );
        double finalEnergy = body.getMechanicalEnergy() + body.getThermalEnergy();
        double energyError = finalEnergy - desiredEnergy;

        double energyErrorSign = MathUtil.getSign( energyError );
        if( Math.abs( energyError ) > Math.abs( allowedToModifyHeat ) ) {//big problem
            body.addThermalEnergy( allowedToModifyHeat * energyErrorSign * -1 );

            double desiredMechEnergy = desiredEnergy - body.getThermalEnergy();
            new EnergyConserver().fixEnergy( body, desiredMechEnergy );//todo enhance energy conserver with thermal changes.
            debug( "FixEnergy", origState, body );
            //This may be causing other problems
        }
        else {
            body.addThermalEnergy( -energyError );
            debug( "AddThermalEnergy", origState, body );
        }
    }

    private Segment getSegment( Body body ) {
        Segment seg = getAttachmentSegment( body );
        if( seg == null ) {
            seg = getCollisionSegment( body );
        }
        return seg;
    }

    private Segment getCollisionSegment( Body body ) {
        try {
            return new SplineLogic( body ).guessSegment( spline );
        }
        catch( NullIntersectionException e ) {
            return null;
        }
    }

    //maybe problem is from discontinuity here
    //this assumes all segments are the same size
    private Segment getAttachmentSegment( Body body ) {
        Point2D.Double attachPoint = body.getAttachPoint();
        SegmentPath segPath = spline.getSegmentPath();
        double bestDist = Double.POSITIVE_INFINITY;
        Segment bestSeg = null;
        for( int i = 0; i < segPath.numSegments(); i++ ) {
            Segment seg = segPath.segmentAt( i );
            double dist = attachPoint.distance( seg.getCenter2D() );
            if( lastSegment != null ) {  //prefer a nearby segment
                int indexOffset = Math.abs( seg.getID() - lastSegment.getID() );
                if( indexOffset > 5 ) {
                    dist += indexOffset * 5.0;
                }
            }
            if( dist < bestDist ) {
                bestSeg = seg;
                bestDist = dist;
            }
        }

        if( bestDist > body.getWidth() / 2 ) {//too far away
//        if( bestDist > body.getWidth() / 6) {//too far away
            return null;
        }
        return bestSeg;
    }

    private void handleBounceAndFlyOff( Body body, EnergyConservationModel model, double dt, State originalState ) {
//        System.out.println( "DIDBOUNCE" );
        //coeff of restitution
        double coefficientOfRestitution = body.getCoefficientOfRestitution();
        double finalVelocity = coefficientOfRestitution * body.getVelocity().getMagnitude();
        AbstractVector2D vec = body.getVelocity().getInstanceOfMagnitude( finalVelocity );
        double initKE = body.getKineticEnergy();
        body.setVelocity( vec );
        double finalKE = body.getKineticEnergy();
        if( finalKE > initKE ) {
            System.out.println( "Something is very wrong." );
        }

        double dE = initKE - finalKE;
        body.addThermalEnergy( dE );

        flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() - dE );
    }

    //just kill the perpendicular part of velocity, if it is through the track.
    // this should be lost to friction or to a bounce.
    private void setupBounce( Body body, Segment segment ) {
        this.bounced = false;
        this.grabbed = false;

        double perpendicularVelocity = body.getVelocity().dot( segment.getUnitNormalVector() );
        System.out.println( "perpendicularVelocity = " + perpendicularVelocity );
        if( perpendicularVelocity >= -0.1 ) {//fall off
            System.out.println( "FALL OFF" );
            grabbed = false;
            bounced = false;
        }
        else if( perpendicularVelocity < -bounceThreshold ) {
            System.out.println( "perp=" + perpendicularVelocity + ", bounce threshold=" + bounceThreshold );
            System.out.println( "BOUNCE" );
            bounced = true;
            grabbed = false;
            AbstractVector2D newVelocity = Vector2D.Double.parseAngleAndMagnitude( body.getVelocity().getMagnitude(), segment.getUnitNormalVector().getAngle() );
            body.setVelocity( newVelocity );
        }
        else {
            System.out.println( "GRAB" );
            bounced = false;
            grabbed = true;
            double angleOffset = body.getVelocity().dot( segment.getUnitDirectionVector() ) < 0 ? Math.PI : 0;
            AbstractVector2D newVelocity = Vector2D.Double.parseAngleAndMagnitude( body.getVelocity().getMagnitude(), segment.getAngle() + angleOffset );
            body.setVelocity( newVelocity );
        }
    }

    //just kill the perpendicular part of velocity, if it is through the track.
    // this should be lost to friction or to a bounce.
//    private void setupBounce( Body body, Segment segment ) {
//        Body origState=body.copyState();
////        System.out.println( "PREBOUNCEbody.getVelocity() = " + body.getVelocity() );
//        RVector2D origVector = new RVector2D( body.getVelocity(), segment.getUnitDirectionVector() );
//        this.bounced = false;
//        this.grabbed = false;
//
//        double originalPerpVel = origVector.getPerpendicular();
//        if( origVector.getPerpendicular() < 0 ) {//velocity is through the segment
//            if( Math.abs( origVector.getPerpendicular() ) > bounceThreshold ) {//bounce
//                origVector.setPerpendicular( Math.abs( origVector.getPerpendicular() ) );
//                bounced = true;
//                System.out.println( "A" );
//            }
//            else {//grab
//                origVector.setPerpendicular( 0.0 );
//                grabbed = true;
//                System.out.println( "B" );
//            }
//        }
//        if( !lastGrabState && grabbed ) {
//            if( origVector.getParallel() >= 0 ) {//try to conserve velocity, so that the EnergyConserver doesn't have
//                //to make up for it all in dHeight.
//                origVector.setParallel( origVector.getParallel() + Math.abs( originalPerpVel ) );
//                System.out.println( "C" );
//            }
//            else if( origVector.getParallel() < 0 ) {
//                origVector.setParallel( origVector.getParallel() - Math.abs( originalPerpVel ) );
//                System.out.println( "D" );
//            }
//        }
//        body.setVelocity( origVector.toCartesianVector() );
////        System.out.println( "POSTBOUNCE.getVelocity() = " + body.getVelocity() );
//        if (origState.getEnergyDifferenceAbs( body )>1000){
//            System.out.println( "?Error in Setup bounce: "+origState.getEnergyDifferenceAbs( body ) );
//        }
//    }

    private void rotateBody( Body body, Segment segment, double dt, double maxRotationDTheta ) {
        double bodyAngle = body.getAttachmentPointRotation();
        double dA = segment.getAngle() - bodyAngle;
        if( dA > Math.PI ) {
            dA -= Math.PI * 2;
        }
        else if( dA < -Math.PI ) {
            dA += Math.PI * 2;
        }
        if( dA > maxRotationDTheta ) {
            dA = maxRotationDTheta;
        }
        else if( dA < -maxRotationDTheta ) {
            dA = -maxRotationDTheta;
        }
        body.rotateAboutAttachmentPoint( dA );
        this.lastDA = dA;
    }

    private double getMaxRotDTheta( double dt ) {
        return Math.PI / 16 * dt / 0.2;
    }

    private void flyOffSurface( Body body, EnergyConservationModel model, double dt, double origTotalEnergy ) {
        double vy = body.getVelocity().getY();
        double timeToReturnToThisHeight = model.getGravity() != 0 ? Math.abs( 2 * vy / model.getGravity() ) : 1000;

        double numTimeSteps = timeToReturnToThisHeight / dt;
        double dTheta = Math.PI * 2 / numTimeSteps / dt;
        if( timeToReturnToThisHeight > flipTimeThreshold ) {
            body.setFreeFallRotationalVelocity( dTheta );
        }
        else {
            double rot = lastDA;
            if( rot > getMaxRotDTheta( dt ) ) {
                rot = getMaxRotDTheta( dt );
            }
            if( rot < -getMaxRotDTheta( dt ) ) {
                rot = -getMaxRotDTheta( dt );
            }
            body.setFreeFallRotationalVelocity( rot );
        }
        body.setFreeFallMode();
        super.setNetForce( new Vector2D.Double( 0, 0 ) );
        super.stepInTime( body, dt );
        new EnergyConserver().fixEnergy( body, origTotalEnergy );
    }

    private void setBottomAtZero( Segment segment, Body body ) {
        double overshoot = getOvershootInSegment( segment, body );
        AbstractVector2D tx = segment.getUnitNormalVector().getScaledInstance( -overshoot );
        body.translate( tx.getX(), tx.getY() );
    }

    private double getOvershootInSegment( Segment segment, Body body ) {
        double dist = new Line2D.Double( segment.getP0(), segment.getP1() ).ptLineDist( body.getAttachPoint() );
        Vector2D.Double x = new Vector2D.Double( segment.getCenter2D(), body.getAttachPoint() );
        dist *= MathUtil.getSign( x.dot( segment.getUnitNormalVector() ) );
        return dist;
    }

    private AbstractVector2D computeNetForce( EnergyConservationModel model, Segment segment ) {
        AbstractVector2D[] forces = new AbstractVector2D[]{
                getGravityForce( model ),
                getNormalForce( model, segment, body ),
                getThrustForce(),
                getFrictionForce( model, segment, body )
        };
        Vector2D.Double sum = new Vector2D.Double();
        for( int i = 0; i < forces.length; i++ ) {
            AbstractVector2D force = forces[i];
            sum.add( force );
        }
        if( Double.isNaN( sum.getX() ) ) {
            System.out.println( "nan" );
        }
        return sum;
    }

    private AbstractVector2D getGravityForce( EnergyConservationModel model ) {
        return new Vector2D.Double( 0, getFGy( model ) );
    }

    private AbstractVector2D getThrustForce() {
        return body.getThrust();
    }

    private double getFGy( EnergyConservationModel model ) {
        return model.getGravity() * body.getMass();
    }

    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment, Body body ) {
        double fricMag = getFrictionCoefficient() * getNormalForce( model, segment, body ).getMagnitude();
        if( body.getVelocity().getMagnitude() > 0 ) {
            return body.getVelocity().getInstanceOfMagnitude( -fricMag * 5 );//todo fudge factor of five
        }
        else {
            return new ImmutableVector2D.Double( 0, 0 );
        }
    }

    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment, Body body ) {
        if( body.getVelocity().dot( segment.getUnitNormalVector() ) <= 0.1 ) {
//        if( segment.getUnitNormalVector().dot( getGravityForce( model ) ) < 0 ) {//todo is this correct?
            return segment.getUnitNormalVector().getScaledInstance( getFGy( model ) * Math.cos( segment.getAngle() ) );
        }
        else {
            return new ImmutableVector2D.Double();
        }
    }

    private double getFrictionCoefficient() {
        return body.getFrictionCoefficient();
    }

    public AbstractSpline getSpline() {
        return spline;
    }

    public EnergyConservationModel getEnergyModel() {
        return model;
    }
}
