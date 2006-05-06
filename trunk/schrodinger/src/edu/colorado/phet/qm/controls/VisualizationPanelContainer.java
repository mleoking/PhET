/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

/**
 * User: Sam Reid
 * Date: May 5, 2006
 * Time: 9:56:45 AM
 * Copyright (c) May 5, 2006 by Sam Reid
 */

public class VisualizationPanelContainer extends VerticalLayoutPanel {
    public void setContent( IVisualizationPanel visualizationPanel ) {
        removeAll();
        add( visualizationPanel.getPanel() );
    }
}
