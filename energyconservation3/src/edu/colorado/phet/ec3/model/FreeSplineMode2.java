package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 11:55:53 AM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class FreeSplineMode2 implements UpdateMode {
    private EnergyConservationModel model;
    private AbstractSpline spline;
    private double savedLocation;
    private Body lastState;
    private Body afterNewton;

    public FreeSplineMode2( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public void stepInTime( Body body, double dt ) {
        Body origState = body.copyState();
        double x = savedLocation;
        double sign = spline.getUnitParallelVector( x ).dot( body.getVelocity() ) > 0 ? 1 : -1;
        body.setVelocity( spline.getUnitParallelVector( x ).getInstanceOfMagnitude( body.getVelocity().getMagnitude() * sign ) );
        new ForceMode( createNetForce( body, x ) ).stepInTime( body, dt );
        afterNewton = body.copyState();
        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x, 0.3, 60, 2 );
        System.out.println( "x2=" + x2 );
        if( x2 <= 0 || x2 >= spline.getLength() - 0.01 ) {//fly off the end of the spline
            body.setFreeFallMode();
            return;
        }
        if( afterNewton.getVelocity().dot( spline.getUnitNormalVector( x2 ) ) > 0 ) {
            body.setFreeFallMode();
            return;
        }
        savedLocation = x2;
        Point2D splineLocation = spline.evaluateAnalytical( x2 );
        body.setAttachmentPointPosition( splineLocation );
        rotateBody( body, x2, dt, Double.POSITIVE_INFINITY );
        new EnergyConserver().fixEnergy( body, origState.getTotalEnergy() );
        lastState = body.copyState();
    }

    private void rotateBody( Body body, double x, double dt, double maxRotationDTheta ) {
        double bodyAngle = body.getAttachmentPointRotation();
        double dA = spline.getUnitParallelVector( x ).getAngle() - bodyAngle;
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
    }

    private double getDistAlongSplineSearch( Point2D attachPoint, double center, double epsilon, int numPts, int numIterations ) {
        double best = 0;
        for( int i = 0; i < numIterations; i++ ) {
            best = getDistAlongSpline( attachPoint, Math.max( center - epsilon, 0 ), Math.min( spline.getLength(), center + epsilon ), numPts );
            center = best;
            epsilon = epsilon / numPts * 2;
        }
        return best;
    }

    private double getDistAlongSpline( Point2D attachPoint ) {
        return getDistAlongSpline( attachPoint, 0, spline.getLength(), 100 );
    }

    public void init( Body body ) {
        body.convertToSpline();
        savedLocation = getDistAlongSpline( body.getAttachPoint() );
        lastState = body.copyState();

    }

    private double getDistAlongSpline( Point2D pt, double min, double max, double numPts ) {
        return spline.getDistAlongSpline( pt, min, max, numPts );
    }

    private AbstractVector2D createNetForce( Body body, double x ) {
        //todo: normal should opposed both gravity and thrust when applicable
        AbstractVector2D[] forces = new AbstractVector2D[]{
                body.getGravityForce(),
                getNormalForce( body, x ),
                body.getThrust(),
                getFrictionForce( body, x )
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

    private AbstractVector2D getFrictionForce( Body body, double x ) {
        double fricMag = body.getFrictionCoefficient() * getNormalForce( body, x ).getMagnitude();
        if( body.getVelocity().getMagnitude() > 0 ) {
            return body.getVelocity().getInstanceOfMagnitude( -fricMag );
        }
        else {
            return new ImmutableVector2D.Double( 0, 0 );
        }
    }

    private AbstractVector2D getNormalForce( Body body, double x ) {
//        if( afterNewton != null ) {
//            System.out.println( "afterNewton.getVelocity() = " + afterNewton.getVelocity() );
//        }
//        if( afterNewton == null || afterNewton.getVelocity().dot( spline.getUnitNormalVector( x ) ) > 0 ) {
//        double cosA = Math.cos( spline.getUnitNormalVector( x ).getNormalVector().getAngle() );
//        return spline.getUnitNormalVector( x ).getScaledInstance( body.getGravityForce().getMagnitude() * cosA );
//        }
//        else {
        return new Vector2D.Double();
//        }
    }
}
