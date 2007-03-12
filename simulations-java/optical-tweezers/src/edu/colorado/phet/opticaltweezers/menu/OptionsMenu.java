/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.menu;

import javax.swing.JMenu;

import edu.colorado.phet.common.view.util.SimStrings;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( SimStrings.get( "menu.options" ) );
        setMnemonic( SimStrings.get( "menu.options.mnemonic" ).charAt( 0 ) );
    }
}
