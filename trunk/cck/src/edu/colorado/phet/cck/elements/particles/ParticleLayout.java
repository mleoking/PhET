/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.particles;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.JunctionGroup;
import edu.colorado.phet.cck.elements.junction.Junction;

/**
 * User: Sam Reid
 * Date: Oct 23, 2003
 * Time: 9:31:49 PM
 * Copyright (c) Oct 23, 2003 by Sam Reid
 */
public class ParticleLayout {
    double distBetweenElectrons;

    public ParticleLayout(double distBetweenElectrons) {
        this.distBetweenElectrons = distBetweenElectrons;
    }

//    public void layout2(Branch newElm, ParticleSet ps) {
//        double electronDX = newElm.getLength() / 10.0;
//        double electronX = electronDX;
//        while (electronX < newElm.getLength()) {
//            BranchParticle bp = new BranchParticle(newElm);
//            ps.addParticle(bp);
//            bp.setPosition(electronX);
//            electronX += electronDX;
//        }
//    }
    public void layout(Circuit circuit, ParticleSet ps) {
        for (int i = 0; i < circuit.numBranches(); i++) {
            layout(circuit.branchAt(i), ps);
        }
    }

    public void layout(Branch newElm, ParticleSet ps) {

        ps.removeParticlesForBranch(newElm);

//        double electronX = 0;
        double electronX = distBetweenElectrons;
        while (electronX < newElm.getLength()) {
            BranchParticle bp = new BranchParticle(newElm);
            ps.addParticle(bp);
            bp.setPosition(electronX);
            electronX += distBetweenElectrons;
        }
    }

    public void layout(Circuit circuit, Junction junction, ParticleSet particleSet) {

        JunctionGroup jg = circuit.getJunctionGroup(junction);
        Branch[] br = circuit.getBranches(jg, new ObjectSelector() {
            public boolean isValid(Object o) {
                return true;
            }
        });
        for (int i = 0; i < br.length; i++) {
            Branch branch2 = br[i];
//            relayoutElectrons(branch2);
            layout(branch2, particleSet);
        }
    }
}
