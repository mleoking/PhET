/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.view.menu;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

import javax.swing.*;

/**
 * ViewMenu
 *
 * @author ?
 * @version $Revision$
 */
public class ViewMenu extends JMenu {

    public ViewMenu() {
        super( PhetCommonResources.getInstance().getLocalizedString( "Common.ViewMenu.Title" ) );
        this.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenu subMenu = new JMenu();
        subMenu.setText( PhetCommonResources.getInstance().getLocalizedString( "Common.ViewMenu.LookandFeel" ) );
        subMenu.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.ViewMenu.LookandFeelMnemonic" ).charAt( 0 ) );

        // bold checkbox item
        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem();
        checkItem.setText( PhetCommonResources.getInstance().getLocalizedString( "Common.ViewMenu.Test" ) );
        subMenu.add( checkItem );

        this.add( subMenu );
    }
}
