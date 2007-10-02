package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.common.phetcommon.util.testing.TestingUtils;
import junit.framework.TestCase;

import java.awt.geom.Point2D;

public class ZStatesOfMatterParticleTester extends TestCase {
    private static final StatesOfMatterParticle P              = new StatesOfMatterParticle(1, 2, 3, 4);
    private static final StatesOfMatterParticle EQUAL_TO_P     = new StatesOfMatterParticle(1, 2, 3, 4);
    private static final StatesOfMatterParticle NOT_EQUAL_TO_P = new StatesOfMatterParticle(1, 2, 3, 5);

    public void setUp() {
    }

    public void testEquality() {
        TestingUtils.testEquality(P, EQUAL_TO_P, NOT_EQUAL_TO_P);
    }

    public void testHashCode() {
        TestingUtils.testHashCode(P, EQUAL_TO_P, NOT_EQUAL_TO_P);
    }

    public void testToString() {
        TestingUtils.testToString(P, EQUAL_TO_P, NOT_EQUAL_TO_P);
    }

    public void testClone() {
        TestingUtils.testClone(P, NOT_EQUAL_TO_P);
    }

    public void testGetInverseMass() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 0, 2);

        assertEquals(1.0/2.0, p.getInverseMass(), 0.00000001);
    }

    public void testGetKineticEnergy() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 4, 3);

        p.setVx(2.0);
        p.setVy(1.0);

        assertEquals(0.5 * 3.0 * Math.pow(Math.sqrt(2.0 * 2.0 + 1.0), 2), p.getKineticEnergy(), 0.00001);
    }
    
    public void testGetPositionReflectsSetPosition() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 4, 3);

        p.setX(9923);
        p.setY(9392);

        assertEquals(new Point2D.Double(9923, 9392), p.getPosition());
    }
}
