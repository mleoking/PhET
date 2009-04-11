/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.menu;

import javax.swing.JMenu;

import edu.colorado.phet.simtemplate.SimTemplateResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( SimTemplateResources.getString( "menu.options" ) );
        setMnemonic( SimTemplateResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
