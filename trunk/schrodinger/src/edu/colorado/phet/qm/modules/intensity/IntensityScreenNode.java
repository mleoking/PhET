/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.controls.SlitControlPanel;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;

/**
 * User: Sam Reid
 * Date: Sep 18, 2005
 * Time: 1:09:00 PM
 * Copyright (c) Sep 18, 2005 by Sam Reid
 */

public class IntensityScreenNode extends SchrodingerScreenNode {
    private SlitControlPanel slitControlPanel;
    private PSwing slitControlGraphic;

    public IntensityScreenNode( SchrodingerModule intensityModule, IntensityPanel panel ) {
        super( panel );
        slitControlPanel = new SlitControlPanel( (IntensityModule)intensityModule );
        slitControlGraphic = new PSwing( panel, slitControlPanel );
        addChild( slitControlGraphic );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        PSwing ds = getDoubleSlitPanelGraphic();
        slitControlGraphic.setOffset( ds.getFullBounds().getX(), ds.getFullBounds().getY() + ds.getFullBounds().getHeight() + 5 );
    }

    public PSwing getSlitControlGraphic() {
        return slitControlGraphic;
    }

    public SlitControlPanel getSlitControlPanel() {
        return slitControlPanel;
    }
}
