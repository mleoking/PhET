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

import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.module.HAModule;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAOptionsMenu extends OptionsMenu {
    
    public HAOptionsMenu( HAModule module ) {
        super();
        
        if ( HAConstants.DEBROGLIE_VIEW_IN_MENUBAR ) {
            DeBroglieViewMenu deBroglieViewMenu = new DeBroglieViewMenu( module );
            add( deBroglieViewMenu );
        }
    }
}
