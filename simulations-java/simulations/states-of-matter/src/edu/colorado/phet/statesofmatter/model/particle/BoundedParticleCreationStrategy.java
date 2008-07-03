package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Rectangle2D;
import java.util.Random;

public class BoundedParticleCreationStrategy extends AbstractParticleCreationStrategy implements ParticleCreationStrategy {
    private final Random random = new Random();
    private final Rectangle2D.Double bounds;
    private final double mass;
    private final double radius;

    public BoundedParticleCreationStrategy(Rectangle2D.Double bounds, double mass, double radius) {
        this.bounds = bounds;
        this.mass   = mass;
        this.radius = radius;
    }

    public StatesOfMatterAtom createParticle() {
        double length = 2 * radius;

        double x = bounds.x + radius + random.nextDouble() * (bounds.width  - length);
        double y = bounds.y + radius + random.nextDouble() * (bounds.height - length);

        return new StatesOfMatterAtom(x, y, radius, mass);
    }
}
