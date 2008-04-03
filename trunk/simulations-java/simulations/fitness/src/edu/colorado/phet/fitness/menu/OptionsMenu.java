/* Copyright 2007, University of Colorado */

package edu.colorado.phet.fitness.menu;

import javax.swing.JMenu;

import edu.colorado.phet.fitness.FitnessResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( FitnessResources.getString( "menu.options" ) );
        setMnemonic( FitnessResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
