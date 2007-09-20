package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.util.Iterator;
import java.util.List;

public class LennardJonesForceCalculator {
    private final LennardJonesForce ljf;
    private final List particles;
    private double[] deltaR    = new double[2];
    private double[] curForces = new double[2];

    public LennardJonesForceCalculator(LennardJonesForce ljf, List particles) {
        this.ljf       = ljf;
        this.particles = particles;
    }

    public void calculate(StatesOfMatterParticle p, double[] forces) {
        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterParticle cur = (StatesOfMatterParticle)iterator.next();

            if (cur != p) {
                deltaR[0] = cur.getX() - p.getX();
                deltaR[1] = cur.getX() - p.getY();

                ljf.evaluateInPlace(deltaR, curForces);

                forces[0] += curForces[0];
                forces[1] += curForces[1];
            }
        }
    }
}
