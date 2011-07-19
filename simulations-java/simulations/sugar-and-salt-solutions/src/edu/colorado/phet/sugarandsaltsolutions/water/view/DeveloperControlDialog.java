// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Window;

import javax.swing.JDialog;

import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;

/**
 * Dialog that shows the developer controls, when running in "-dev" mode and after the dev control button is pressed.
 *
 * @author Sam Reid
 */
public class DeveloperControlDialog extends JDialog {
    public DeveloperControlDialog( Window owner, WaterModel waterModel ) {
        super( owner );
        setContentPane( new DeveloperControl( waterModel ) );
        pack();
    }
}
