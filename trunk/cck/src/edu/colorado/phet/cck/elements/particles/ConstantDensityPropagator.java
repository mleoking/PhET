/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.particles;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.JunctionGroup;
import edu.colorado.phet.common.math.PhetVector;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Nov 21, 2003
 * Time: 9:58:03 AM
 * Copyright (c) Nov 21, 2003 by Sam Reid
 */
public class ConstantDensityPropagator implements Propagator {
    ParticleSet particleSet;
//    private double currentSpeedScale = .0005;
    private double currentSpeedScale = .005;
//    double maxSpeed = currentSpeedScale * 10;
    double maxSpeed = .15999985056645929;//currentSpeedScale * 100000;
    private Circuit circuit;
    Random random = new Random();
    double density = CCK2Module.ELECTRON_SEPARATION;
    private double time = 0;

    public ConstantDensityPropagator(Circuit circuit, ParticleSet particleSet) {
        this.circuit = circuit;
        this.particleSet = particleSet;
    }

    private double clamp(double speed) {
        if (speed < 0) {
            if (speed < -maxSpeed)
                return -maxSpeed;
        } else {
            if (speed > maxSpeed)
                return maxSpeed;
        }
        return speed;
    }

    public void propagate(BranchParticle bp, double dt) {
        double speed = bp.getBranch().getCurrent() * currentSpeedScale;
//       System.out.println("speed = " + speed);
        speed = clamp(speed);
//        System.out.println("Clamped speed = " + speed);
        //Apply any necessary clamps.
//        if (speed == 0)
//            speed = .001;
        double x = bp.getPosition();
        x += dt * speed;
        if (x > bp.getBranch().getLength()) {
            particleSet.removeParticle(bp);
        } else if (x < 0) {
            particleSet.removeParticle(bp);
        } else
            bp.setPosition(x);
    }

    public void stepInTime(double dt) {
        time += dt;
        //Create any new particles?

        for (int i = 0; i < circuit.numBranches(); i++) {
            Branch b = circuit.branchAt(i);
            JunctionGroup start = circuit.getJunctionGroup(b.getStartJunction());
            boolean startJunction = true;
            if (b.getCurrent() < 0) {
                start = circuit.getJunctionGroup(b.getEndJunction());
                startJunction = false;
            }
            double distToClosest = getDistanceToClosestParticle(b, start);
            if (distToClosest >= density) {
                BranchParticle bp = new BranchParticle(b);
                if (!startJunction) {
                    bp.setPosition(b.getLength());
                }
                particleSet.addParticle(bp);
            }
        }
    }

    private Branch[] getOutgoingBranches(final JunctionGroup jg) {
        Branch[] branches = circuit.getBranches(jg, new ObjectSelector() {
            public boolean isValid(Object o) {
                Branch b = (Branch) o;
                if (jg.contains(b.getStartJunction())) {
                    if (b.getCurrent() > 0)
                        return true;
                    else
                        return false;
                } else if (jg.contains(b.getEndJunction())) {
                    if (b.getCurrent() < 0)
                        return true;
                    else
                        return false;
                } else
                    throw new RuntimeException("Branch not contained.");
            }
        });
        return branches;
    }

    public Branch getOutgoingBranch(final JunctionGroup jg) {
        Branch[] branches = getOutgoingBranches(jg);
        if (branches.length == 0)
            return null;
        else {
            int index = random.nextInt(branches.length);
            double biggestDistToClosest = 0;//Choose the branch with the farthest distance to the next particle.
            for (int i = 0; i < branches.length; i++) {
                double distToClosest = getDistanceToClosestParticle(branches[i], jg);
                if (distToClosest > biggestDistToClosest) {
                    biggestDistToClosest = distToClosest;
                    index = i;
                }
            }
            return branches[index];
        }
    }

    private double getDistanceToClosestParticle(Branch branch, JunctionGroup jg) {
        BranchParticle[] bp = particleSet.getBranchParticles(branch);
        double closestDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < bp.length; i++) {
            BranchParticle particle = bp[i];
            PhetVector pv = particle.getPosition2D();
            Point2D.Double jgLoc = jg.getLocation();
            double dist = jgLoc.distance(pv.getX(), pv.getY());
            if (dist < closestDist) {
                closestDist = dist;
            }
        }
        return closestDist;
    }

}
