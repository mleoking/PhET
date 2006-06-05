/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.controls.*;
import edu.colorado.phet.qm.util.ComponentCenterer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:25 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityControlPanel extends QWIControlPanel {
    private IntensityModule intensityModule;
    private ParticleVisualizationPanel particleVisualizationPanel;
    private VisualizationPanelContainer visualizationPanel;
    private IVisualizationPanel photonVisualizationPanel;

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

        particleVisualizationPanel = new ParticleVisualizationPanel( getSchrodingerPanel() );
        photonVisualizationPanel = new PhotonVisualizationPanel( getSchrodingerPanel() );
        intensityModule.addListener( new QWIModule.Listener() {
            public void deactivated() {
            }

            public void activated() {
            }

            public void beamTypeChanged() {
                updateVisualizationPanel();
            }
        } );

        visualizationPanel = new VisualizationPanelContainer();
//        addControl( particleVisualizationPanel );
        addControl( visualizationPanel );
        updateVisualizationPanel();
        new ComponentCenterer( resetButton, super.getContentPanel() ).start();
        ExpandableDoubleSlitPanel expandableDoubleSlitPanel = new ExpandableDoubleSlitPanel( intensityModule );
        setPreferredWidth( expandableDoubleSlitPanel.getControls().getPreferredSize().width + 10 );

        addControl( expandableDoubleSlitPanel );
        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
        advancedPanel.addControlFullWidth( new PotentialPanel( getModule() ) );
        addControl( advancedPanel );

        updateVisualizationPanel();
    }

    private void updateVisualizationPanel() {
        boolean photon = intensityModule.getIntensityPanel().getHighIntensityGun().isPhotonMode();
        visualizationPanel.setContent( photon ? photonVisualizationPanel : particleVisualizationPanel );
//        visualizationPanel.setContent( particleVisualizationPanel );
//        HighIntensityGunGraphic gun = intensityModule.getIntensityPanel().getHighIntensityGun();
//        particleVisualizationPanel.setPhaseColorEnabled( !gun.isPhotonMode() );
//        visualizationPanel.setPhaseColorEnabled
    }


}
