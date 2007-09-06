package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleParticleModel extends BaseModel {
    private final List particles = new ArrayList();
    public static final MultipleParticleModel TEST = new MultipleParticleModel();

    public MultipleParticleModel() {
        initialize();
    }

    public void initialize() {
        particles.clear();

        ParticleCreationStrategy strategy = new RandomParticleCreationStrategy();

        for (int i = 0; i < StatesOfMatterConfig.INITIAL_PARTICLE_COUNT; i++) {
            particles.add(strategy.createNewParticle(particles));
        }
    }

    public List getParticles() {
        return Collections.unmodifiableList(particles);
    }

    public StatesOfMatterParticle getParticle(int i) {
        return (StatesOfMatterParticle)particles.get(i);
    }
}
