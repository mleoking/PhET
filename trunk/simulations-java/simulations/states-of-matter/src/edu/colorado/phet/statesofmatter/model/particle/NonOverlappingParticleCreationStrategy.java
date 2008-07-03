package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class NonOverlappingParticleCreationStrategy extends AbstractParticleCreationStrategy implements ParticleCreationStrategy {
    private static final int MAX_TRIALS = 1000;

    private final ParticleCreationStrategy boundedStrategy;
    private final double particleRadius;
    private final double cushion;
    private final List particles;

    public NonOverlappingParticleCreationStrategy(Rectangle2D.Double bounds, double particleMass, double particleRadius, double cushion, List particles) {
        this.cushion         = cushion;
        this.particles       = particles;
        this.boundedStrategy = new BoundedParticleCreationStrategy(bounds, particleMass, particleRadius);
        this.particleRadius  = particleRadius;
    }

    public StatesOfMatterAtom createParticle() {
        for (int i = 0; i < MAX_TRIALS; i++) {
            boolean nonOverlapping = true;

            StatesOfMatterAtom p1 = boundedStrategy.createParticle();

            for (int j = 0; j < particles.size(); j++) {
                StatesOfMatterAtom p2 = (StatesOfMatterAtom)particles.get(j);

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

        return null;
    }
}
