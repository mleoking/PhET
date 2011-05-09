// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:54:25 PM
 */
public class ConstantDensityLayout extends CircuitListenerAdapter {
    private boolean dolayout = true;
    private Circuit circuit;
    private ElectronSet particleSet;
    private boolean electronsVisible = true;

    public ConstantDensityLayout(Circuit circuit, ElectronSet particleSet) {
        this.circuit = circuit;
        this.particleSet = particleSet;
    }

    public void branchesMoved(Branch[] branches) {
        if (!dolayout) {
            return;
        }
        BranchSet bs = new BranchSet(getCircuit(), branches);
        for (int i = 0; i < branches.length; i++) {
            bs.addBranches(getCircuit().getStrongConnections(branches[i].getStartJunction()));
            bs.addBranches(getCircuit().getStrongConnections(branches[i].getEndJunction()));
        }
        Branch[] torelayout = bs.getBranches();
        layoutElectrons(torelayout);
    }

    private Circuit getCircuit() {
        return circuit;
    }

    public void layoutElectrons(Branch[] branches) {
        for (int i = 0; i < branches.length; i++) {
            Branch branch = branches[i];
            layoutElectrons(branch);
        }
    }

    private void layoutElectrons(Branch branch) {
        particleSet.removeParticles(branch);

        if (getElectronsVisible()) {
            double offset = CCKModel.ELECTRON_DX / 2;
            double endingPoint = branch.getLength() - offset;
            //compress or expand, but fix a particle at startingPoint and endingPoint.
            double L = endingPoint - offset;
            double desiredDensity = 1 / CCKModel.ELECTRON_DX;
            double N = L * desiredDensity;
            int integralNumberParticles = (int) Math.ceil(N);
            double mydensity = (integralNumberParticles - 1) / L;
            double dx = 1 / mydensity;
            if (mydensity == 0) {
                integralNumberParticles = 0;
            }
            for (int i = 0; i < integralNumberParticles; i++) {
                particleSet.addParticle(new Electron(branch, i * dx + offset));
            }
        }
    }

    private boolean getElectronsVisible() {
        return electronsVisible;
    }
}
