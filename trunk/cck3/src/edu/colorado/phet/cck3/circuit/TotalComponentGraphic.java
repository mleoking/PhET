/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 9:05:19 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class TotalComponentGraphic extends CompositeGraphic {
    private CircuitComponentInteractiveGraphic interactiveBranchGraphic;
    private Graphic interactiveJunctionGraphic1;
    private Graphic interactiveJunctionGraphic2;
    private CircuitGraphic circuitGraphic;

    public TotalComponentGraphic( CircuitGraphic circuitGraphic, final CircuitComponent branch, ApparatusPanel apparatusPanel, final ModelViewTransform2D transform, IComponentGraphic bg, double junctionRadius, CCK3Module module ) {
        this.circuitGraphic = circuitGraphic;
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

        addGraphic( interactiveBranchGraphic );
        addGraphic( interactiveJunctionGraphic1 );
        addGraphic( interactiveJunctionGraphic2 );
    }

    public CircuitComponentInteractiveGraphic getInteractiveBranchGraphic() {
        return interactiveBranchGraphic;
    }

    public Graphic getInteractiveJunctionGraphic1() {
        return interactiveJunctionGraphic1;
    }

    public Graphic getInteractiveJunctionGraphic2() {
        return interactiveJunctionGraphic2;
    }
}
