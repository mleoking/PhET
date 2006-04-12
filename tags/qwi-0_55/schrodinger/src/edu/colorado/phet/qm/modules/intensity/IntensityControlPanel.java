/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.controls.*;
import edu.colorado.phet.qm.util.ComponentCenterer;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:25 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityControlPanel extends SchrodingerControlPanel {
    private IntensityModule intensityModule;
    private VisualizationPanel visualizationPanel;

    public IntensityControlPanel( final IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        addSeparator();
        addSpacer();
        ResetButton resetButton = new ResetButton( intensityModule );
        getContentPanel().setAnchor( GridBagConstraints.CENTER );
        addControl( resetButton );
        addControl( new ClearButton( intensityModule.getSchrodingerPanel() ) );
        getContentPanel().setAnchor( GridBagConstraints.WEST );
        addSpacer();
        addSeparator();
        addSpacer();

        visualizationPanel = new VisualizationPanel( getSchrodingerPanel() );
        intensityModule.addListener( new SchrodingerModule.Listener() {
            public void deactivated() {
            }

            public void activated() {
            }

            public void beamTypeChanged() {
                updateVisualizationPanel();
            }
        } );
        updateVisualizationPanel();
        addControl( visualizationPanel );

        new ComponentCenterer( resetButton, super.getContentPanel() ).start();
        ExpandableDoubleSlitPanel expandableDoubleSlitPanel = new ExpandableDoubleSlitPanel( intensityModule );
        setPreferredWidth( expandableDoubleSlitPanel.getControls().getPreferredSize().width + 10 );

        addControl( expandableDoubleSlitPanel );
        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
        advancedPanel.addControlFullWidth( new PotentialPanel( getModule() ) );
        addControl( advancedPanel );
    }

    private void updateVisualizationPanel() {
        HighIntensityGunGraphic gun = intensityModule.getIntensityPanel().getHighIntensityGun();
        visualizationPanel.setPhaseColorEnabled( !gun.isPhotonMode() );
    }


}
