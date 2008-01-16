/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.menu;

import javax.swing.JMenu;

import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( GlaciersStrings.MENU_OPTIONS );
        setMnemonic( GlaciersStrings.MENU_OPTIONS_MNEMONIC );
    }
}
