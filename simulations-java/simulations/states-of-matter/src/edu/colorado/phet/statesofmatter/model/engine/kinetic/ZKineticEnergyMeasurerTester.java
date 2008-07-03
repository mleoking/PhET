package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.util.Arrays;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class ZKineticEnergyMeasurerTester extends TestCase {
    private static final StatesOfMatterAtom P1 = new StatesOfMatterAtom(0, 2, 3, 4);
    private static final StatesOfMatterAtom P2 = new StatesOfMatterAtom(4, 3, 2, 1);

    public void testMeasure() {
        KineticEnergyMeasurer measurer = new KineticEnergyMeasurer(Arrays.asList(new StatesOfMatterAtom[]{P1, P2}));

        assertEquals(P1.getKineticEnergy() + P2.getKineticEnergy(), measurer.measure(), 0.000001);
    }
}
