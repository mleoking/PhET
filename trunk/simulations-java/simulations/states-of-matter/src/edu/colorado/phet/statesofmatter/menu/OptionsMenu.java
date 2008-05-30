/* Copyright 2008, University of Colorado */ 

package edu.colorado.phet.statesofmatter.menu;

import javax.swing.JMenu;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( NuclearPhysics2Resources.getString( "menu.options" ) );
        setMnemonic( NuclearPhysics2Resources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
