/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.menu;

import javax.swing.JMenu;

import edu.colorado.phet.acidbasesolutions.ABSResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( ABSResources.getString( "menu.options" ) );
        setMnemonic( ABSResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
