package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
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
    private double g = 9.8 * 10000;

    private UpdateStrategy updateStrategy = new Particle1DUpdate();
    private ParticleStage particleStage;

    public Particle( CubicSpline2D cubicSpline2D ) {
        this( new ParticleStage( cubicSpline2D ) );
    }

    public Particle( ParticleStage particleStage ) {
        particle1D = new Particle1D( particleStage.getCubicSpline2D( 0 ) );
        this.particleStage = particleStage;
    }

    public void stepInTime( double dt ) {
        updateStrategy.stepInTime( dt );
        update();
    }

    interface UpdateStrategy {
        void stepInTime( double dt );
    }

    class Particle1DUpdate implements UpdateStrategy {
        public void stepInTime( double dt ) {
            particle1D.stepInTime( dt );
            x = particle1D.getX();
            y = particle1D.getY();
            AbstractVector2D vel = particle1D.getVelocity();
            vx = vel.getX();
            vy = vel.getY();
        }
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
                int numSegments = 100;
                double splineLength = cubicSpline.getMetricDelta( 0, 1 ) / numSegments;

                for( int k = 0; k < numSegments; k++ ) {
                    boolean checkForCrossOver = checkForCrossOver( cubicSpline, k / numSegments, origLoc, newLoc );
                    double alpha = ( (double)k ) / numSegments;
                    double ptLineDist = new Line2D.Double( origLoc, newLoc ).ptLineDist( cubicSpline.evaluate( alpha ) );
                    System.out.println( "k = " + k + ", cross=" + checkForCrossOver + ", ptLineDist=" + ptLineDist );
                    if( checkForCrossOver ) {
                        if( ptLineDist < splineLength / numSegments * 10 ) {
                            setParticle1DStrategy( cubicSpline );
                            break;
                        }
                    }
                }
            }
        }
    }

    boolean checkForCrossOver( CubicSpline2D cubicSpline2D, double alpha, Point2D origLoc, Point2D newLoc ) {
        AbstractVector2D v = cubicSpline2D.getUnitNormalVector( alpha );
        Vector2D.Double a = new Vector2D.Double( cubicSpline2D.evaluate( alpha ), origLoc );
        Vector2D.Double b = new Vector2D.Double( cubicSpline2D.evaluate( alpha ), newLoc );
        if( MathUtil.getSign( a.dot( v ) ) != MathUtil.getSign( b.dot( v ) ) ) {
            return true;
        }
        else {
            return false;
        }
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
