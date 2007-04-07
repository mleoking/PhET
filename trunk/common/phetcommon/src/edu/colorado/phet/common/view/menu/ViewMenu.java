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
import edu.colorado.phet.common.PhetCommonProjectConfig;

import javax.swing.*;

/**
 * ViewMenu
 *
 * @author ?
 * @version $Revision$
 */
public class ViewMenu extends JMenu {

    public ViewMenu() {
        super( PhetCommonProjectConfig.getInstance().getString( "Common.ViewMenu.Title" ) );
        this.setMnemonic( PhetCommonProjectConfig.getInstance().getString( "Common.ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenu subMenu = new JMenu();
        subMenu.setText( PhetCommonProjectConfig.getInstance().getString( "Common.ViewMenu.LookandFeel" ) );
        subMenu.setMnemonic( PhetCommonProjectConfig.getInstance().getString( "Common.ViewMenu.LookandFeelMnemonic" ).charAt( 0 ) );

        // bold checkbox item
        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem();
        checkItem.setText( PhetCommonProjectConfig.getInstance().getString( "Common.ViewMenu.Test" ) );
        subMenu.add( checkItem );

        this.add( subMenu );
    }
}
