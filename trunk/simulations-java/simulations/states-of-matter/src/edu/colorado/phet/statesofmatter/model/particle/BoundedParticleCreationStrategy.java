package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

public class BoundedParticleCreationStrategy implements ParticleCreationStrategy {
    private final Random random = new Random();
    private final Rectangle2D.Double bounds;

    public BoundedParticleCreationStrategy(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    public StatesOfMatterParticle createNewParticle(List particles, double radius, double mass) {
        double length = 2 * radius;

        double x = bounds.x + radius + random.nextDouble() * (bounds.width  - length);
        double y = bounds.y + radius + random.nextDouble() * (bounds.height - length);

        return new StatesOfMatterParticle(x, y, radius, mass);
    }
}
