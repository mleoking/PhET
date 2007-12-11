package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class NonOverlappingParticleCreationStrategy implements ParticleCreationStrategy {
    private static final int MAX_TRIALS = 1000;

    private final ParticleCreationStrategy boundedStrategy;
    private final double particleRadius;
    private final double cushion;

    public NonOverlappingParticleCreationStrategy(Rectangle2D.Double bounds, double particleRadius, double cushion) {
        this.cushion = cushion;
        this.boundedStrategy = new BoundedParticleCreationStrategy(bounds);
        this.particleRadius  = particleRadius;
    }

    public StatesOfMatterParticle createNewParticle(List particles, double radius, double mass) {
        for (int i = 0; i < MAX_TRIALS; i++) {
            boolean nonOverlapping = true;

            StatesOfMatterParticle p1 = boundedStrategy.createNewParticle(particles, radius, mass);

            for (int j = 0; j < particles.size(); j++) {
                StatesOfMatterParticle p2 = (StatesOfMatterParticle)particles.get(j);

                double deltaX = p1.getX() - p2.getX();
                double deltaY = p1.getY() - p2.getY();

                double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                if (dist < 2 * particleRadius + cushion) {
                    nonOverlapping = false;

                    break;
                }
            }

            if (nonOverlapping) {
                return p1;
            }
        }

        throw new RuntimeException("Space too crowded to create new non-overlapping particle");
    }
}
