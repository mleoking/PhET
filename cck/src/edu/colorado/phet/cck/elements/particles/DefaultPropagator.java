/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.particles;

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
public class DefaultPropagator implements Propagator {
    ParticleSet particleSet;
    private double currentSpeedScale = .0005;
    private double maxSpeed = currentSpeedScale * 10;
    private Circuit circuit;
    Random random = new Random();

    public DefaultPropagator(Circuit circuit, ParticleSet particleSet) {
        this.circuit = circuit;

        this.particleSet = particleSet;
    }

    public void propagate(BranchParticle bp, double dt) {
        double speed = bp.getBranch().getCurrent() * currentSpeedScale;
        System.out.println("---");
        System.out.println("speed = " + speed);
        speed = clamp(speed);
        System.out.println("clamp speed = " + speed);

        double x = bp.getPosition();
        x += dt * speed;
        if (x > bp.getBranch().getLength()) {
            JunctionGroup jg = circuit.getJunctionGroup(bp.getBranch().getEndJunction());
            Branch outgoingBranch = getOutgoingBranch(jg);
            if (outgoingBranch == null)
                return;
            double distanceOver = x - bp.getBranch().getLength();
            bp.setBranch(outgoingBranch, distanceOver, jg);
//            bp.setDistanceFromJunction(distanceOver, jg);
        } else if (x < 0) {
            double distanceUnder = -x;
            JunctionGroup jg = circuit.getJunctionGroup(bp.getBranch().getStartJunction());
            Branch outgoingBranch = getOutgoingBranch(jg);
            if (outgoingBranch == null) {
                return;
            }
            bp.setBranch(outgoingBranch, distanceUnder, jg);
//            bp.setDistanceFromJunction(distanceUnder, jg);
        } else
            bp.setPosition(x);
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

    public void stepInTime(double dt) {
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
