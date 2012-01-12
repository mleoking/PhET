// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * User: Sam Reid
 * Date: May 5, 2006
 * Time: 9:56:45 AM
 */

public class VisualizationPanelContainer extends VerticalLayoutPanel {
    private IVisualizationPanel photonVisualizationPanel;
    private IVisualizationPanel particleVisualizationPanel;

    public VisualizationPanelContainer( IVisualizationPanel photonVisualizationPanel, IVisualizationPanel particleVisualizationPanel ) {
        this.photonVisualizationPanel = photonVisualizationPanel;
        this.particleVisualizationPanel = particleVisualizationPanel;
    }

    public void setContent( IVisualizationPanel visualizationPanel ) {
        removeAll();
        add( visualizationPanel.getPanel() );
        visualizationPanel.applyChanges();
    }

    public void setPhotonMode( boolean photonMode ) {
        setContent( photonMode ? photonVisualizationPanel : particleVisualizationPanel );
    }
}
