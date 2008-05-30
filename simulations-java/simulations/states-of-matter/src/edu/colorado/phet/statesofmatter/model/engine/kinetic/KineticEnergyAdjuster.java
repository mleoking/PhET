package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.util.List;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class KineticEnergyAdjuster {
    private static final double TARGET_PRECISION_EPS = 0.00000001;

    public void adjust(List particles, double totalTargetKe) {
        if (totalTargetKe < 0.0) throw new IllegalArgumentException("Total target KE must be positive, but was " + totalTargetKe);
        if (particles.size() == 0) return;
        
        do {
            double totalCurKe = getKe(particles);

            double totalDeltaKe = totalTargetKe - totalCurKe;

            boolean givingEnergy = totalDeltaKe > 0.0;

            if (Math.abs(totalDeltaKe) < 0.00001) {
                return;
            }

            double particleDeltaKe;

            if (!givingEnergy) {
                // Can only take energy from particles that HAVE energy:
                particleDeltaKe = totalDeltaKe / countParticlesHavingEnergy(particles);
            }
            else {
                // Can give energy to ALL particles:
                particleDeltaKe = totalDeltaKe / particles.size();
            }

            for (int i = 0; i < particles.size(); i++) {
                StatesOfMatterParticle p = (StatesOfMatterParticle)particles.get(i);

                if (givingEnergy || particleHasEnergy(p)) {
                    double particleCurKe = p.getKineticEnergy();

                    double particleTargetKe = particleCurKe + particleDeltaKe;

                    if (particleTargetKe <= 0.0) {
                        particleTargetKe = 0.0;
                    }

                    p.setKineticEnergy(particleTargetKe);
                }
            }
        }
        while (Math.abs(getKe(particles) - totalTargetKe) > TARGET_PRECISION_EPS);
    }

    private int countParticlesHavingEnergy(List particles) {
        int count = 0;

        for (int i = 0; i < particles.size(); i++) {
            StatesOfMatterParticle p = (StatesOfMatterParticle)particles.get(i);

            if (particleHasEnergy(p)) {
                count++;
            }
        }

        return count;
    }

    private boolean particleHasEnergy(StatesOfMatterParticle p) {
        return p.getKineticEnergy() > 0.000001;
    }

    private double getKe(List particles) {
        return new KineticEnergyMeasurer(particles).measure();
    }
}
