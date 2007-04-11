package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Dec 1, 2006
 * Time: 12:32:22 PM
 * Copyright (c) Dec 1, 2006 by Sam Reid
 */

public class PanZoomOnscreenControl extends PhetPNode {
    private PanZoomControl panZoomControl;

    public PanZoomOnscreenControl( EnergySkateParkSimulationPanel phetPCanvas ) {
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
