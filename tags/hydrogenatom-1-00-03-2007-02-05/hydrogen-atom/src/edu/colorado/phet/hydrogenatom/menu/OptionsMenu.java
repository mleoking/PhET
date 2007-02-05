/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.menu;

import javax.swing.JMenu;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.module.HAModule;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OptionsMenu extends JMenu {
    
    public OptionsMenu( HAModule module ) {
        super( SimStrings.get( "menu.options" ) );
        setMnemonic( SimStrings.get( "menu.options.mnemonic" ).charAt( 0 ) );
        
        if ( HAConstants.DEBROGLIE_VIEW_IN_MENUBAR ) {
            DeBroglieViewMenu deBroglieViewMenu = new DeBroglieViewMenu( module );
            add( deBroglieViewMenu );
        }
    }
}
