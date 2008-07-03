package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.Arrays;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class ZLJPotentialEnergyMeasurerTester extends TestCase {
    private static final StatesOfMatterAtom P1 = new StatesOfMatterAtom(10, 2, 1, 1);
    private static final StatesOfMatterAtom P2 = new StatesOfMatterAtom(20, 3, 1, 1);
    private static final StatesOfMatterAtom P3 = new StatesOfMatterAtom(20.5, 3, 1, 1);
    private static final LennardJonesPotential  P  = new LennardJonesPotential(1.0, 1.0);

    public void testPotentialEnergyOfOneParticleIsZero() {
        assertTrue(measure(new StatesOfMatterAtom[]{P1}) == 0.0);
    }

    public void testPotentialEnergyOfTwoFarParticlesIsLessThanZero() {
        assertTrue(measure(new StatesOfMatterAtom[]{P1, P2}) < 0.0);
    }

    public void testCloseParticlesHaveHigherPotentialEnergy() {
        assertTrue(
            measure(new StatesOfMatterAtom[]{P1, P2}) <
            measure(new StatesOfMatterAtom[]{P2, P3})
        );
    }

    public void testPotentialOfAllIsPotentialOfPairs() {
        assertEquals(
            measure(new StatesOfMatterAtom[]{P1, P2}) +
            measure(new StatesOfMatterAtom[]{P1, P3}) +
            measure(new StatesOfMatterAtom[]{P2, P3}),
            measure(new StatesOfMatterAtom[]{P1, P2, P3}),
            0.00001
        );
    }
    
    public double measure(StatesOfMatterAtom[] particles) {
        return new LJPotentialEnergyMeasurer(Arrays.asList(particles), P).measure();
    }
}
