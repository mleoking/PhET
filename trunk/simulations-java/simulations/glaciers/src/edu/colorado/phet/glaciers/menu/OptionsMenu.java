/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.menu;

import javax.swing.JMenu;

import edu.colorado.phet.glaciers.GlaciersResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( GlaciersResources.getString( "menu.options" ) );
        setMnemonic( GlaciersResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
