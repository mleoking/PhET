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

public class SplineMode implements UpdateMode {
    private EnergyConservationModel model;
    private AbstractSpline spline;
    private double lastX;
    private Body lastState;
    private Body afterNewton;

    public SplineMode( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public void stepInTime( Body body, double dt ) {
        Body origState = body.copyState();
//        System.out.println( "body.isUserControlled() = " + body.isUserControlled() );
//        double x1 = body.isUserControlled()?getDistAlongSplineSearch( body.getAttachPoint(), lastX, 5, 60, 3 ):lastX;
        double x1 = lastX;
        pointVelocityAlongSpline( x1, body );
        AbstractVector2D netForce = getNetForce( x1, body );
//        System.out.println( "netForce = " + netForce );
        new ForceMode( netForce ).stepInTime( body, dt );
        afterNewton = body.copyState();

//        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x1, 0.3, 60, 2 );
        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x1, 0.3, 60, 3 );
//        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x1, 0.3, 600, 3 );
        if( x2 <= 0 || x2 >= spline.getLength() - 0.01 ) {//fly off the end of the spline
            fixEnergy( origState, netForce, x2, body, dt );
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
        }
        else if( shouldFlyOff( x2, body ) ) {
            fixEnergy( origState, netForce, x2, body, dt );
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
            body.setAngularVelocity( 0.0 );
        }
        else {
            double thermalEnergy = getFrictionForce( ( x1 + x2 ) / 2, body ).getMagnitude() * origState.getPositionVector().getSubtractedInstance( body.getPositionVector() ).getMagnitude() * 1E2;
//            System.out.println( "thermalEnergy = " + thermalEnergy );
            body.addThermalEnergy( thermalEnergy );
            lastX = x2;

            //todo: make sure we sank into the spline before applying this change
            //these 2 steps are sometimes changing the energy by a lot!!!
            body.setAttachmentPointPosition( spline.evaluateAnalytical( x2 ) );
            rotateBody( x2, dt, Double.POSITIVE_INFINITY, body );

//            if( !isUserControlled( body ) ) {
            System.out.println( "body.getThrust().getMagnitude() = " + body.getThrust().getMagnitude() + ", ltz=" + ( body.getThrust().getMagnitude() <= 0 ) );
            if( body.getThrust().getMagnitude() <= 0 ) {
                fixEnergy( origState, netForce, x2, body, dt );
            }
//            }
            lastState = body.copyState();
        }
    }

    private void pointVelocityAlongSpline( double x1, Body body ) {
        double sign = spline.getUnitParallelVector( x1 ).dot( body.getVelocity() ) > 0 ? 1 : -1;
        body.setVelocity( spline.getUnitParallelVector( x1 ).getInstanceOfMagnitude( body.getVelocity().getMagnitude() * sign ) );
    }

    private void fixEnergy( Body origState, AbstractVector2D netForce, double x2, final Body body, double dt ) {
        boolean fixed = false;

        if( !fixed && body.getSpeed() >= 0.0001 ) {
            //increasing the speed threshold from 0.001 to 0.1 causes the moon-sticking problem to go away.
            fixed = fixed || new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 15, 0.0001 );
//            if( fixed ) {
//                System.out.println( "Fixed with velocity 1" );
//            }
        }
        if( !fixed && Math.abs( spline.getUnitNormalVector( x2 ).getY() ) < 0.9 ) {
            double epsilon = 0.001;//1E-8     
            fixed = fixed || fixEnergyOnSpline( origState, x2, body, epsilon );
            if( fixed ) {
                System.out.println( "fixed with spline" );
            }
        }
        if( !fixed ) {
            fixed = fixed || new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 15, 0.0001 );
            if( fixed ) {
                System.out.println( "Fixed with velocity 2" );
            }
        }
        if( !fixed && body.getFrictionCoefficient() > 0 ) {
            //try to fix with heat
            if( body.getTotalEnergy() > origState.getTotalEnergy() ) {
                double gainedEnergyValue = origState.getEnergyDifferenceAbs( body );
                System.out.println( "Energy error: gained " + gainedEnergyValue + " joules" );
                double gainedHeat = body.getThermalEnergy() - origState.getThermalEnergy();
                System.out.println( "gained " + gainedHeat + " joules of heat" );
                if( gainedHeat > gainedEnergyValue ) {
                    body.addThermalEnergy( -gainedEnergyValue );
                    System.out.println( "Reduced heat to solve energy crisis: newError=" + origState.getEnergyDifferenceAbs( body ) );
                    fixed = true;
                }
                else {
                    System.out.println( "Had Error, but can't wholly correct with heat, removing what we can." );
                    body.addThermalEnergy( -gainedHeat );
                }
            }
            else {
                System.out.println( "Energy error: lost " + origState.getEnergyDifferenceAbs( body ) + " joules" );
                System.out.println( "The system lost energy while friction was on!!! error=" + origState.getEnergyDifferenceAbs( body ) );

                //rarely happens
            }
        }
        if( !fixed && !body.isUserControlled() ) {

            //look for a nearby rotation and/or spline position that conserves energy...?
            //wait until upside up to stop in a well
//            System.out.println( "netForce.getMagnitude() = " + netForce.getMagnitude() + ", absSinRot=" + Math.abs( Math.sin( body.getAttachmentPointRotation() ) ) );
            if( netForce.getMagnitude() < 5000 && ( Math.abs( Math.sin( body.getAttachmentPointRotation() ) ) < 0.1 ) ) {
                System.out.println( "Looks like the bottom of a well: Stopping..." );
                setBodyState( origState, body );
                body.setVelocity( new ImmutableVector2D.Double( 0, 0 ) );
                body.addThermalEnergy( origState.getTotalEnergy() - body.getTotalEnergy() );
            }
            else {
                if( origState.getEnergyDifferenceAbs( body ) > 1E-6 ) {
                    double finalE = body.getTotalEnergy();
                    double origE = origState.getTotalEnergy();
                    boolean gainedEnergy = finalE > origE;
                    String text = gainedEnergy ? "Gained Energy" : "Lost Energy";
                    System.out.println( text + ", After everything we tried, still have Energy error=" + origState.getEnergyDifferenceAbs( body ) + ". " + ", velocity=" + body.getVelocity() + ", DeltaVelocity=" + body.getVelocity().getSubtractedInstance( origState.getVelocity() ) + ", deltaY=" + ( body.getY() - origState.getY() ) + ", deltaThermal=" + ( body.getThermalEnergy() - origState.getThermalEnergy() ) + ", ke=" + body.getKineticEnergy() + ", pe=" + body.getPotentialEnergy() + ", deltaKE=" + ( body.getKineticEnergy() - origState.getKineticEnergy() ) + ", deltaPE=" + ( body.getPotentialEnergy() - origState.getPotentialEnergy() ) );
                    System.out.println( "trying to fix on spline again..." );
                    boolean splineFixed = false;
                    double minEpsilon = 0.001;
                    double maxEpsilon = 0.01;
                    int numSteps = 5;
                    for( int i = 0; i < numSteps; i++ ) {
                        double epsilon = minEpsilon + ( maxEpsilon - minEpsilon ) * i / ( (double)numSteps );
                        System.out.println( "epsilon = " + epsilon );
                        splineFixed = splineFixed || fixEnergyOnSpline( origState, x2, body, epsilon );
                        if( splineFixed ) {
                            System.out.println( "spline fixed for epsilon=" + epsilon );
                        }
                    }
                    System.out.println( "origState.getEnergyDifferenceAbs( body ) = " + origState.getEnergyDifferenceAbs( body ) );
                    if( origState.getEnergyDifferenceAbs( body ) < 1E-6 ) {
                        System.out.println( "fixed by moving elsewhere on spline" );
                    }
                    else {
                        System.out.println( "couldn't fix on spline, getting stuck." );
                        fixEnergyOnSplineTakeBest( origState, x2, body, 0.01 );
                        setBodyState( origState, body );
                    }
                }
            }
        }
    }

    boolean fixEnergyOnSpline( final Body origState, final double x2, final Body body, double epsilon ) {
        Body beforeSearch = body.copyState();
        fixEnergyOnSplineTakeBest( origState, x2, body, epsilon );
        boolean fixed = origState.getEnergyDifferenceAbs( body ) < 1E-4;
        if( !fixed ) {
            body.setAttachmentPointPosition( beforeSearch.getAttachPoint() );
            body.setAttachmentPointRotation( beforeSearch.getAttachmentPointRotation() );
        }
        return fixed;
    }

    private void fixEnergyOnSplineTakeBest( final Body origState, final double x2, final Body body, double epsilon ) {
        double x3 = getDistAlongSplineBinarySearch( x2, epsilon, 60, 5, new AbstractSpline.SplineCriteria() {
            public double evaluate( Point2D loc ) {
                body.setAttachmentPointPosition( loc );
                rotateBody( x2, 1.0, Double.POSITIVE_INFINITY, body );
                return Math.abs( body.getTotalEnergy() - origState.getTotalEnergy() );
            }
        } );
        body.setAttachmentPointPosition( spline.evaluateAnalytical( x3 ) );
        rotateBody( x2, 1.0, Double.POSITIVE_INFINITY, body );
    }

    private double getDistAlongSplineBinarySearch( double center, double epsilon, int numPts, int numIterations, AbstractSpline.SplineCriteria splineCriteria ) {
        double best = 0;
        for( int i = 0; i < numIterations; i++ ) {
            best = spline.minimizeCriteria( Math.max( center - epsilon, 0 ), Math.min( spline.getLength(), center + epsilon ), numPts, splineCriteria );
            center = best;
            epsilon = epsilon / numPts * 2;
        }
        return best;
    }

    private void setBodyState( Body state, Body body ) {
        body.setVelocity( state.getVelocity() );
        body.setAttachmentPointPosition( state.getAttachPoint() );
        body.setAttachmentPointRotation( state.getAttachmentPointRotation() );
        body.setThermalEnergy( state.getThermalEnergy() );
    }

    private boolean shouldFlyOff( double x, Body body ) {
        boolean flyOffTop = afterNewton.getVelocity().dot( spline.getUnitNormalVector( x ) ) > 0 && isSplineTop( spline, x, body );
        boolean flyOffBottom = afterNewton.getVelocity().dot( spline.getUnitNormalVector( x ) ) < 0 && !isSplineTop( spline, x, body );
        return ( flyOffTop || flyOffBottom ) && !spline.isRollerCoasterMode();
    }

    private void rotateBody( double x, double dt, double maxRotationDTheta, Body body ) {
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
        double offset = isSplineTop( spline, x, body ) ? 0.0 : Math.PI;
        body.rotateAboutAttachmentPoint( dA + offset );
    }

    public static boolean isSplineTop( AbstractSpline spline, double x, Body body ) {
        Vector2D.Double cmVector = new Vector2D.Double( body.getAttachPoint(), body.getCenterOfMass() );
        Vector2D.Double attachVector = new Vector2D.Double( body.getAttachPoint(), body.getCenterOfMass() );
        return cmVector.dot( spline.getUnitNormalVector( x ) ) > 0 && attachVector.dot( spline.getUnitNormalVector( x ) ) > 0;
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
        lastX = getDistAlongSpline( body.getAttachPoint() );
        lastState = body.copyState();
    }

    private double getDistAlongSpline( Point2D pt, double min, double max, double numPts ) {
        return spline.getDistAlongSpline( pt, min, max, numPts );
    }

    private AbstractVector2D getNetForce( double x, Body body ) {
        //todo: normal should opposed both gravity and thrust when applicable
        AbstractVector2D[] forces = new AbstractVector2D[]{
                body.getGravityForce(),
                body.getThrust(),
                getFrictionForce( x, body ),
                getNormalForce( x, body )
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

    private AbstractVector2D getNormalForce( double x, Body body ) {
        AbstractVector2D n = spline.getUnitNormalVector( x );
        double length = body.getGravityForce().getAddedInstance( body.getThrust() ).dot( n );
        double angle = isSplineTop( spline, x, body ) ? n.getAngle() : n.getAngle() + Math.PI;
        return Vector2D.Double.parseAngleAndMagnitude( length, -angle );
    }

    private AbstractVector2D getFrictionForce( double x, Body body ) {
        //todo kind of a funny workaround for getting friction on the ground.
        double coefficient = Math.max( body.getFrictionCoefficient(), spline.getFrictionCoefficient() );
        double fricMag = coefficient * getNormalForce( x, body ).getMagnitude();//todo should the normal force be computed as emergent?
        if( body.getVelocity().getMagnitude() > 0 ) {
            return body.getVelocity().getInstanceOfMagnitude( -fricMag );
        }
        else {
            return new ImmutableVector2D.Double( 0, 0 );
        }
    }

    public AbstractSpline getSpline() {
        return spline;
    }

    public UpdateMode copy() {
        SplineMode splineMode = new SplineMode( model, spline );
        splineMode.lastX = lastX;
//        splineMode.lastNormalForce = lastNormalForce == null ? null : new Vector2D.Double( lastNormalForce );
        //todo: shouldn't we have to do some portion of copying the body states?
        return splineMode;
    }

    public void setSpline( AbstractSpline spline ) {
        this.spline = spline;
    }
}
