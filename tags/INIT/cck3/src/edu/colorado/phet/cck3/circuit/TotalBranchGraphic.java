/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 9:05:19 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class TotalBranchGraphic extends CompositeInteractiveGraphic {

    private InteractiveBranchGraphic interactiveBranchGraphic;
    private InteractiveWireJunctionGraphic interactiveJunctionGraphic1;
    private InteractiveWireJunctionGraphic interactiveJunctionGraphic2;
    private CircuitGraphic circuitGraphic;

    public TotalBranchGraphic( CircuitGraphic circuitGraphic, final Branch branch, ApparatusPanel apparatusPanel, final ModelViewTransform2D transform, Color color, double junctionRadius, CCK3Module module,double wireThickness ) {
        this.circuitGraphic = circuitGraphic;

        BranchGraphic bg = new BranchGraphic( branch, apparatusPanel, wireThickness, transform, color );
        interactiveBranchGraphic = new InteractiveBranchGraphic( circuitGraphic, bg, transform,module );

        JunctionGraphic jg = new JunctionGraphic( apparatusPanel, branch.getStartJunction(), transform, junctionRadius );
        JunctionGraphic jg2 = new JunctionGraphic( apparatusPanel, branch.getEndJunction(), transform, junctionRadius );
        interactiveJunctionGraphic1 = new InteractiveWireJunctionGraphic( circuitGraphic, jg, transform, module );
        interactiveJunctionGraphic2 = new InteractiveWireJunctionGraphic( circuitGraphic, jg2, transform, module );
        addGraphic( interactiveBranchGraphic );
        addGraphic( interactiveJunctionGraphic1 );
        addGraphic( interactiveJunctionGraphic2 );
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
