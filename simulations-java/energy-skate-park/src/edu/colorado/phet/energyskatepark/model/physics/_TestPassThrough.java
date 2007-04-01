package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import junit.framework.TestCase;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * Mar 31, 2007, 7:44:36 PM
 */
public class _TestPassThrough extends TestCase {

    public ParticleTestState getDefaultTestState( double lineX, double speedX ) {
        return new ParticleTestState( new Point2D.Double[]{new Point2D.Double( lineX, -1 ), new Point2D.Double( lineX, +1 )},
                                      new Point2D.Double( 0, 0 ), new Vector2D.Double( speedX, 0.0 ) );
    }

    public void runTest( ParticleTestState testState, double dt, int iterations ) {
        boolean origSide = testState.getSide();
        double t = 0;
        for( int i = 0; i < 100; i++ ) {
            testState.stepInTime( dt );
            t += dt;
            boolean side = testState.getSide();
            assertEquals( "Particle passed through at iteration: " + i, origSide, side );
            System.out.println( "t=" + t + ", top=" + testState.getSide() + ", testState.getParticle().getPosition() = " + testState.getParticle().getPosition() );
        }
    }

    public void testFastPassThrough() {
        ParticleTestState testState = getDefaultTestState( 1, 2.0 );
        runTest( testState, 1.0, 100 );
    }

    public void testSlowPassThrough() {
        ParticleTestState testState = getDefaultTestState( 1, 0.1 );
        runTest( testState, 1.0, 100 );
    }

    public static class ParticleTestState {
        private ParametricFunction2D parametricFunction2D;
        private Particle particle;

        public ParticleTestState( Point2D.Double[] controlPoints, Point2D.Double position, AbstractVector2D velocity ) {
            parametricFunction2D = new CubicSpline2D( controlPoints );
            particle = new Particle( parametricFunction2D );
            particle.setGravity( 0.0 );
            particle.setPosition( position );
            particle.setVelocity( velocity );
        }

        public boolean getSide() {
            return particle.isAboveSpline( 0 );
        }

        public void stepInTime( double dt ) {
            particle.stepInTime( dt );
        }

        public Particle getParticle() {
            return particle;
        }
    }
}
