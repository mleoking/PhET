package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import junit.framework.TestCase;

import java.util.List;

public class ZMultipleParticleModelTester extends TestCase {
    private volatile MultipleParticleModel model;

    public void setUp() {
        this.model = new MultipleParticleModel();
    }

    public void testThatNewlyConstructedModelContainsDefaultParticles() {
        List particleList = model.getParticles();

        assertEquals(StatesOfMatterConfig.INITIAL_PARTICLE_COUNT, particleList.size());
    }

    public void testThatInitializeResetsParticleList() {
        model.initialize();

        List particleList = model.getParticles();

        assertEquals(StatesOfMatterConfig.INITIAL_PARTICLE_COUNT, particleList.size());
    }

    public void testThatParticleListIsUnmodifiable() {
        try {
            model.getParticles().add(StatesOfMatterParticle.TEST);

            fail();
        }
        catch (Exception e) {
        }
    }
}
