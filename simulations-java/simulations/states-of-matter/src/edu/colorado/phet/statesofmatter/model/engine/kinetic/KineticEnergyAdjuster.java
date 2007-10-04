package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.util.List;

public class KineticEnergyAdjuster {
    public void adjust(List particles, double totalTargetKe) {
        if (particles.size() == 0) return;

        double leftOverEnergy;

        boolean changed;

        do {
            changed        = false;
            leftOverEnergy = 0.0;

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
                        leftOverEnergy = particleTargetKe;

                        particleTargetKe = 0.0;
                    }

                    p.setKineticEnergy(particleTargetKe);

                    changed = true;
                }
            }

            totalTargetKe = getKe(particles) + leftOverEnergy;
        }
        while (changed && Math.abs(leftOverEnergy) > 0.000001);
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
