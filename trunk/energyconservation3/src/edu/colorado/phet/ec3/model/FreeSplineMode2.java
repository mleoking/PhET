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

        double x = getDistAlongSpline( body );
        System.out.println( "x = " + x );
        new ForceMode( createNetForce( body, x ) ).stepInTime( body, dt );
        //move along the spline until we have a good match
//        double epsilon = 1;
//        double deltaT = 0.01;
//        for( double t = x - epsilon; t <= x + epsilon; t += deltaT ) {
//
//        }
        double x2 = getDistAlongSpline( body );

        Point2D splineLocation = spline.getSegmentPath().evaluate( x2 );
        body.setAttachmentPointPosition( splineLocation );
        new EnergyConserver().fixEnergy( body, origState.getTotalEnergy() );
//        System.out.println( "origKE="+origState.getKineticEnergy()+", finalKE="+body.getKineticEnergy() );
    }

    public void init( Body body ) {
        body.convertToSpline();
    }

    private double getDistAlongSpline( Body body ) {//todo: binary search?
        double best = Double.POSITIVE_INFINITY;
        double scalar = 0.0;
        int N = 1000;
        for( int i = 0; i < N; i++ ) {
            double splineLength = spline.getSegmentPath().getLength();
            double alongSpline = i / ( (double)N ) * splineLength;
//            System.out.println( "alongSpline = " + alongSpline );
            Point2D loc = spline.evaluate( alongSpline );
            double dist = body.getPosition().distance( loc );
//            System.out.println( "i = " + i + ", scalar=" + alongSpline + ", score=" + dist );
            if( dist < best ) {
                best = dist;
                scalar = alongSpline;
            }
        }
        System.out.println( "found best=" + scalar + ", score=" + best );
        return scalar;
    }
//    private double getDistAlongSpline( Body body ) {
//        double x = getDistAlongSpline( body, 0, spline.getLength() );
//        return x;
//    }
//
//    private double getDistAlongSpline( Body body, double min, double max ) {//todo: binary search?
//        double bestScore = Double.POSITIVE_INFINITY;
//        double bestDist = 0.0;
//        int N = 100;
//        for( double alongSpline = min; alongSpline <= max; alongSpline += ( max - min ) / N ) {
//
////            double alongSpline = i / ( (double)N ) * splineLength;
//            Point2D loc = spline.evaluate( alongSpline );
//            double dist = body.getPosition().distance( loc );
//            System.out.println( "alongSpline = " + alongSpline + ", dist=" + dist );
//            if( dist < bestScore ) {
//                bestScore = dist;
//                bestDist = alongSpline;
//            }
//        }
////        System.out.println( "found best=" + scalar + ", score=" + best );
//        return bestDist;
//    }

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
        return spline.getUnitNormalVector( x ).getScaledInstance( -body.getGravityForce().getMagnitude() * Math.cos( spline.getUnitNormalVector( x ).getNormalVector().getAngle() ) );
    }
}
