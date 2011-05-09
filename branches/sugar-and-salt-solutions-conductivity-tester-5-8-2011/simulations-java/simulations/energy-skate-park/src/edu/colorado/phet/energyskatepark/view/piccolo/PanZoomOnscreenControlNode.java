// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.colorado.phet.energyskatepark.view.swing.PanZoomControl;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Dec 1, 2006
 * Time: 12:32:22 PM
 */

public class PanZoomOnscreenControlNode extends PhetPNode {
    private PanZoomControl panZoomControl;

    public PanZoomOnscreenControlNode( EnergySkateParkSimulationPanel phetPCanvas ) {
        panZoomControl = new PanZoomControl( phetPCanvas );
        addChild( new PSwing( panZoomControl ) );
    }

    public void reset() {
        panZoomControl.reset();
    }

    public void updateScale() {
        panZoomControl.updateScale();
    }
}
