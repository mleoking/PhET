/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.controls.*;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
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

        photonVisualizationPanel = new PhotonVisualizationPanel( getSchrodingerPanel() );
        particleVisualizationPanel = new ParticleVisualizationPanel( getSchrodingerPanel() );
        intensityModule.addListener( new IntensityModule.Adapter() {
            public void detectorsChanged() {
                if( intensityModule.isLeftDetectorEnabled() || intensityModule.isRightDetectorEnabled() ) {
                    particleVisualizationPanel.setMagnitudeMode();
                    particleVisualizationPanel.setPhaseColorEnabled( false );
                }
                else {
                    particleVisualizationPanel.setLastUserSelected();
                    particleVisualizationPanel.setPhaseColorEnabled( true );
                }
            }
        } );
        intensityModule.addListener( new QWIModule.Listener() {
            public void deactivated() {
            }

            public void activated() {
            }

            public void beamTypeChanged() {
                updateVisualizationPanel();
            }
        } );

        visualizationPanel = new VisualizationPanelContainer( photonVisualizationPanel, particleVisualizationPanel );
//        addControl( particleVisualizationPanel );
        addControl( visualizationPanel );
        new ComponentCenterer( resetButton, super.getContentPanel() ).start();
        ExpandableDoubleSlitPanel expandableDoubleSlitPanel = new ExpandableDoubleSlitPanel( intensityModule );
        setPreferredWidth( expandableDoubleSlitPanel.getControls().getPreferredSize().width + 10 );

        addControl( expandableDoubleSlitPanel );
        AdvancedPanel advancedPanel = new AdvancedPanel( QWIStrings.getString( "potential.barriers" ), QWIStrings.getString( "hide.potential.barriers" ) );
        advancedPanel.addControlFullWidth( new PotentialPanel( getModule() ) );
        addControl( advancedPanel );

        updateVisualizationPanel();
    }

    private void updateVisualizationPanel() {
        visualizationPanel.setPhotonMode( isPhotonMode() );
    }

    private boolean isPhotonMode() {
        return intensityModule.getIntensityPanel().getHighIntensityGun().isPhotonMode();
    }


}
