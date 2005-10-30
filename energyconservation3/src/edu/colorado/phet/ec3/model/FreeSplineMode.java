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

    static {
        System.out.println( "origKE\torigPE\torigTot" );
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        Point2D.Double origPosition = body.getPosition();
        double origKE = body.getKineticEnergy();
        double origPE = model.getPotentialEnergy( body );
        double origTotalEnergy = model.getTotalEnergy( body );
//        System.out.println( origKE + "\t" + origPE + "\t" + origTotalEnergy );
        EnergyDebugger.stepStarted( model, body, dt );
        double position = 0;
        try {
            if( lastPositionSet ) {
                position = new SplineLogic( body ).guessPositionAlongSpline( getSpline(), lastScalarPosition );
                lastPositionSet = true;
            }
            else {
                position = new SplineLogic( body ).guessPositionAlongSpline( getSpline() );
                lastPositionSet = true;
            }

        }
        catch( NullIntersectionException e ) {
//            e.printStackTrace();
            leaveSurface( body, model, dt, origTotalEnergy );
            return;
        }
        lastScalarPosition = position;

        Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.
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
//            body.setAngle( segment.getAngle() );//todo rotations.

        AbstractVector2D netForce = computeNetForce( model, segment );
        System.out.println( "netForce = " + netForce );
        super.setNetForce( netForce );
//        AbstractVector2D origVel = new ImmutableVector2D.Double( body.getVelocity() );
        super.stepInTime( model, body, dt );
//        AbstractVector2D finalVel = new ImmutableVector2D.Double( body.getVelocity() );
//        if( origVel.getX() * finalVel.getX() < 0 ) {
//            body.setVelocity( 0, body.getVelocity().getY() );
//        }
//        if( origVel.getY() * finalVel.getY() < 0 ) {
//            body.setVelocity( body.getVelocity().getX(), 0 );
//        }
        //just kill the perpendicular part of velocity, if it is through the track.
        // this should be lost to friction.
        //or to a bounce.

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
//        if( !grabbed ) {
        double dx = body.getPosition().distance( origPosition );
//        System.out.println( "dx = " + dx );
        double dHeat = getFrictionForce( model, segment ).getMagnitude() * dx;

        new EnergyConserver().fixEnergy( model, body, origTotalEnergy, dHeat );
//        }
    }

    private void leaveSurface( Body body, EnergyConservationModel model, double dt, double origTotalEnergy ) {
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
        new EnergyConserver().fixEnergy( model, body, origTotalEnergy, 0 );
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
        Vector2D.Double mechanicalForce = getMechanicalForce( model, segment );
        AbstractVector2D dissipForce = new ImmutableVector2D.Double();
        return mechanicalForce.getAddedInstance( dissipForce );
    }

    private Vector2D.Double getMechanicalForce( EnergyConservationModel model, Segment segment ) {
        double fgy = getFGy( model );
        EC3Debug.debug( "segment.getAngle() = " + segment.getAngle() );
        EC3Debug.debug( "Math.cos( segment.getAngle()) = " + Math.cos( segment.getAngle() ) );
        AbstractVector2D normalForce = getNormalForce( model, segment );
        EC3Debug.debug( "normalForce.getY() = " + normalForce.getY() );
        double fy = fgy + normalForce.getY();
        double fx = normalForce.getX();
//        Vector2D.Double netForce = new Vector2D.Double( fx, fy );

//        System.out.println( "fx = " + fx );
//        System.out.println( "fy = " + fy );
//        Vector2D.Double netForce = new Vector2D.Double( fx + body.getThrust().getX(), fy + body.getThrust().getY() );
        Vector2D.Double netForce = new Vector2D.Double( fx + body.getThrust().getX(), fy + body.getThrust().getY() );
        return netForce;
    }

    private double getFGy( EnergyConservationModel model ) {
        double fgy = model.getGravity() * body.getMass();
        return fgy;
    }

    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment ) {
//                if( body.getVelocity().getMagnitude() > .1 ) {
//                    return body.getVelocity().getScaledInstance( -getFrictionCoefficient());
//                }else{
//                    return new ImmutableVector2D.Double( );
//                }

        //todo if not moving, see if the friction force should balance the mechanical forces.
//        System.out.println( "body.getVelocity().getMagnitude() = " + body.getVelocity().getMagnitude() );
        if( body.getVelocity().getMagnitude() > 0 ) {
            double fricMag = getFrictionCoefficient() * getNormalForce( model, segment ).getMagnitude();
            AbstractVector2D friction = body.getVelocity().getScaledInstance( -fricMag );
            return friction;
        }
        else {
            return new ImmutableVector2D.Double();
        }
    }

    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment ) {
        double fgy = getFGy( model );
        AbstractVector2D normalForce = segment.getUnitNormalVector().getScaledInstance( -fgy * Math.cos( segment.getAngle() ) );
        return normalForce;
    }

    private double getFrictionCoefficient() {
        return 0.2;//todo parameterize
//        return 10;//todo parameterize
    }

    public AbstractSpline getSpline() {
        return spline;
    }

}
