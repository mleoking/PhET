package edu.colorado.phet.statesofmatter.model.particle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractParticleCreationStrategy implements ParticleCreationStrategy {
    public Collection createParticles(int maximum) {
        List list = new ArrayList();

        for (int i = 0; i < maximum; i++) {
            StatesOfMatterParticle p = createParticle();

            if (p != null) {
                list.add(p);
            }
            else {
                break;
            }
        }

        return list;
    }
}
