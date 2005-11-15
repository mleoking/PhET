/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;

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
    private boolean lastPositionSet = false;
    private double lastScalarPosition = 0;
    private boolean lastGrabState = false;
    private boolean bounced = false;
    private boolean grabbed = false;

    public FreeSplineMode( AbstractSpline spline, Body body ) {
        this.spline = spline;
        this.body = body;
    }

    private static class State {
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

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        State originalState = new State( model, body );
        EnergyDebugger.stepStarted( model, body, dt );
        try {
            double position = getPositionOnSpline( body );
            lastScalarPosition = position;
            Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.
//            System.out.println( "segment = " + segment );
//            System.out.println( "position =\t" + position );
            rotateBody( body, segment );

            AbstractVector2D netForce = computeNetForce( model, segment );
            super.setNetForce( netForce );
            super.stepInTime( model, body, dt ); //apply newton's laws

            handleBounceVelocities( body, segment );//Why is this happening after newton?
            if( bounced || grabbed ) {
                setBottomAtZero( segment, body );
            }

            if( bounced && !grabbed && !lastGrabState ) {
                handleBounceAndFlyOff( body, model, dt, originalState );
            }
            else {
                AbstractVector2D dx = body.getPositionVector().getSubtractedInstance( new Vector2D.Double( originalState.getPosition() ) );
                double frictiveWork = bounced ? 0.0 : Math.abs( getFrictionForce( model, segment ).dot( dx ) );
                if( frictiveWork == 0 ) {//can't manipulate friction, so just modify v/h
                    new EnergyConserver().fixEnergy( model, body, originalState.getMechanicalEnergy() - frictiveWork );
//                    setBottomAtZero( segment, body );
                }
                else {
                    patchEnergyInclThermal( frictiveWork, model, body, originalState );
                }
            }
            lastGrabState = grabbed;

        }
        catch( NullIntersectionException e ) {
            flyOffSurface( body, model, dt, originalState.getMechanicalEnergy() );
            System.out.println( "Fly off surface!" );
            return;
        }
    }

    private void patchEnergyInclThermal( double frictiveWork, EnergyConservationModel model, Body body, State originalState ) {
        //modify the frictive work slightly so we don't have to account for all error energy in V and H.
        double allowedToModifyHeat = Math.abs( frictiveWork * 0.75 );
        model.addThermalEnergy( frictiveWork );
        double finalTotalEnergy1 = model.getTotalMechanicalEnergy( body ) + model.getThermalEnergy();
        double energyError = finalTotalEnergy1 - originalState.getTotalEnergy();
        System.out.println( "energyError " + energyError + ", frictiveWork=" + frictiveWork );

        double energyErrorSign = MathUtil.getSign( energyError );
        if( Math.abs( energyError ) > Math.abs( allowedToModifyHeat ) ) {//big problem
            System.out.println( "error was too large to fix only with heat" );
            model.addThermalEnergy( allowedToModifyHeat * energyErrorSign * -1 );

            double origTotalEnergyAll = originalState.getMechanicalEnergy() + originalState.getHeat();
            double desiredMechEnergy = origTotalEnergyAll - model.getThermalEnergy();
            new EnergyConserver().fixEnergy( model, body, desiredMechEnergy );//todo enhance energy conserver with thermal changes.
        }
        else {
            System.out.println( "Error was okay to fix with heat only." );
            model.addThermalEnergy( -energyError );
        }
    }

    private void handleBounceAndFlyOff( Body body, EnergyConservationModel model, double dt, State originalState ) {
        System.out.println( "DIDBOUNCE" );
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

    private double getPositionOnSpline( Body body ) throws NullIntersectionException {
        double position;
        if( lastPositionSet ) {
            position = new SplineLogic( body ).guessPositionAlongSpline( getSpline(), lastScalarPosition );
            lastPositionSet = true;
        }
        else {
            position = new SplineLogic( body ).guessPositionAlongSpline( getSpline() );
            lastPositionSet = true;
        }
        return position;
    }

    //just kill the perpendicular part of velocity, if it is through the track.
    // this should be lost to friction or to a bounce.
    private boolean handleBounceVelocities( Body body, Segment segment ) {
        RVector2D origVector = new RVector2D( body.getVelocity(), segment.getUnitDirectionVector() );
        double bounceThreshold = 30;
//        double bounceThreshold = 5;
        this.bounced = false;
        this.grabbed = false;
        double originalPerpVel = origVector.getPerpendicular();
        if( origVector.getPerpendicular() < 0 ) {//velocity is through the segment
            if( Math.abs( origVector.getPerpendicular() ) > bounceThreshold ) {//bounce
                origVector.setPerpendicular( Math.abs( origVector.getPerpendicular() ) );
                bounced = true;
            }
            else {//grab
                origVector.setPerpendicular( 0.0 );
                grabbed = true;
            }
        }
        if( !lastGrabState && grabbed ) {
            if( origVector.getParallel() > 0 ) {//try to conserve velocity, so that the EnergyConserver doesn't have
                //to make up for it all in dHeight.
                origVector.setParallel( origVector.getParallel() + Math.abs( originalPerpVel ) );
            }
            else if( origVector.getParallel() < 0 ) {
                origVector.setParallel( origVector.getParallel() - Math.abs( originalPerpVel ) );
            }
        }

        Vector2D.Double newVelocity = origVector.toCartesianVector();

        EC3Debug.debug( "newVelocity = " + newVelocity );
        body.setVelocity( newVelocity );

        return bounced;
    }

    private void rotateBody( Body body, Segment segment ) {
        double bodyAngle = body.getAngle();
        double dA = segment.getAngle() - bodyAngle;
//        System.out.println( "seg=" + segment.getAngle() + ", body=" + bodyAngle + ", da=" + dA );

        if( dA > Math.PI ) {
            dA -= Math.PI * 2;
        }
        else if( dA < -Math.PI ) {
            dA += Math.PI * 2;
        }
        double rotationDTheta = Math.PI / 16;
        if( dA > rotationDTheta ) {
            dA = rotationDTheta;
        }
        else if( dA < -rotationDTheta ) {
            dA = -rotationDTheta;
        }
        body.rotate( dA );
        this.lastDA = dA;
    }

    private void flyOffSurface( Body body, EnergyConservationModel model, double dt, double origTotalEnergy ) {
        System.out.println( "FreeSplineMode.flyOffSurface" );
        double vy = body.getVelocity().getY();
        double timeToReturnToThisHeight = model.getGravity() != 0 ? Math.abs( 2 * vy / model.getGravity() ) : 1000;

        double numTimeSteps = timeToReturnToThisHeight / dt;
        double dTheta = Math.PI * 2 / numTimeSteps / dt;

        if( timeToReturnToThisHeight > 10 ) {
            body.setFreeFallRotation( -dTheta );
            System.out.println( "Flipping!" );
        }
        else {
            body.setFreeFallRotation( lastDA );
        }
        body.setFreeFallMode();
        super.setNetForce( new Vector2D.Double( 0, 0 ) );
        super.stepInTime( model, body, dt );
        new EnergyConserver().fixEnergy( model, body, origTotalEnergy );
    }

    private void setBottomAtZero( Segment segment, Body body ) {
        double overshoot = getDepthInSegment( segment, body );
        EC3Debug.debug( "overshoot = " + overshoot );
//        System.out.println( "ORIG:overshoot = " + overshoot );
        overshoot -= 5;
        if( overshoot > 0 ) {
            AbstractVector2D tx = segment.getUnitNormalVector().getScaledInstance( overshoot );
            body.translate( tx.getX(), tx.getY() );
        }
//        System.out.println( "FIN: getDepthInSegment( segment, body ) = " + getDepthInSegment( segment, body ) );
    }

    private double getDepthInSegment( Segment segment, Body body ) {
        double bodyYPerp = segment.getUnitNormalVector().dot( body.getPositionVector() );
        double segmentYPerp = segment.getUnitNormalVector().dot( new ImmutableVector2D.Double( segment.getCenter2D() ) );
        double overshoot = -( bodyYPerp - segmentYPerp - body.getHeight() / 2.0 ) + segment.getThickness() / 2;
        return overshoot;
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
        AbstractVector2D gravityForce = new Vector2D.Double( 0, getFGy( model ) );
        return gravityForce;
    }

    private AbstractVector2D getThrustForce() {
        return body.getThrust();
    }

    private double getFGy( EnergyConservationModel model ) {
        return model.getGravity() * body.getMass();
    }

    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment ) {
//        double fricMag = getFrictionCoefficient() * getNormalForce( model, segment ).getMagnitude();
//        AbstractVector2D friction = segment.getUnitDirectionVector().getInstanceOfMagnitude( fricMag );
//        if( friction.dot( body.getVelocity() ) < 0 ) {
//            friction = friction.getScaledInstance( -1 );
//        }
//        return friction;
        double fricMag = getFrictionCoefficient() * getNormalForce( model, segment ).getMagnitude();
//        System.out.println( "body.getVelocity().getMagnitude() = " + body.getVelocity().getMagnitude() );
        AbstractVector2D friction = body.getVelocity().getScaledInstance( -fricMag );
        return friction;
    }

    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment ) {
        if( segment.getUnitNormalVector().dot( getGravityForce( model ) ) < 0 ) {
            AbstractVector2D normalForce = segment.getUnitNormalVector().getScaledInstance( getFGy( model ) * Math.cos( segment.getAngle() ) );
            return normalForce;
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

}
