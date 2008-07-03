package edu.colorado.phet.statesofmatter.model.engine.gravity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class ZGravityPotentialMeasurerTester extends TestCase {
    private static final double FLOOR = -2.0;
    private static final double G     = -10.0;
    
    private static final StatesOfMatterAtom P1 = new StatesOfMatterAtom(0, FLOOR, 1, 1);
    private static final StatesOfMatterAtom P2 = new StatesOfMatterAtom(4, 3, 1, 1);

    public void testGravityPotentialIsZeroAtFloor() {
        assertEquals(0, measure(P1), 0.000001);
    }

    public void testP1() {
        test(P1);
    }

    public void testP2() {
        test(P2);
    }

    public void testPotentialEnergyIsPositiveAboveFloor() {
        assertTrue(measure(new StatesOfMatterAtom(0, FLOOR - 1, 1, 1)) > 0);
    }

    public void testPotentialEnergyIsNegativeBelowFloor() {
        assertTrue(measure(new StatesOfMatterAtom(0, FLOOR + 1, 1, 1)) < 0);
    }

    public void testPotentialEnergyIsZeroAtFloor() {
        assertEquals(0.0, measure(new StatesOfMatterAtom(0, FLOOR, 1, 1)), 0.0);
    }

    public void testSumOfPotentialEnergies() {
        assertEquals(measure(P1, P2), measure(P1) + measure(P2), 0.000001);
    }
    
    private void test(StatesOfMatterAtom p) {
        assertEquals(p.getMass()*(FLOOR-p.getY())*Math.abs(G), measure(p), 0.000001);
    }

    private double measure(StatesOfMatterAtom p) {
        return measure(p, null);
    }

    private double measure(StatesOfMatterAtom p, StatesOfMatterAtom p2) {
        List list = new ArrayList();

        list.add(p);

        if (p2 != null) {
            list.add(p2);
        }

        return new GravityPotentialMeasurer(list, FLOOR, G).measure();
    }
}
