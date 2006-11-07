package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
    private Vector2D.Double lastNormalForce = new Vector2D.Double();

    public SplineMode( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public boolean isUserControlled( Body body ) {
        return body.isUserControlled() || spline.isUserControlled();
    }

    public void stepInTime( Body body, double dt ) {
        Body origState = body.copyState();
        double x1 = lastX;
        double sign = spline.getUnitParallelVector( x1 ).dot( body.getVelocity() ) > 0 ? 1 : -1;
        body.setVelocity( spline.getUnitParallelVector( x1 ).getInstanceOfMagnitude( body.getVelocity().getMagnitude() * sign ) );
        AbstractVector2D netForceWithoutNormal = getNetForcesWithoutNormal( x1, body );
        new ForceMode( netForceWithoutNormal ).stepInTime( body, dt );
        afterNewton = body.copyState();

        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x1, 0.3, 60, 2 );
        if( x2 <= 0 || x2 >= spline.getLength() - 0.01 ) {//fly off the end of the spline
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
        }
        else if( shouldFlyOff( x2, body ) ) {
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
            body.setAngularVelocity( 0.0 );
        }
        else {
            double thermalEnergy = getFrictionForce( ( x1 + x2 ) / 2, body ).getMagnitude() * origState.getPositionVector().getSubtractedInstance( body.getPositionVector() ).getMagnitude();
            body.addThermalEnergy( thermalEnergy );
            lastX = x2;

            //make sure we sank into the spline before applying this change
            body.setAttachmentPointPosition( spline.evaluateAnalytical( x2 ) );
            rotateBody( x2, dt, Double.POSITIVE_INFINITY, body );

            if( !isUserControlled( body ) ) {
                fixEnergy( origState, netForceWithoutNormal.getAddedInstance( lastNormalForce ), x2, body );
            }
            lastState = body.copyState();
            lastNormalForce = updateNormalForce( origState, body, netForceWithoutNormal, dt );
        }
    }

    boolean fixEnergyOnSpline( final Body origState, double x2, final Body body ) {
        Body beforeFix = body.copyState();
        //look for an adjacent position with a more accurate energy
//        double epsilon = 0.01;//1E-7
        double epsilon = 0.001;//1E-8     
//        double epsilon = 0.0001;//0.5 sometimes     
//        double epsilon = 0.01;
        double x3 = getDistAlongSplineBinarySearch( x2, epsilon, 60, 5, new AbstractSpline.SplineCriteria() {
            public double evaluate( Point2D loc ) {
                body.setAttachmentPointPosition( loc );
                return Math.abs( body.getTotalEnergy() - origState.getTotalEnergy() );
            }
        } );
        body.setAttachmentPointPosition( spline.evaluateAnalytical( x3 ) );
        double origError = Math.abs( origState.getTotalEnergy() - beforeFix.getTotalEnergy() );
        double newError = Math.abs( origState.getTotalEnergy() - body.getTotalEnergy() );
        return newError == 0;//probably never
//        System.out.println( "x2=" + x2 + ", x3=" + x3 + ", origEnergy=" + origState.getTotalEnergy() + ", beforeFix=" + beforeFix.getTotalEnergy() + ", after fix=" + body.getTotalEnergy() +", origError="+origError+", newError="+newError);
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

    private void fixEnergy( Body origState, AbstractVector2D netForce, double x2, final Body body ) {
        boolean fixed = false;
        if( !fixed && body.getSpeed() >= 0.1 ) {
            //increasing the speed threshold from 0.001 to 0.1 causes the moon-sticking problem to go away.
            fixed = fixed || new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 15, 0.001 );
        }
//        System.out.println( "spline.getUnitNormalVector( x2) = " + spline.getUnitNormalVector( x2 ) );
        if( !fixed && Math.abs( spline.getUnitNormalVector( x2 ).getY() ) < 0.9 ) {
            fixed = fixed || fixEnergyOnSpline( origState, x2, body );
        }
        if( !fixed ) {
            fixed = fixed || new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 15, 0.001 );
        }

        if( !fixed ) {
            //look for a nearby rotation and/or spline position that conserves energy...?
            //wait until upside up to stop in a well
            if( netForce.getMagnitude() < 5000 && ( Math.abs( Math.sin( body.getAttachmentPointRotation() ) ) < 0.1 ) )
            {
                System.out.println( "Looks like the bottom of a well: Stopping..." );
                setBodyState( origState, body );
            }
            else {
                if( origState.getEnergyDifferenceAbs( body ) > 1E1 ) {
                    System.out.println( "After everything we tried, still have Energy error=" + origState.getEnergyDifferenceAbs( body ) + ", rolling back changes." );
                    setBodyState( origState, body );
                }
            }
            //maybe could fix by rotation?, i think no.
            //could fix with friction, if friction is enabled.
        }
    }

    private void setBodyState( Body state, Body body ) {
        body.setVelocity( state.getVelocity() );
        body.setAttachmentPointPosition( state.getAttachPoint() );
        body.setAttachmentPointRotation( state.getAttachmentPointRotation() );
        body.setThermalEnergy( state.getThermalEnergy() );
    }

    private boolean shouldFlyOff( double x, Body body ) {
        boolean flyOffTop = afterNewton.getVelocity().dot( spline.getUnitNormalVector( x ) ) > 0 && isSplineTop( x, body );
        boolean flyOffBottom = afterNewton.getVelocity().dot( spline.getUnitNormalVector( x ) ) < 0 && !isSplineTop( x, body );
        return flyOffTop || flyOffBottom;
    }

    private Vector2D.Double updateNormalForce( Body origState, Body body, AbstractVector2D netForce, double dt ) {
        //numerically unstable, since we divide by dt^2
        //2m/t^2 (x1-x0-v0t)-Fa
        Vector2D.Double vec = new Vector2D.Double();
        vec.add( body.getPositionVector() );
        vec.subtract( origState.getPositionVector() );
        vec.subtract( origState.getVelocity() );
        vec.scale( 2 * body.getMass() / dt / dt );
        vec.subtract( netForce );
        return vec;
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
        double offset = isSplineTop( x, body ) ? 0.0 : Math.PI;
        body.rotateAboutAttachmentPoint( dA + offset );
    }

    private boolean isSplineTop( double x, Body body ) {
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
        lastNormalForce = new Vector2D.Double();
    }

    private double getDistAlongSpline( Point2D pt, double min, double max, double numPts ) {
        return spline.getDistAlongSpline( pt, min, max, numPts );
    }

    private AbstractVector2D getNetForcesWithoutNormal( double x, Body body ) {
        //todo: normal should opposed both gravity and thrust when applicable
        AbstractVector2D[] forces = new AbstractVector2D[]{
                body.getGravityForce(),
                body.getThrust(),
                getFrictionForce( x, body )
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

    private AbstractVector2D getFrictionForce( double x, Body body ) {
        //todo kind of a funny workaround for getting friction on the ground.
        double coefficient = Math.max( body.getFrictionCoefficient(), spline.getFrictionCoefficient() );
        double fricMag = coefficient * lastNormalForce.getMagnitude() / 10.0;//todo should the normal force be computed as emergent?
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
        splineMode.lastNormalForce = lastNormalForce == null ? null : new Vector2D.Double( lastNormalForce );
        //todo: shouldn't we have to do some portion of copying the body states?
        return splineMode;
    }

    public static class GrabSpline {
        private EnergyConservationModel energyConservationModel;

        public GrabSpline( EnergyConservationModel energyConservationModel ) {
            this.energyConservationModel = energyConservationModel;
        }

        public void interactWithSplines( Body body ) {
            body.convertToSpline();
            AbstractSpline grabSpline = getGrabSpline( body );
            if( grabSpline != null ) {
                body.setSplineMode( energyConservationModel, grabSpline );
            }
            else {
                tryCollision( body );
                body.convertToFreefall();
            }
        }

        private AbstractSpline getGrabSpline( Body body ) {
            double bestScore = Double.POSITIVE_INFINITY;
            AbstractSpline bestSpline = null;
            ArrayList allSplines = energyConservationModel.getAllSplines();
            for( int i = 0; i < allSplines.size(); i++ ) {
                double score = getGrabScore( (AbstractSpline)allSplines.get( i ), body );
                if( score < bestScore ) {
                    bestScore = score;
                    bestSpline = (AbstractSpline)allSplines.get( i );
                }
            }
            return bestSpline;
        }

        private void tryCollision( Body body ) {
            body.convertToSpline();
            AbstractSpline collisionSpline = getCollisionSpline( body );
            if( collisionSpline != null ) {
                doCollision( collisionSpline, body );
            }
            else {
                body.convertToFreefall();
            }
        }

        private void doCollision( AbstractSpline collisionSpline, Body body ) {
            Area area = new Area( collisionSpline.getArea() );//todo: remove this duplicate computation
            //todo: add restitution
            area.intersect( new Area( body.getShape() ) );
            Rectangle2D intersectionBounds = area.getBounds2D();
            double x = collisionSpline.getDistAlongSpline( new Point2D.Double( intersectionBounds.getCenterX(), intersectionBounds.getCenterY() ), 0, collisionSpline.getLength(), 100 );
            if( !feetAreClose( body, x, collisionSpline ) ) {
                double epsilon = 0.05;
                if( ( x <= epsilon || x >= collisionSpline.getLength() - epsilon ) && isBodyMovingTowardSpline( body, collisionSpline, x ) )
                {
                    collideWithEnd( body );
                }
                else {
                    collideWithTrack( collisionSpline, x, body );
                }
            }
        }

        private void collideWithTrack( AbstractSpline collisionSpline, double x, Body body ) {
            double origSpeed = body.getSpeed();
            double origEnergy = body.getTotalEnergy();
            double parallelPart = collisionSpline.getUnitParallelVector( x ).dot( body.getVelocity() );
            double perpPart = collisionSpline.getUnitNormalVector( x ).dot( body.getVelocity() );
            Vector2D.Double newVelocity = new Vector2D.Double();
            newVelocity.add( collisionSpline.getUnitParallelVector( x ).getScaledInstance( parallelPart ) );
            newVelocity.add( collisionSpline.getUnitNormalVector( x ).getScaledInstance( -perpPart ) );

            double alpha = 5;
            double speedScale = 1.0 * Math.exp( -alpha * body.getFrictionCoefficient() );
            double newSpeed = body.getSpeed() * speedScale;
            if( newSpeed > origSpeed ) {
                throw new RuntimeException( "Body gained speed on collision" );
            }
            body.setVelocity( newVelocity.getInstanceOfMagnitude( newSpeed ) );

            double newTotalEnergy = body.getTotalEnergy();
            double energyLostInCollision = newTotalEnergy - origEnergy;
            body.addThermalEnergy( -energyLostInCollision );
//            System.out.println( "speedScale=" + speedScale + ", origSpeed=" + origSpeed + ", newSpeed=" + newSpeed + ", friction=" + body.getFrictionCoefficient() + ", energyLostInCollision=" + energyLostInCollision );

//            Body origState = body.copyState();
            body.convertToFreefall();
            body.setAngularVelocity( parallelPart / 2 );
            //while they are still collided, walk back out of it.
//            int count = 0;
//            while( getCollisionSpline( body ) == collisionSpline && count <= 10 ) {
//                new FreeFall( energyConservationModel ).stepIgnoreSplines( body, 0.01 );
//                count++;
//            }
//            if( count >= 10 ) {
//                body.setState( origState.getAcceleration(), origState.getVelocity(), origState.getPosition() );
//            }
        }

        private void collideWithEnd( Body body ) {
            System.out.println( "Collision with end." );
            body.setVelocity( body.getVelocity().getScaledInstance( -1 ) );
            double angle = body.getVelocity().getAngle();
            double maxDTheta = Math.PI / 16;
            double dTheta = ( Math.random() * 2 - 1 ) * maxDTheta;
            body.setVelocity( Vector2D.Double.parseAngleAndMagnitude( body.getVelocity().getMagnitude(), angle + dTheta ) );
            body.setAngularVelocity( dTheta * 10 );
            body.convertToFreefall();
        }

        private AbstractSpline getCollisionSpline( Body body ) {
            double bestScore = Double.POSITIVE_INFINITY;
            AbstractSpline bestSpline = null;
            ArrayList allSplines = energyConservationModel.getAllSplines();
            for( int i = 0; i < allSplines.size(); i++ ) {
                double score = getCollisionScore( (AbstractSpline)allSplines.get( i ), body );
                if( score < bestScore ) {
                    bestScore = score;
                    bestSpline = (AbstractSpline)allSplines.get( i );
                }
            }
            return bestSpline;
        }

        private boolean isBodyMovingTowardSpline( Body body, AbstractSpline bestSpline, double x ) {
            Point2D loc = bestSpline.evaluateAnalytical( x );
            Point2D cm = body.getCenterOfMass();
            Vector2D.Double dir = new Vector2D.Double( loc, cm );
            double v = dir.dot( body.getVelocity() );
            return v < 0;
        }

        private boolean feetAreClose( Body body, double x, AbstractSpline bestSpline ) {
            return bestSpline.evaluateAnalytical( x ).distance( body.getAttachPoint() ) < bestSpline.evaluateAnalytical( x ).distance( body.getCenterOfMass() );
        }

        private double getCollisionScore( AbstractSpline splineSurface, Body body ) {
            Area area = new Area( splineSurface.getArea() );
            area.intersect( new Area( body.getShape() ) );
            boolean bounce = !area.isEmpty();
            return bounce ? 0.0 : Double.POSITIVE_INFINITY;
        }

        private double getGrabScore( AbstractSpline splineSurface, Body body ) {
            double x = splineSurface.getDistAlongSpline( body.getAttachPoint(), 0, splineSurface.getLength(), 100 );
            Point2D pt = splineSurface.evaluateAnalytical( x );
            double dist = pt.distance( body.getAttachPoint() );
//            if( dist < 0.5 && !justLeft( body, splineSurface ) && movingTowards( splineSurface, body, x, pt ) ) {
            if( dist < 0.5 && !justLeft( body, splineSurface ) && movingTowards( splineSurface, body, x, pt ) ) {
                return dist;
            }
            else {
                return Double.POSITIVE_INFINITY;
            }
        }

        private boolean movingTowards( AbstractSpline splineSurface, Body body, double x, Point2D pt ) {
            Point2D cm = body.getCenterOfMass();
            Vector2D.Double cmToPoint = new Vector2D.Double( cm, pt );
            return cmToPoint.dot( body.getVelocity() ) >= 0;
        }

        private boolean justLeft( Body body, AbstractSpline splineSurface ) {
            return body.getLastFallSpline() == splineSurface && ( System.currentTimeMillis() - body.getLastFallTime() ) < 500;
        }

    }
}
