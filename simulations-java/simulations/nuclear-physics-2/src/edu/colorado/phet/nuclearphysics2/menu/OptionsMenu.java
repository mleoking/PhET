/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.menu;

import javax.swing.JMenu;

import edu.colorado.phet.nuclearphysics2.TemplateResources;

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
