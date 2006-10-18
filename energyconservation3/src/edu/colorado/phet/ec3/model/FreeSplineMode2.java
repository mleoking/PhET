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
        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x, 0.3, 60, 2 );
        double distToSpline = ( body.getAttachPoint().distance( spline.evaluateAnalytical( x2 ) ) );

        savedLocation = x2;
        Point2D splineLocation = spline.evaluateAnalytical( x2 );
        body.setAttachmentPointPosition( splineLocation );
        rotateBody( body, x2, dt, Double.POSITIVE_INFINITY );
        new EnergyConserver().fixEnergy( body, origState.getTotalEnergy() );
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
    }

    private double getDistAlongSpline( Point2D pt, double min, double max, double numPts ) {
        double best = Double.POSITIVE_INFINITY;
        double scalar = 0.0;
        for( double x = min; x <= max; x += ( max - min ) / numPts ) {
            Point2D loc = spline.evaluateAnalytical( x );
            double dist = pt.distance( loc );
            if( dist < best ) {
                best = dist;
                scalar = x;
            }
        }
//        System.out.println( "found best=" + scalar + ", score=" + best );
        return scalar;
    }

    private AbstractVector2D createNetForce( Body body, double x ) {
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
        double cosA = Math.cos( spline.getUnitNormalVector( x ).getNormalVector().getAngle() );
        return spline.getUnitNormalVector( x ).getScaledInstance( body.getGravityForce().getMagnitude() * cosA );
    }
}
