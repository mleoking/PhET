package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.Collection;
import java.util.Iterator;

import edu.colorado.phet.statesofmatter.model.engine.Calculator;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class LennardJonesForceCalculator implements Calculator {
    private final LennardJonesForce ljf;
    private final Collection particles;
    private double[] deltaR    = new double[2];
    private double[] curForces = new double[2];

    public LennardJonesForceCalculator(LennardJonesForce ljf, Collection particles) {
        this.ljf       = ljf;
        this.particles = particles;
    }

    public void calculate(StatesOfMatterParticle p, double[] forces) {
        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterParticle cur = (StatesOfMatterParticle)iterator.next();

            if (cur != p) {
                deltaR[0] = p.getX() - cur.getX();
                deltaR[1] = p.getY() - cur.getY();

                ljf.evaluateInPlace(deltaR, curForces);

                forces[0] += curForces[0];
                forces[1] += curForces[1];
            }
        }
    }
}
