// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 2, 2007
 * Time: 1:28:03 PM
 */

public class TestState {
    private ArrayList controlPointSets = new ArrayList();
    private String name;

    public TestState( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int numCubicSpline2Ds() {
        return controlPointSets.size();
    }

    public void addCubicSpline2D( SerializablePoint2D[] controlPoints ) {
        controlPointSets.add( controlPoints );
    }

    public SerializablePoint2D[] getCubicSpline2D( int i ) {
        return (SerializablePoint2D[])controlPointSets.get( i );
    }

    public void start() {
        TestPhysics1D testPhysics1D = new TestPhysics1D();
        testPhysics1D.setTestState( this );
        testPhysics1D.start();
    }

    public void init( Particle particle, ParticleStage particleStage ) {
    }

    public static class SplineTestState extends TestState {
        private int splineIndex;
        double alpha;
        double velocity;
        boolean top;

        public SplineTestState( int splineIndex, double alpha, double velocity, boolean top ) {
            this( "", splineIndex, alpha, velocity, top );
        }

        public SplineTestState( String name, int splineIndex, double alpha, double velocity, boolean top ) {
            super( name );
            this.splineIndex = splineIndex;
            this.alpha = alpha;
            this.velocity = velocity;
            this.top = top;
        }

        public int getSplineIndex() {
            return splineIndex;
        }

        public void setSplineIndex( int splineIndex ) {
            this.splineIndex = splineIndex;
        }

        public boolean isTop() {
            return top;
        }

        public void setTop( boolean top ) {
            this.top = top;
        }

        public double getAlpha() {
            return alpha;
        }

        public void setAlpha( double alpha ) {
            this.alpha = alpha;
        }

        public double getVelocity() {
            return velocity;
        }

        public void setVelocity( double velocity ) {
            this.velocity = velocity;
        }

        public void init( Particle particle, ParticleStage particleStage ) {
            super.init( particle, particleStage );
            particle.switchToTrack( particleStage.getCubicSpline2D( splineIndex ), alpha, top );
            particle.getParticle1D().setVelocity( velocity );
        }
    }

    public static class FreeFallTestState extends TestState {
        double x;
        double y;
        double vx;
        double vy;

        public FreeFallTestState( double x, double y, double vx, double vy ) {
            this( "", x, y, vx, vy );
        }

        public FreeFallTestState( String name, double x, double y, double vx, double vy ) {
            super( name );
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        public void setPosition( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public void setVelocity( double vx, double vy ) {
            this.vx = vx;
            this.vy = vy;
        }

        public void init( Particle particle, ParticleStage particleStage ) {
            super.init( particle, particleStage );
            particle.setFreeFall();
//            particle.switchToTrack( particleStage.getCubicSpline2D( splineIndex ), alpha, top );
//            particle.getParticle1D().setVelocity( velocity );
            particle.setPosition( x, y );
            particle.setVelocity( vx, vy );
        }
    }
}
