// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.controls;

import javax.swing.JPanel;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;

/**
 * User: Sam Reid
 * Date: Dec 16, 2006
 * Time: 10:15:13 AM
 */
public class ShowReadoutPanel extends JPanel {
    public ShowReadoutPanel( final CCKModule module ) {
        add( new PropertyCheckBox( CCKResources.getString( "CCK3ControlPanel.ShowValuesCheckBox" ), module.getCCKViewState().getReadoutsVisibleProperty() ) );
    }
}
