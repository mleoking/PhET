package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:42:19 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */

public class Particle {
    private Particle1D particle1D;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double g = 9.8;
    private double mass = 1.0;

    private UpdateStrategy updateStrategy = new Particle1DUpdate();
    private ParticleStage particleStage;

    public Particle( CubicSpline2D cubicSpline2D ) {
        this( new ParticleStage( cubicSpline2D ) );
    }

    public Particle( ParticleStage particleStage ) {
        particle1D = new Particle1D( particleStage.getCubicSpline2D( 0 ), g );

        this.particleStage = particleStage;
    }

    public void stepInTime( double dt ) {
        updateStrategy.stepInTime( dt );
        update();
    }

    interface UpdateStrategy {
        void stepInTime( double dt );
    }

    public double getMass() {
        return mass;
    }

    class Particle1DUpdate implements UpdateStrategy {
        public void stepInTime( double dt ) {

            //compare a to v/r^2 to see if it leaves the track
//            double a = Math.abs( particle1D.getUnitNormalVector().dot( particle1D.getNetForce() ) );
            double r = Math.abs( particle1D.getRadiusOfCurvature() );//todo: how can I be certain units are correct here?
            System.out.println( "r = " + r );
            double normalForce = getMass() * particle1D.getSpeed() * particle1D.getSpeed() / r - getMass() * g;
            System.out.println( "normalForce = " + normalForce );
//            double threshold = particle1D.getSpeed() * particle1D.getSpeed() / r;
//            System.out.println( "normalAccel=" + a + ", v^2/r=" + threshold );
            System.out.println( "particle1D.getCurvatureDirection() = " + particle1D.getCurvatureDirection() + ", y>=0: " + (particle1D.getCurvatureDirection().getY() >= 0) );
            //if normal force is positive on the top of a hill fly off
            //if normal force is negative on a valley fly off
            if( normalForce > 0 && particle1D.getCurvatureDirection().getY() >= 0 ) {
                switchToFreeFall();
            }
            else {

                particle1D.stepInTime( dt );
                x = particle1D.getX();
                y = particle1D.getY();
                AbstractVector2D vel = particle1D.getVelocity2D();
                vx = vel.getX();
                vy = vel.getY();
            }
        }
    }

    private void switchToFreeFall() {
        setVelocity( particle1D.getVelocity2D() );
        setFreeFall();
        //todo: update location so it's guaranteed on the right side of the spline?
    }

    class FreeFall implements UpdateStrategy {

        public void stepInTime( double dt ) {
            Point2D origLoc = new Point2D.Double( x, y );
            vy += g * dt;
            vx += 0;

            y += vy * dt + 0.5 * g * dt * dt;
            x += vx * dt;

            Point2D newLoc = new Point2D.Double( x, y );

            //check for crossover
            for( int i = 0; i < particleStage.numCubicSpline2Ds(); i++ ) {
                CubicSpline2D cubicSpline = particleStage.getCubicSpline2D( i );

                double alpha = cubicSpline.getClosestPoint( new Line2D.Double( origLoc, newLoc ) );
                boolean crossed = checkForCrossOver( cubicSpline, alpha, origLoc, newLoc );

                if( crossed ) {
                    System.out.println( "crossed over" );
                    double ptLineDist = new Line2D.Double( origLoc, newLoc ).ptLineDist( cubicSpline.evaluate( alpha ) );
                    System.out.println( "ptLineDist = " + ptLineDist );
                    if( ptLineDist < 0.5 ) {//this number was determined heuristically for a set of tests (free parameter)
                        //todo: should take a min over all possible crossover points (for this spline and others)

                        AbstractVector2D parallel = cubicSpline.getUnitParallelVector( alpha );
                        AbstractVector2D norm = cubicSpline.getUnitNormalVector( alpha );
                        //reflect the velocity about the parallel direction
                        AbstractVector2D parallelVelocity = parallel.getInstanceOfMagnitude( parallel.dot( getVelocity() ) );
                        double elasticity = 0.9;
                        AbstractVector2D newNormalVelocity = norm.getInstanceOfMagnitude( norm.dot( getVelocity() ) ).getScaledInstance( elasticity );
                        AbstractVector2D newVelocity = parallelVelocity.getSubtractedInstance( newNormalVelocity );

                        double stickiness = 0.5;

                        double testVal = Math.abs( newNormalVelocity.getMagnitude() / newVelocity.getMagnitude() );
                        System.out.println( "testv = " + testVal );
                        boolean bounce = testVal >= stickiness;

                        if( bounce ) {
                            setVelocity( newVelocity );

                            //set the position to be just on top of the spline
                            Point2D splineLoc = cubicSpline.evaluate( alpha );
                            double sign = isBelowSpline( cubicSpline, alpha, origLoc ) ? -1.0 : 1.0;
                            Point2D finalPosition = norm.getInstanceOfMagnitude( 1.0E-2 * sign ).getDestination( splineLoc );//todo: determine this free parameter
                            setPosition( finalPosition );
                        }
                        else {
                            setParticle1DStrategy( cubicSpline );
                            particle1D.setAlpha( alpha );
                            particle1D.setVelocity( 0.0 );//todo convert 2d velocity to 1d velocity
                        }
                        break;
                    }
                }
            }
        }
    }

    private void setVelocity( AbstractVector2D velocity ) {
        setVelocity( velocity.getX(), velocity.getY() );
    }

    public Vector2D.Double getVelocity() {
        return new Vector2D.Double( vx, vy );
    }

    public boolean isBelowSpline( CubicSpline2D cubicSpline2D, double alpha, Point2D loc ) {
        AbstractVector2D v = cubicSpline2D.getUnitNormalVector( alpha );
        Vector2D.Double a = new Vector2D.Double( cubicSpline2D.evaluate( alpha ), loc );
        return a.dot( v ) < 0;
    }

    boolean checkForCrossOver( CubicSpline2D cubicSpline2D, double alpha, Point2D origLoc, Point2D newLoc ) {
        return isBelowSpline( cubicSpline2D, alpha, origLoc ) != isBelowSpline( cubicSpline2D, alpha, newLoc );
    }

    class UserUpdateStrategy implements UpdateStrategy {
        public void stepInTime( double dt ) {
        }
    }

    public void setFreeFall() {
        setUpdateStrategy( new FreeFall() );
    }

    public void setParticle1DStrategy( CubicSpline2D spline ) {
        particle1D.setCubicSpline2D( spline );
        setUpdateStrategy( new Particle1DUpdate() );
    }

    public void setUserUpdateStrategy() {
        setUpdateStrategy( new UserUpdateStrategy() );
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
        update();
    }

    private void update() {
        notifyListeners();
    }

    private ArrayList listeners = new ArrayList();

    public Point2D getPosition() {
        return new Point2D.Double( x, y );
    }

    public void setPosition( Point2D pt ) {
        setPosition( pt.getX(), pt.getY() );
    }

    public void setPosition( double x, double y ) {
        setFreeFall();
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setVelocity( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
        notifyListeners();
    }

    public static interface Listener {
        void particleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleChanged();
        }
    }

}
