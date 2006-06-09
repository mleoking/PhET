/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.menu;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * ViewMenu
 *
 * @author ?
 * @version $Revision$
 */
public class ViewMenu extends JMenu {

    public ViewMenu() {
        super( SimStrings.get( "Common.ViewMenu.Title" ) );
        this.setMnemonic( SimStrings.get( "Common.ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenu subMenu = new JMenu();
        subMenu.setText( SimStrings.get( "Common.ViewMenu.LookandFeel" ) );
        subMenu.setMnemonic( SimStrings.get( "Common.ViewMenu.LookandFeelMnemonic" ).charAt( 0 ) );

        // bold checkbox item
        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem();
        checkItem.setText( SimStrings.get( "Common.ViewMenu.Test" ) );
        subMenu.add( checkItem );

        this.add( subMenu );
    }
}
