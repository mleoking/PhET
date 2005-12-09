/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:25 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityControlPanel extends SchrodingerControlPanel {
    public IntensityControlPanel( final IntensityModule intensityModule ) {
        super( intensityModule );
        addResetButton();
        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
        advancedPanel.addControlFullWidth( super.createPotentialPanel( getModule() ) );
        addControlFullWidth( advancedPanel );
    }
}
