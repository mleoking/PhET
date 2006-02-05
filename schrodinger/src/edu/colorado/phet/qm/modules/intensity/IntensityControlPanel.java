/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.controls.ClearButton;
import edu.colorado.phet.qm.controls.ExpandableDoubleSlitPanel;
import edu.colorado.phet.qm.controls.ResetButton;
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
        addControl( new ResetButton( intensityModule ) );
        addControl( new ClearButton( intensityModule.getSchrodingerPanel() ) );
        addControl( new ExpandableDoubleSlitPanel( intensityModule ) );
        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
        advancedPanel.addControlFullWidth( super.createPotentialPanel( getModule() ) );
//        advancedPanel.addControlFullWidth( new InverseSlitsCheckbox( getSchrodingerPanel() ) );
        addControlFullWidth( advancedPanel );


    }

//    public SlitDetectorPanel getSlitDetectorPanel() {
//        return getDoubleSlitPanel().getSlitDetectorPanel();
//    }
}
