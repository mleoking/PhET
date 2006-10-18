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

    public FreeSplineMode2( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public void stepInTime( Body body, double dt ) {
        Body origState = body.copyState();

        double x = getDistAlongSpline( body.getAttachPoint() );
        System.out.println( " createNetForce( body, x )  = " + createNetForce( body, x ) );
        body.setVelocity( spline.getUnitParallelVector( x ).getInstanceOfMagnitude( body.getVelocity().getMagnitude() ) );
        new ForceMode( createNetForce( body, x ) ).stepInTime( body, dt );
        double x2 = getDistAlongSpline( body.getAttachPoint(), Math.max( x - 1, 0 ), Math.min( x + 1, spline.getLength() ), 1000 );
        Point2D splineLocation = spline.evaluateAnalytical( x2 );
        body.setAttachmentPointPosition( splineLocation );
        new EnergyConserver().fixEnergy( body, origState.getTotalEnergy() );
    }

    private double getDistAlongSpline( Point2D attachPoint ) {
        return getDistAlongSpline( attachPoint, 0, spline.getLength(), 1000 );
    }

    public void init( Body body ) {
        body.convertToSpline();
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
