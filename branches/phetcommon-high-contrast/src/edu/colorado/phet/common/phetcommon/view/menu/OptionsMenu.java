/* Copyright 2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.menu;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu() {
        super( PhetCommonResources.getString( "Common.OptionsMenu" ) );
        setMnemonic( PhetCommonResources.getChar( "Common.OptionsMenu.mnemonic", 'O' ) );
    }
}
