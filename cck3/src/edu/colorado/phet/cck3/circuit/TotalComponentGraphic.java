/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 9:05:19 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class TotalComponentGraphic {
    private CircuitComponentInteractiveGraphic interactiveBranchGraphic;
    private HasJunctionGraphic interactiveJunctionGraphic1;
    private HasJunctionGraphic interactiveJunctionGraphic2;

    public TotalComponentGraphic( CircuitGraphic circuitGraphic, final CircuitComponent branch, ApparatusPanel apparatusPanel, final ModelViewTransform2D transform, IComponentGraphic bg, double junctionRadius, CCKModule module ) {
        interactiveBranchGraphic = new CircuitComponentInteractiveGraphic( bg, circuitGraphic );

        JunctionGraphic jg = new JunctionGraphic( apparatusPanel, branch.getStartJunction(), transform, junctionRadius, circuitGraphic.getCircuit() );
        JunctionGraphic jg2 = new JunctionGraphic( apparatusPanel, branch.getEndJunction(), transform, junctionRadius, circuitGraphic.getCircuit() );
        if( circuitGraphic.getCircuit().getAdjacentBranches( branch.getStartJunction() ).length == 1 ) {
            interactiveJunctionGraphic1 = new InteractiveComponentJunctionGraphic( circuitGraphic, jg, branch, module );
        }
        else {
            interactiveJunctionGraphic1 = new InteractiveWireJunctionGraphic( circuitGraphic, jg, transform, module );
        }
        if( circuitGraphic.getCircuit().getAdjacentBranches( branch.getEndJunction() ).length == 1 ) {
            interactiveJunctionGraphic2 = new InteractiveComponentJunctionGraphic( circuitGraphic, jg2, branch, module );
        }
        else {
            interactiveJunctionGraphic2 = new InteractiveWireJunctionGraphic( circuitGraphic, jg2, transform, module );
        }
    }

    public CircuitComponentInteractiveGraphic getInteractiveBranchGraphic() {
        return interactiveBranchGraphic;
    }

    public HasJunctionGraphic getInteractiveJunctionGraphic1() {
        return interactiveJunctionGraphic1;
    }

    public HasJunctionGraphic getInteractiveJunctionGraphic2() {
        return interactiveJunctionGraphic2;
    }
}
