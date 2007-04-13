/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.menu;

import javax.swing.JMenu;

import edu.colorado.phet.opticaltweezers.OTResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( OTResources.getString( "menu.options" ) );
        setMnemonic( OTResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
