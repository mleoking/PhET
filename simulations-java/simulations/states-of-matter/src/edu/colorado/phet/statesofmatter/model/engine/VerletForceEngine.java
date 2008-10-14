package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.engine.integration.Integrator1D;
import edu.colorado.phet.statesofmatter.model.engine.integration.VelocityVerletIntegrator1D;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesForce;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class VerletForceEngine implements ForceEngine {
    private Point2D.Double[] newPositions;
    private Vector2D.Double[] newVelocities, newAccelerations;

    public ForceComputation compute(Collection particles, EngineConfig descriptor) {
        allocateArrays(particles.size());

        LennardJonesForce ljf = new LennardJonesForce(descriptor.epsilon, descriptor.rMin);

        EngineForceCalculator calculator = new EngineForceCalculator(descriptor.gravity, ljf, particles /* , descriptor.container.getAllWalls()*/, particles);

        double[] forces = new double[2];

        Integrator1D integrator = new VelocityVerletIntegrator1D(descriptor.deltaT);

        int i = 0;

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterAtom particle = (StatesOfMatterAtom)iterator.next();

            // Calculate force on current particle:
            calculator.calculate(particle, forces);

            // Calculate acceleration from force:
            double curAx = forces[0] / particle.getMass();
            double curAy = forces[1] / particle.getMass();

            // Compute and store new positions, velocities, and accelerations:
            newPositions[i].x = integrator.nextPosition(particle.getX(), particle.getVx(), particle.getAx());
            newPositions[i].y = integrator.nextPosition(particle.getY(), particle.getVy(), particle.getAy());

            newVelocities[i].setX(integrator.nextVelocity(particle.getVx(), particle.getAx(), curAx));
            newVelocities[i].setY(integrator.nextVelocity(particle.getVy(), particle.getAy(), curAy));

            newAccelerations[i].setX(curAx);
            newAccelerations[i].setY(curAy);

            ++i;
        }

        return new ForceComputation(newPositions, newVelocities, newAccelerations);
    }

    private void allocateArrays(int length) {
        if (newPositions == null || newPositions.length != length) {
            newPositions     = new Point2D.Double[length];
            newVelocities    = new Vector2D.Double[length];
            newAccelerations = new Vector2D.Double[length];

            for (int i = 0; i < length; i++) {
                newPositions[i]     = new Point2D.Double();
                newVelocities[i]    = new Vector2D.Double();
                newAccelerations[i] = new Vector2D.Double();
            }
        }
    }
}
