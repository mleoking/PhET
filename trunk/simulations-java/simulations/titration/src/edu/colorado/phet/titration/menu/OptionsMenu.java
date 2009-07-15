/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.menu;

import javax.swing.JMenu;

import edu.colorado.phet.titration.TitrationResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( TitrationResources.getString( "menu.options" ) );
        setMnemonic( TitrationResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
