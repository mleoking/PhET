package edu.colorado.phet.statesofmatter.model.engine.gravity;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class ZGravityPotentialTester extends TestCase {
    private static final double FLOOR = -2.0;
    private static final double G     = 10.0;
    
    private static final StatesOfMatterParticle P1 = new StatesOfMatterParticle(0, FLOOR, 1, 1);
    private static final StatesOfMatterParticle P2 = new StatesOfMatterParticle(4, 3, 1, 1);

    public void testGravityPotentialIsZeroAtFloor() {
        assertEquals(0, measure(P1), 0.000001);
    }

    public void testP1() {
        test(P1);
    }

    public void testP2() {
        test(P2);
    }

    public void testSumOfPotentialEnergies() {
        assertEquals(measure(P1, P2), measure(P1) + measure(P2), 0.000001);
    }
    
    private void test(StatesOfMatterParticle p) {
        assertEquals(p.getMass()*(p.getY()-FLOOR)*G, measure(p), 0.000001);
    }

    private double measure(StatesOfMatterParticle p) {
        return measure(p, null);
    }

    private double measure(StatesOfMatterParticle p, StatesOfMatterParticle p2) {
        List list = new ArrayList();

        list.add(p);

        if (p2 != null) {
            list.add(p2);
        }

        return new GravityPotential(list, FLOOR, G).measure();
    }
}
