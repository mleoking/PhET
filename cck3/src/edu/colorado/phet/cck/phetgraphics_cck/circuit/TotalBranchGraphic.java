/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.CCKModule;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 9:05:19 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class TotalBranchGraphic {

    private InteractiveBranchGraphic interactiveBranchGraphic;
    private InteractiveWireJunctionGraphic interactiveJunctionGraphic1;
    private InteractiveWireJunctionGraphic interactiveJunctionGraphic2;

    public TotalBranchGraphic( CircuitGraphic circuitGraphic, final Branch branch, ApparatusPanel apparatusPanel, final ModelViewTransform2D transform, Color color, double junctionRadius, CCKModule module, double wireThickness ) {
        BranchGraphic bg = new BranchGraphic( branch, apparatusPanel, wireThickness, transform, color );
        interactiveBranchGraphic = new InteractiveBranchGraphic( circuitGraphic, bg, transform, module );

        JunctionGraphic jg = new JunctionGraphic( apparatusPanel, branch.getStartJunction(), transform, junctionRadius, circuitGraphic.getCircuit() );
        JunctionGraphic jg2 = new JunctionGraphic( apparatusPanel, branch.getEndJunction(), transform, junctionRadius, circuitGraphic.getCircuit() );
        interactiveJunctionGraphic1 = new InteractiveWireJunctionGraphic( circuitGraphic, jg, transform, module );
        interactiveJunctionGraphic2 = new InteractiveWireJunctionGraphic( circuitGraphic, jg2, transform, module );
    }

    public InteractiveBranchGraphic getInteractiveBranchGraphic() {
        return interactiveBranchGraphic;
    }

    public InteractiveWireJunctionGraphic getInteractiveJunctionGraphic1() {
        return interactiveJunctionGraphic1;
    }

    public InteractiveWireJunctionGraphic getInteractiveJunctionGraphic2() {
        return interactiveJunctionGraphic2;
    }
}
