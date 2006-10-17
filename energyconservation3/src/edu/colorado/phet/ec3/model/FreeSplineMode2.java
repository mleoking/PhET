package edu.colorado.phet.ec3.model;

import edu.colorado.phet.ec3.model.spline.AbstractSpline;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 11:55:53 AM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class FreeSplineMode2 implements UpdateMode {
    private EnergyConservationModel model;
    private AbstractSpline spline;
    private double x = 0;

    public FreeSplineMode2( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public void stepInTime( Body body, double dt ) {
        ForceMode forceMode = new ForceMode() {
            public void init( Body body ) {
            }
        };
//        forceMode.setNetForce( createNetForce( body ) );
        forceMode.stepInTime( body, dt );
        System.out.println( "FreeSplineMode2.stepInTime" );

//        spline.evaluate();
        setDistAlongSpline( body, x );
        x += 0.01;
    }

    private void setDistAlongSpline( Body body, double distAlongSpline ) {
        body.setAttachmentPointPosition( spline.evaluate( distAlongSpline ) );
    }

    public void init( Body body ) {
        x = getDistAlongSpline();
        body.setVelocity( 0, 0 );
        body.convertToSpline();
    }

    private double getDistAlongSpline() {//todo: binary search?
        for( int i = 0; i < 100; i++ ) {
            double
        }
        return 0;
    }
//    private AbstractVector2D createNetForce() {
//        return new ImmutableVector2D.Double( 50, 0 );
//    }

//    private AbstractVector2D createNetForce( Body body ) {
//        AbstractVector2D[] forces = new AbstractVector2D[]{
//                getGravityForce( model, body ),
//                getNormalForce( model, body ),
//                body.getThrust(),
//                getFrictionForce( model, body )
//        };
//        Vector2D.Double sum = new Vector2D.Double();
//        for( int i = 0; i < forces.length; i++ ) {
//            AbstractVector2D force = forces[i];
//            sum.add( force );
//        }
//        if( Double.isNaN( sum.getX() ) ) {
//            System.out.println( "nan" );
//        }
//        return sum;
//    }
//
//    private AbstractVector2D getGravityForce( EnergyConservationModel model, Body body ) {
//        return new Vector2D.Double( 0, getFGy( model, body ) );
//    }
//
//    private double getFGy( EnergyConservationModel model, Body body ) {
//        return model.getGravity() * body.getMass();
//    }
//
//    private AbstractVector2D getFrictionForce( EnergyConservationModel model, Segment segment, Body body ) {
//        double fricMag = getFrictionCoefficient( body ) * getNormalForce( model, segment, body ).getMagnitude();
//        if( body.getVelocity().getMagnitude() > 0 ) {
//            return body.getVelocity().getInstanceOfMagnitude( -fricMag * 5 );//todo fudge factor of five
//        }
//        else {
//            return new ImmutableVector2D.Double( 0, 0 );
//        }
//    }
//
//    private AbstractVector2D getNormalForce( EnergyConservationModel model, Segment segment, Body body ) {
//        if( body.getVelocity().dot( segment.getUnitNormalVector() ) <= 0.1 ) {
////        if( segment.getUnitNormalVector().dot( getGravityForce( model ) ) < 0 ) {//todo is this correct?
//            return segment.getUnitNormalVector().getScaledInstance( getFGy( model, body ) * Math.cos( segment.getAngle() ) );
//        }
//        else {
//            return new ImmutableVector2D.Double();
//        }
//    }
//
//    private double getFrictionCoefficient( Body body ) {
//        return body.getFrictionCoefficient();
//    }
}
