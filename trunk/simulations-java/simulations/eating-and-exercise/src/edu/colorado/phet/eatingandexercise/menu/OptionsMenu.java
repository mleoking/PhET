/* Copyright 2007, University of Colorado */

package edu.colorado.phet.eatingandexercise.menu;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {

    public OptionsMenu() {
        super( EatingAndExerciseResources.getString( "menu.options" ) );
        setMnemonic( EatingAndExerciseResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
