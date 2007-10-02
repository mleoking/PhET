package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.awt.geom.Line2D;

public class ZLennardJonesWallForceCalculatorTester extends TestCase {
    private static final double EPSILON = 1.0;
    private static final double RMIN    = 1.0;
    private static final Line2D.Double     WALL  = new Line2D.Double(1, 1, -1, -1);
    private static final LennardJonesForce FORCE = new LennardJonesForce(EPSILON, RMIN);

    private volatile StatesOfMatterParticle p;
    private double[] forces = new double[2];

    public void setUp() {
        p = new StatesOfMatterParticle(0, 1, 1, 1);

        for (int i = 0; i < forces.length; i++) {
            forces[i] = 0.0;
        }
    }

    public void testForceIsZeroAtEpsilonFromWall() {
        calcForceAtDist(EPSILON);

        assertEquals(forces[0], 0.0, 0.0000001);
        assertEquals(forces[1], 0.0, 0.0000001);
    }


    public void testForceIsRepulsiveAtLessThanEpsilonFromWall() {
        calcForceAtDist(0.5 * EPSILON);

        assertTrue(forces[0] > 0);
        assertTrue(forces[1] < 0);
    }

    public void testForceIsAttractiveAtGreaterThanEpsilonFromWall() {
        calcForceAtDist(1.5 * EPSILON);

        assertTrue(forces[0] < 0);
        assertTrue(forces[1] > 0);
    }

    private void calcForceAtDist(double dist) {
        double delta = dist / Math.sqrt(2);

        double t = Math.random() * Math.sqrt(8) - Math.sqrt(8)/2.0;

        p.setX(t + delta);
        p.setY(t - delta);

        LennardJonesWallForceCalculator calculator = new LennardJonesWallForceCalculator(FORCE, WALL);

        assertEquals(WALL.ptLineDist(p.getX(), p.getY()), dist, 0.000001);

        calculator.calculate(p, forces);
    }
    
}
