/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.menu;

import javax.swing.JMenu;

import edu.colorado.phet.simtemplate.TemplateResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( TemplateResources.getString( "menu.options" ) );
        setMnemonic( TemplateResources.getChar( "menu.options.mnemonic", 'O' ) );
    }
}
