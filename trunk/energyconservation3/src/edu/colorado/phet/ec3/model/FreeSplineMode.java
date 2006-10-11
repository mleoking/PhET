/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;
import edu.colorado.phet.ec3.model.spline.SegmentPath;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:54 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeSplineMode extends ForceMode {
    private AbstractSpline spline;
    private Body body;
    private double lastDA;
    private boolean lastGrabState = false;
    private boolean bounced = false;
    private boolean grabbed = false;
    private Segment lastSegment;
    private double bounceThreshold = 4;
    private final double flipTimeThreshold = 1.0;

    public FreeSplineMode( AbstractSpline spline, Body body ) {
        this.spline = spline;
        this.body = body;
    }

    public static class State {
        private EnergyConservationModel model;
        private Body body;

        public State( EnergyConservationModel model, Body body ) {
            this.model = model.copyState();
            this.body = body.copyState();
        }

        public EnergyConservationModel getModel() {
            return model;
        }

        public Body getBody() {
            return body;
        }

        public Point2D.Double getPosition() {
            return body.getPosition();
        }

        public double getMechanicalEnergy() {
            return model.getMechanicalEnergy( body );
        }

        public double getTotalEnergy() {
            return model.getTotalMechanicalEnergy( body ) + model.getThermalEnergy();
        }

        public double getHeat() {
            return model.getThermalEnergy();
        }
    }

    public void debug( String type, double desiredEnergy, EnergyConservationModel model, Body body ) {
        double dE = new State( model, body ).getTotalEnergy() - desiredEnergy;
        if( Math.abs( dE ) > 0.1 ) {
            System.out.println( type + ", dE = " + dE );
        }
    }

    public void debug2( String type, double desiredEnergy, EnergyConservationModel model, Body body ) {
        double dE = new State( model, body ).getTotalEnergy() - desiredEnergy;
        if( Math.abs( dE ) > 1E-6 ) {
            System.out.println( type + ", dE = " + dE );
        }
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
//        System.out.println( "body.getAttachPoint() = " + body.getAttachPoint() );
        State originalState = new State( model, body );
        Segment segment = getSegment( body );
        if( segment == null ) {
            flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
            return;
        }
        rotateBody( body, segment, dt, getMaxRotDTheta( dt ) );
        setNetForce( computeNetForce( model, segment ) );
        super.stepInTime( model, body, dt ); //apply newton's laws
//        double emergentSpeed=(body.getPositionVector().getMagnitude()-originalState.getBody().getPositionVector().getMagnitude())/dt;
//        System.out.println( "emergentSpeed = " + emergentSpeed +", speed="+body.getSpeed());

//        if( getFrictionForce( model, segment ).getMagnitude() > 0 &&body.getVelocity().getMagnitude()<2) {
//            convertVelocityToThermal( model, originalState, body );
//        }

        segment = getSegment( body );
        if( segment == null ) {
            flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
            return;
        }
        setupBounce( body, segment );
        AbstractVector2D dx = body.getPositionVector().getSubtractedInstance( new Vector2D.Double( originalState.getPosition() ) );
        double frictiveWork = bounced ? 0.0 : Math.abs( getFrictionForce( model, segment ).dot( dx ) );
        model.addThermalEnergy( frictiveWork );
//                debug( "newton's laws", originalState.getTotalEnergy(), model, body );
        debug( "newton or maybe setup bounce", originalState.getTotalEnergy(), model, body );
        if( bounced && !grabbed && !lastGrabState ) {
            handleBounceAndFlyOff( body, model, dt, originalState );
        }
        else {
            double v = body.getVelocity().dot( segment.getUnitNormalVector() );
            if( v > 0.01 ) {
                flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
                return;
            }
            rotateBody( body, segment, dt, Double.POSITIVE_INFINITY );
            segment = getSegment( body );//need to find our new segment after rotation.
            if( segment == null ) {
                return;
            }
            debug( "We just rotated body", originalState.getTotalEnergy(), model, body );
            setBottomAtZero( segment, body );//can we find another implementation of this that preserves energy better?
            debug( "set bottom to zero", originalState.getTotalEnergy(), model, body );

            if( frictiveWork == 0 ) {//can't manipulate friction, so just modify v/h
                new EnergyConserver().fixEnergy( model, body, originalState.getMechanicalEnergy() );//todo shouldn't this be origState.getTotalEnergy()?
            }
            else {
                patchEnergyInclThermal( frictiveWork, model, body, originalState.getTotalEnergy() );
            }
        }
        debug2( "after everything", originalState.getTotalEnergy(), model, body );

        lastGrabState = grabbed;
        lastSegment = segment;
    }

//    private void convertVelocityToThermal( EnergyConservationModel model, State originalState, Body body ) {
//        double ke0 = body.getKineticEnergy();
//        body.setVelocity( body.getVelocity().getScaledInstance( 0.5 ) );
//        double ke = body.getKineticEnergy();
//        model.addThermalEnergy( Math.abs( ke - ke0 ) );
////        body.setVelocity( body.getVelocity().getScaledInstance( 1.0 / Math.sqrt( A ) ) );
//    }

    private void patchEnergyInclThermal( double frictiveWork, EnergyConservationModel model, Body body, double desiredEnergy ) {
//        originalState=model.getDesiredEnergy(body);
        //modify the frictive work slightly so we don't have to account for all error energy in V and H.
        double allowedToModifyHeat = Math.abs( frictiveWork * 0.2 );

        debug( "Added thermal energy", desiredEnergy, model, body );
        double finalEnergy = model.getTotalMechanicalEnergy( body ) + model.getThermalEnergy();
        double energyError = finalEnergy - desiredEnergy;

        double energyErrorSign = MathUtil.getSign( energyError );
        if( Math.abs( energyError ) > Math.abs( allowedToModifyHeat ) ) {//big problem
            model.addThermalEnergy( allowedToModifyHeat * energyErrorSign * -1 );

            double desiredMechEnergy = desiredEnergy - model.getThermalEnergy();
            new EnergyConserver().fixEnergy( model, body, desiredMechEnergy );//todo enhance energy conserver with thermal changes.
            debug( "FixEnergy", desiredEnergy, model, body );
            //This may be causing other problems
        }
        else {
            model.addThermalEnergy( -energyError );
            debug( "AddThermalEnergy", desiredEnergy, model, body );
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

        if( bestDist > body.getWidth() / 2.0 ) {//too far away
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
        model.addThermalEnergy( dE );

        flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() - dE );
    }

    //just kill the perpendicular part of velocity, if it is through the track.
    // this should be lost to friction or to a bounce.
    private void setupBounce( Body body, Segment segment ) {
        this.bounced = false;
        this.grabbed = false;

        double angleOffset = body.getVelocity().dot( segment.getUnitDirectionVector() ) < 0 ? Math.PI : 0;
        AbstractVector2D newVelocity = Vector2D.Double.parseAngleAndMagnitude( body.getVelocity().getMagnitude(), segment.getAngle() + angleOffset );
        body.setVelocity( newVelocity );
    }

    private void rotateBody( Body body, Segment segment, double dt, double maxRotationDTheta ) {
        double bodyAngle = body.getAngle();
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
        body.rotate( dA );
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
            body.setFreeFallRotation( dTheta );
        }
        else {
            double rot = lastDA;
            if( rot > getMaxRotDTheta( dt ) ) {
                rot = getMaxRotDTheta( dt );
            }
            if( rot < -getMaxRotDTheta( dt ) ) {
                rot = -getMaxRotDTheta( dt );
            }
            body.setFreeFallRotation( rot );
        }
        body.setFreeFallMode();
        super.setNetForce( new Vector2D.Double( 0, 0 ) );
        super.stepInTime( model, body, dt );
        new EnergyConserver().fixEnergy( model, body, origTotalEnergy );
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
                getNormalForce( model, segment ),
                getThrustForce(),
                getFrictionForce( model, segment )
        };
        Vector2D.Double sum = new Vector2D.Double();
        for( int i = 0; i < forces.length; i++ ) {
            AbstractVector2D force = forces[i];
            sum.add( force );
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

    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment ) {
        double fricMag = getFrictionCoefficient() * getNormalForce( model, segment ).getMagnitude();
        return body.getVelocity().getInstanceOfMagnitude( -fricMag * 5 );
    }

    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment ) {
//        if( segment.getUnitNormalVector().dot( getGravityForce( model ) ) < 0 ) {//todo is this correct?
        return segment.getUnitNormalVector().getScaledInstance( getFGy( model ) * Math.cos( segment.getAngle() ) );
//        }
//        else {
//            return new ImmutableVector2D.Double();
//        }
    }

    private double getFrictionCoefficient() {
        return body.getFrictionCoefficient();
    }

    public AbstractSpline getSpline() {
        return spline;
    }

}
