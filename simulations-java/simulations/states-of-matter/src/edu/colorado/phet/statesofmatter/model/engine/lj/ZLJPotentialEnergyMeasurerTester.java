package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.Arrays;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class ZLJPotentialEnergyMeasurerTester extends TestCase {
    private static final StatesOfMatterParticle P1 = new StatesOfMatterParticle(10, 2, 1, 1);
    private static final StatesOfMatterParticle P2 = new StatesOfMatterParticle(20, 3, 1, 1);
    private static final StatesOfMatterParticle P3 = new StatesOfMatterParticle(20.5, 3, 1, 1);
    private static final LennardJonesPotential  P  = new LennardJonesPotential(1.0, 1.0);

    public void testPotentialEnergyOfOneParticleIsZero() {
        assertTrue(measure(new StatesOfMatterParticle[]{P1}) == 0.0);
    }

    public void testPotentialEnergyOfTwoFarParticlesIsLessThanZero() {
        assertTrue(measure(new StatesOfMatterParticle[]{P1, P2}) < 0.0);
    }

    public void testCloseParticlesHaveHigherPotentialEnergy() {
        assertTrue(
            measure(new StatesOfMatterParticle[]{P1, P2}) <
            measure(new StatesOfMatterParticle[]{P2, P3})
        );
    }

    public void testPotentialOfAllIsPotentialOfPairs() {
        assertEquals(
            measure(new StatesOfMatterParticle[]{P1, P2}) +
            measure(new StatesOfMatterParticle[]{P1, P3}) +
            measure(new StatesOfMatterParticle[]{P2, P3}),
            measure(new StatesOfMatterParticle[]{P1, P2, P3}),
            0.00001
        );
    }
    
    public double measure(StatesOfMatterParticle[] particles) {
        return new LJPotentialEnergyMeasurer(Arrays.asList(particles), P).measure();
    }
}
