/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.controls.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:25 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityControlPanel extends SchrodingerControlPanel {
    public IntensityControlPanel( final IntensityModule intensityModule ) {
        super( intensityModule );
        addControl( new ResetButton( intensityModule ) );
        addControl( new ClearButton( intensityModule.getSchrodingerPanel() ) );
        ExpandableDoubleSlitPanel expandableDoubleSlitPanel = new ExpandableDoubleSlitPanel( intensityModule );
        addControl( expandableDoubleSlitPanel );
        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
        advancedPanel.addControlFullWidth( new PotentialPanel( getModule() ) );
        addControl( advancedPanel );
        setPreferredWidth( expandableDoubleSlitPanel.getControls().getPreferredSize().width + 10 );
    }

}
