/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
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

    public FreeSplineMode( AbstractSpline spline, Body body ) {
        this.spline = spline;
        this.body = body;
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        Point2D.Double origPosition = body.getPosition();
        double origTotalEnergy = model.getTotalEnergy( body );
        EnergyDebugger.stepStarted( model, body, dt );
        double position = 0;
        try {
            position = getPositionOnSpline( body );
        }
        catch( NullIntersectionException e ) {
            flyOffSurface( body, model, dt, origTotalEnergy );
            return;
        }
        lastScalarPosition = position;

        Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.
        rotateBody( body, segment );

        AbstractVector2D netForce = computeNetForce( model, segment );
        System.out.println( "netForce = " + netForce );
        super.setNetForce( netForce );
        super.stepInTime( model, body, dt );

        //just kill the perpendicular part of velocity, if it is through the track.
        // this should be lost to friction.
        //or to a bounce.
        handleBounce( body, segment );
        double dx = body.getPosition().distance( origPosition );
        double dHeat = getFrictionForce( model, segment ).getMagnitude() * dx;
        System.out.println( "dHeat = " + dHeat );
        new EnergyConserver().fixEnergy( model, body, origTotalEnergy - dHeat );
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

    private void handleBounce( Body body, Segment segment ) {
        RVector2D origVector = new RVector2D( body.getVelocity(), segment.getUnitDirectionVector() );
        double bounceThreshold = 30;
        boolean bounced = false;
        boolean grabbed = false;
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
        if( lastGrabState == false && grabbed == true ) {

            if( origVector.getParallel() > 0 ) {//try to conserve velocity, so that the EnergyConserver doesn't have
                //to make up for it all in dHeight.
                origVector.setParallel( origVector.getParallel() + Math.abs( originalPerpVel ) );
            }
            else if( origVector.getParallel() < 0 ) {
                origVector.setParallel( origVector.getParallel() - Math.abs( originalPerpVel ) );
            }
        }
        lastGrabState = grabbed;
        Vector2D.Double newVelocity = origVector.toCartesianVector();

        EC3Debug.debug( "newVelocity = " + newVelocity );
        body.setVelocity( newVelocity );

        if( bounced || grabbed ) {
            //set bottom at zero.
            setBottomAtZero( segment, body );
        }
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
        double vy = body.getVelocity().getY();
        double timeToReturnToThisHeight = Math.abs( 2 * vy / model.getGravity() );
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
        double bodyYPerp = segment.getUnitNormalVector().dot( body.getPositionVector() );
        double segmentYPerp = segment.getUnitNormalVector().dot( new ImmutableVector2D.Double( segment.getCenter2D() ) );
        double overshoot = -( bodyYPerp - segmentYPerp - body.getHeight() / 2.0 ) + segment.getThickness() / 2;
        EC3Debug.debug( "overshoot = " + overshoot );
        overshoot -= 1;//hang in there
        if( overshoot > 0 ) {
            AbstractVector2D tx = segment.getUnitNormalVector().getScaledInstance( overshoot );
            body.translate( tx.getX(), tx.getY() );
        }
    }

    private AbstractVector2D computeNetForce( EnergyConservationModel model, Segment segment ) {
        AbstractVector2D[] forces = new AbstractVector2D[]{
            getGravityForce( model ),
            getNormalForce( model, segment ),
            getThrustForce(), getFrictionForce( model, segment )};
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
        double fgy = model.getGravity() * body.getMass();
        return fgy;
    }

    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment ) {
        if( body.getVelocity().getMagnitude() > 0.01 ) {
            double fricMag = getFrictionCoefficient() * getNormalForce( model, segment ).getMagnitude();
            AbstractVector2D friction = body.getVelocity().getScaledInstance( -fricMag );
            return friction;
        }
        else {
            return new ImmutableVector2D.Double();
        }
    }

    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment ) {
        return segment.getUnitNormalVector().getScaledInstance( -getFGy( model ) * Math.cos( segment.getAngle() ) );
    }

    private double getFrictionCoefficient() {
        return 0.02;//todo parameterize
    }

    public AbstractSpline getSpline() {
        return spline;
    }

}
