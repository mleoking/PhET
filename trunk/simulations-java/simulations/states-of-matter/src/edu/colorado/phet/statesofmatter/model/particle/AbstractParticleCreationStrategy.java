package edu.colorado.phet.statesofmatter.model.particle;

import java.util.Collection;

public abstract class AbstractParticleCreationStrategy implements ParticleCreationStrategy {
    public int createParticles(Collection list, int maximum) {
        int created = 0;

        for (int i = 0; i < maximum; i++) {
            StatesOfMatterAtom p = createParticle();

            if (p != null) {
                ++created;

                list.add(p);
            }
            else {
                break;
            }
        }

        return created;
    }
}
