/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom;

import javax.swing.JMenu;

import edu.colorado.phet.hydrogenatom.module.HAModule;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains developer-only features for tuning and debugging.
 * To make this menu visible, start the program with the -dev command line arg. 
 * This menu is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperMenu extends JMenu {
    
    public DeveloperMenu( HAModule module ) {
        super( "Developer" );
        setMnemonic( 'D' );
    }
}
