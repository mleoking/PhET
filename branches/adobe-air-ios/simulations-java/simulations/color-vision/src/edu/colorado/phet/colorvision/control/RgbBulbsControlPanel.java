// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.control;

import javax.swing.*;

import edu.colorado.phet.colorvision.ColorVisionConstants;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * RgbBulbsControlPanel is the control panel for the "RGB Bulbs" simulation module.
 * This control panel currently has no controls, but it's included so that the
 * play area will be the same size in all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RgbBulbsControlPanel extends ControlPanel {

    public RgbBulbsControlPanel() {

        super();

        JPanel fillerPanel = new JPanel();
        fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
        fillerPanel.add( Box.createHorizontalStrut( ColorVisionConstants.CONTROL_PANEL_MIN_WIDTH ) );

        // WORKAROUND: PhetControlPanel doesn't display anything unless we give it a dummy control pane.
        this.addControlFullWidth( fillerPanel );
    }

}