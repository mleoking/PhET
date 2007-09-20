package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.util.Arrays;

public class ZKineticEnergyMeasurerTester extends TestCase {
    private static final StatesOfMatterParticle P1 = new StatesOfMatterParticle(0, 2, 3, 4);
    private static final StatesOfMatterParticle P2 = new StatesOfMatterParticle(4, 3, 2, 1);

    public void testMeasure() {
        KineticEnergyMeasurer measurer = new KineticEnergyMeasurer(Arrays.asList(new StatesOfMatterParticle[]{P1, P2}));

        assertEquals(P1.getKineticEnergy() + P2.getKineticEnergy(), measurer.measure(), 0.000001);
    }
}
