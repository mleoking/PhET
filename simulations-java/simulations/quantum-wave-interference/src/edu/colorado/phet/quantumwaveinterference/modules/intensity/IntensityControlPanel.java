/*  */
package edu.colorado.phet.quantumwaveinterference.modules.intensity;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.controls.*;
import edu.colorado.phet.quantumwaveinterference.util.ComponentCenterer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:25 AM
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
//        addControl( visualizationPanel );
        addControlFullWidth( visualizationPanel );
        new ComponentCenterer( resetButton, super.getContentPanel() ).start();
        ExpandableDoubleSlitPanel expandableDoubleSlitPanel = new ExpandableDoubleSlitPanel( intensityModule );
        setPreferredWidth( expandableDoubleSlitPanel.getControls().getPreferredSize().width + 10 );

        addControl( expandableDoubleSlitPanel );
        AdvancedPanel advancedPanel = new AdvancedPanel( QWIResources.getString( "controls.barriers.show" ), QWIResources.getString( "controls.barriers.hide" ) );
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
