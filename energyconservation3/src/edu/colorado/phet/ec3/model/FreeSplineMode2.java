package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;

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
//    private double x = 0;

    public FreeSplineMode2( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public void stepInTime( Body body, double dt ) {
        ForceMode forceMode = new ForceMode() {
            public void init( Body body ) {
            }
        };
        AbstractVector2D netForce = createNetForce( body );
        System.out.println( "netForce = " + netForce );
        forceMode.setNetForce( netForce );
        forceMode.stepInTime( body, dt );
        Point2D splineLocation = spline.getSegmentPath().evaluate( getDistAlongSpline( body ) );
        body.setAttachmentPointPosition( splineLocation );
//        System.out.println( "FreeSplineMode2.stepInTime" );

//        spline.evaluate();
//        System.out.println( "x = " + x );
//        setDistAlongSpline( body, x );
//        x += 0.01;
    }

    private void setDistAlongSpline( Body body, double distAlongSpline ) {
        body.setAttachmentPointPosition( spline.evaluate( distAlongSpline ) );
    }

    public void init( Body body ) {
        body.convertToSpline();
//        x = getDistAlongSpline( body );
        body.setVelocity( 0, 0 );
    }

    private double getDistAlongSpline( Body body ) {//todo: binary search?
        double best = Double.POSITIVE_INFINITY;
        double scalar = 0.0;
        int N = 1000;
        for( int i = 0; i < N; i++ ) {
            double splineLength = spline.getSegmentPath().getLength();
            double alongSpline = i / ( (double)N ) * splineLength;
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

    private AbstractVector2D createNetForce( Body body ) {
        double x = getDistAlongSpline( body );
        AbstractVector2D[] forces = new AbstractVector2D[]{
                body.getGravityForce(),
                getNormalForce( model, spline.getSegmentPath().getSegmentAtPosition( x ), body ),
                body.getThrust(),
                getFrictionForce( model, spline.getSegmentPath().getSegmentAtPosition( x ), body )
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

    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment, Body body ) {
        if( segment == null ) {
            return new Vector2D.Double();
        }
        double fricMag = body.getFrictionCoefficient() * getNormalForce( model, segment, body ).getMagnitude();
        if( body.getVelocity().getMagnitude() > 0 ) {
            return body.getVelocity().getInstanceOfMagnitude( -fricMag * 5 );//todo fudge factor of five
        }
        else {
            return new ImmutableVector2D.Double( 0, 0 );
        }
    }

    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment, Body body ) {
        if( segment == null ) {
            return new Vector2D.Double();
        }
        if( body.getVelocity().dot( segment.getUnitNormalVector() ) <= 0.1 ) {
//        if( segment.getUnitNormalVector().dot( getGravityForce( model ) ) < 0 ) {//todo is this correct?
            return segment.getUnitNormalVector().getScaledInstance( -body.getGravityForce().getMagnitude() * Math.cos( segment.getAngle() ) );
        }
        else {
            return new ImmutableVector2D.Double();
        }
    }
}
