package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.util.List;

public class ZMultipleParticleModelTester extends TestCase {
    private volatile MultipleParticleModel model;
    private volatile ConstantDtClock clock;

    public void setUp() {
        this.clock = new ConstantDtClock(30, 1);
        this.model = new MultipleParticleModel(clock);
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
//
//    public void testThatParticlesMoveWhenClockRunning() {
//        StatesOfMatterParticle p = model.getParticle(0);
//
//
//
//        clock.start();
//    }
}
