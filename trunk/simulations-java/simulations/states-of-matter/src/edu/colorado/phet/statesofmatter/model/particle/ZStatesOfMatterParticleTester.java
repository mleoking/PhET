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

    public void testEqualsHashCodeWithOnlyAccelXDifference() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1, 2, 3, 4);
        StatesOfMatterParticle p2 = new StatesOfMatterParticle(1, 2, 3, 4);
        StatesOfMatterParticle p3 = new StatesOfMatterParticle(1, 2, 3, 4);

        p3.setAx(1.0);
        
        TestingUtils.testEqualityAndHashCode(p1, p2, p3);
    }
    
    public void testEqualsHashCodeWithOnlyAccelYDifference() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1, 2, 3, 4);
        StatesOfMatterParticle p2 = new StatesOfMatterParticle(1, 2, 3, 4);
        StatesOfMatterParticle p3 = new StatesOfMatterParticle(1, 2, 3, 4);

        p3.setAy(1.0);

        TestingUtils.testEqualityAndHashCode(p1, p2, p3);
    }

    public void testClone() {
        TestingUtils.testClone(P, NOT_EQUAL_TO_P);
    }

    public void testGetInverseMass() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 1, 2);

        assertEquals(1.0/2.0, p.getInverseMass(), 0.00000001);
    }

    public void testGetKineticEnergy() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 4, 3);

        p.setVx(2.0);
        p.setVy(1.0);

        assertEquals(0.5 * 3.0 * Math.pow(Math.sqrt(2.0 * 2.0 + 1.0), 2), p.getKineticEnergy(), 0.00001);
    }

    public void testSetKineticEnergy() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 4, 3);

        p.setVx(2.0);
        p.setVy(1.0);

        p.setKineticEnergy(37);

        assertEquals(37.0, p.getKineticEnergy(), 0.00001);
        assertEquals(p.getVx(), p.getVy() * 2.0, 0.00001);
    }

    public void testSetKineticEnergyOnParticleHavingNoKineticEnergy() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 4, 3);

        p.setKineticEnergy(37);

        assertEquals(37.0, p.getKineticEnergy(), 0.00001);
        assertTrue(p.getVelocity().getMagnitude() > 0);
    }

    public void testSetKineticEnergyToZero() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 4, 3);

        p.setVx(2.0);
        p.setVy(1.0);

        p.setKineticEnergy(0);

        assertEquals(0.0, p.getVelocity().getMagnitude(), 0.0);
    }
    
    public void testGetPositionReflectsSetPosition() {
        StatesOfMatterParticle p = new StatesOfMatterParticle(0, 0, 4, 3);

        p.setX(9923);
        p.setY(9392);

        assertEquals(new Point2D.Double(9923, 9392), p.getPosition());
    }

    public void testCloneIsDeepForX() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1.0, 1.0, 1.0, 2.0);
        StatesOfMatterParticle p2 = (StatesOfMatterParticle)p1.clone();

        p2.setX(2.0);

        assertEquals(1.0, p1.getX(), 0.0);
    }

    public void testCloneIsDeepForY() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1.0, 1.0, 1.0, 2.0);
        StatesOfMatterParticle p2 = (StatesOfMatterParticle)p1.clone();

        p2.setY(2.0);

        assertEquals(1.0, p1.getY(), 0.0);
    }

    public void testCloneIsDeepForVx() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1.0, 1.0, 1.0, 2.0);
        StatesOfMatterParticle p2 = (StatesOfMatterParticle)p1.clone();

        p2.setVx(2.0);

        assertEquals(0.0, p1.getVx(), 0.0);
    }

    public void testCloneIsDeepForVy() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1.0, 1.0, 1.0, 2.0);
        StatesOfMatterParticle p2 = (StatesOfMatterParticle)p1.clone();

        p2.setVy(2.0);

        assertEquals(0.0, p1.getVy(), 0.0);
    }

    public void testCloneIsDeepForAx() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1.0, 1.0, 1.0, 2.0);
        StatesOfMatterParticle p2 = (StatesOfMatterParticle)p1.clone();

        p2.setAx(2.0);

        assertEquals(0.0, p1.getAx(), 0.0);
    }

    public void testCloneIsDeepForAy() {
        StatesOfMatterParticle p1 = new StatesOfMatterParticle(1.0, 1.0, 1.0, 2.0);
        StatesOfMatterParticle p2 = (StatesOfMatterParticle)p1.clone();

        p2.setAy(2.0);

        assertEquals(0.0, p1.getAy(), 0.0);
    }

    public void testIllegalArgumentExceptionThrownForOutOfRangeRadius() {
        try {
            new StatesOfMatterParticle(0, 0, -1, 1);

            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }
    
    public void testIllegalArgumentExceptionThrownForOutOfRangeMass() {
        try {
            new StatesOfMatterParticle(0, 0, 1, -1);

            fail();
        }
        catch (IllegalArgumentException e) {
        }
    }
}
