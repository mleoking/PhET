/* Copyright 2007, University of Colorado */

package edu.colorado.phet.naturalselection.menu;

import javax.swing.JMenu;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( NaturalSelectionResources.getString( "menu.options" ) );
        setMnemonic( NaturalSelectionResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
