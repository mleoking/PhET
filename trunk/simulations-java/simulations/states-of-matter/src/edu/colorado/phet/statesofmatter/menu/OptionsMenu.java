/* Copyright 2008, University of Colorado */ 

package edu.colorado.phet.statesofmatter.menu;

import javax.swing.JMenu;

import edu.colorado.phet.statesofmatter.StatesOfMatterResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( StatesOfMatterResources.getString( "menu.options" ) );
        setMnemonic( StatesOfMatterResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
