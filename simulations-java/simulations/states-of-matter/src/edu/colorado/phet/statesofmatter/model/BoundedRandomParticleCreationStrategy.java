package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

public class BoundedRandomParticleCreationStrategy implements ParticleCreationStrategy {
    private final Random random = new Random();
    private final Rectangle2D.Double bounds;

    public BoundedRandomParticleCreationStrategy(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    public Particle createNewParticle(List particles) {
        double x = bounds.x + random.nextDouble() * bounds.width;
        double y = bounds.y + random.nextDouble() * bounds.height;

        return new Particle(x, y);
    }
}
