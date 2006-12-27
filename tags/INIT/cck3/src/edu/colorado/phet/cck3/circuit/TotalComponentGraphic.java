/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 9:05:19 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class TotalComponentGraphic extends CompositeInteractiveGraphic {
    private CircuitComponentInteractiveGraphic interactiveBranchGraphic;
    private InteractiveComponentJunctionGraphic interactiveJunctionGraphic1;
    private InteractiveComponentJunctionGraphic interactiveJunctionGraphic2;
    private CircuitGraphic circuitGraphic;

    public TotalComponentGraphic( CircuitGraphic circuitGraphic, final CircuitComponent branch, ApparatusPanel apparatusPanel, final ModelViewTransform2D transform, IComponentGraphic bg, double junctionRadius, CCK3Module module ) {
        this.circuitGraphic = circuitGraphic;

//        CircuitComponentImageGraphic bg = new CircuitComponentImageGraphic( image, apparatusPanel, branch, transform );
        interactiveBranchGraphic = new CircuitComponentInteractiveGraphic( bg, circuitGraphic );// circuitGraphic, bg, transform );

        JunctionGraphic jg = new JunctionGraphic( apparatusPanel, branch.getStartJunction(), transform, junctionRadius );
        JunctionGraphic jg2 = new JunctionGraphic( apparatusPanel, branch.getEndJunction(), transform, junctionRadius );
        interactiveJunctionGraphic1 = new InteractiveComponentJunctionGraphic( circuitGraphic, jg, branch, module );
        interactiveJunctionGraphic2 = new InteractiveComponentJunctionGraphic( circuitGraphic, jg2, branch, module );
        addGraphic( interactiveBranchGraphic );
        addGraphic( interactiveJunctionGraphic1 );
        addGraphic( interactiveJunctionGraphic2 );
    }

    public CircuitComponentInteractiveGraphic getInteractiveBranchGraphic() {
        return interactiveBranchGraphic;
    }

    public InteractiveComponentJunctionGraphic getInteractiveJunctionGraphic1() {
        return interactiveJunctionGraphic1;
    }

    public InteractiveComponentJunctionGraphic getInteractiveJunctionGraphic2() {
        return interactiveJunctionGraphic2;
    }
}
