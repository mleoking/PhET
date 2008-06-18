/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.menu;

import javax.swing.JMenu;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( NuclearPhysicsResources.getString( "menu.options" ) );
        setMnemonic( NuclearPhysicsResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
