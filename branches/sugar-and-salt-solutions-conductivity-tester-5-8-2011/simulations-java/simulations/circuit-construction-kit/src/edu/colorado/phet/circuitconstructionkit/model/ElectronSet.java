// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.model.ModelElement;

import java.util.ArrayList;

/**
 * ElectronSet contains all of the electrons in the circuit.
 *
 * @author Sam Reid
 */
public class ElectronSet implements ModelElement {
    private ArrayList<Electron> particles = new ArrayList<Electron>();
    private ConstantDensityPropagator propagator;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public ElectronSet(Circuit circuit) {
        propagator = new ConstantDensityPropagator(this, circuit);
        circuit.addCircuitListener(new CircuitListenerAdapter() {
            public void branchRemoved(Branch branch) {
                removeParticles(branch);
            }
        });
    }

    public double getDensity(Branch branch) {
        Electron[] e = getParticles(branch);
        return e.length / branch.getLength();
    }

    public void addParticle(Electron e) {
        particles.add(e);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).particleAdded(e);
        }
    }

    public void clear() {
        Electron[] e = particles.toArray(new Electron[0]);
        particles.clear();
        notifyElectronsRemoved(e);
    }

    public Electron[] getParticles(Branch branch) {
        ArrayList<Electron> all = new ArrayList<Electron>();
        for (Electron electron : particles) {
            if (electron.getBranch() == branch) {
                all.add(electron);
            }
        }
        return all.toArray(new Electron[all.size()]);
    }

    public static interface Listener {
        void particlesRemoved(Electron[] electrons);

        void particleAdded(Electron e);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void notifyElectronsRemoved(Electron[] p) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).particlesRemoved(p);
        }
    }

    public Electron[] removeParticles(Branch branch) {
        Electron[] p = getParticles(branch);
        for (int i = 0; i < p.length; i++) {
            Electron electron = p[i];
            particles.remove(electron);
            electron.delete();
        }
        notifyElectronsRemoved(p);
        return p;
    }

    public int numParticles() {
        return particles.size();
    }

    public Electron particleAt(int i) {
        return particles.get(i);
    }

    public void stepInTime(double dt) {
        propagator.stepInTime(dt);
    }

    public Electron getUpperNeighborInBranch(Electron myelectron) {
        Electron[] branchElectrons = getParticles(myelectron.getBranch());
        Electron upper = null;
        double dist = Double.POSITIVE_INFINITY;
        for (Electron electron : branchElectrons) {
            if (electron != myelectron) {
                double yourDist = electron.getDistAlongWire();
                double myDist = myelectron.getDistAlongWire();
                if (yourDist > myDist) {
                    double distance = yourDist - myDist;
                    if (distance < dist) {
                        dist = distance;
                        upper = electron;
                    }
                }
            }
        }
        return upper;
    }

    public Electron getLowerNeighborInBranch(Electron myelectron) {
        Electron[] branchElectrons = getParticles(myelectron.getBranch());
        Electron lower = null;
        double dist = Double.POSITIVE_INFINITY;
        for (Electron electron : branchElectrons) {
            if (electron != myelectron) {
                double yourDist = electron.getDistAlongWire();
                double myDist = myelectron.getDistAlongWire();
                if (yourDist < myDist) {
                    double distance = myDist - yourDist;
                    if (distance < dist) {
                        dist = distance;
                        lower = electron;
                    }
                }
            }
        }
        return lower;
    }

    public ConstantDensityPropagator getPropagator() {
        return propagator;
    }
}