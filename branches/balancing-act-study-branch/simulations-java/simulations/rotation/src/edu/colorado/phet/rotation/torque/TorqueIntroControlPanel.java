// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.rotation.controls.ResetButton;
import edu.colorado.phet.rotation.controls.ShowVectorsControl;

/**
 * Created by: Sam
 * Oct 31, 2007 at 11:34:53 PM
 */
public class TorqueIntroControlPanel extends JPanel {
    public TorqueIntroControlPanel( final TorqueIntroModule torqueModule ) {
        JPanel checkBoxPanel = new VerticalLayoutPanel();
        checkBoxPanel.add( new ShowVectorsControl( torqueModule.getVectorViewModel() ) );
        checkBoxPanel.add( new ResetButton( torqueModule ) );
        add( checkBoxPanel );
    }
}
