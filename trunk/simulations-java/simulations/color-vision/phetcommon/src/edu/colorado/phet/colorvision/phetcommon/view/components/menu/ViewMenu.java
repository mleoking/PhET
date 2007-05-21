/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */
package edu.colorado.phet.colorvision.phetcommon.view.components.menu;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;

/**
 * ViewMenu
 *
 * @author Ron LeMaster
 * @version $Revision:14443 $
 */
public class ViewMenu extends JMenu {

    public ViewMenu() {
        super( SimStrings.get( "Common.ViewMenu.Title" ) );
        this.setMnemonic( SimStrings.get( "Common.ViewMenu.TitleMnemonic" ).charAt(0) );
        //        JMenuItem menuItem = new JMenuItem( "Look and Feel" );
        //        menuItem.setMnemonic( 'L' );
        //        menuItem.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent e ) {
        //                new LookAndFeelMenu.
        //            }
        //        } );
        JMenu subMenu = new JMenu();
        subMenu.setText( SimStrings.get( "Common.ViewMenu.LookandFeel" ) );
        subMenu.setMnemonic( SimStrings.get( "Common.ViewMenu.LookandFeelMnemonic" ).charAt(0) );

        // bold checkbox item
        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem();
        checkItem.setText( SimStrings.get( "Common.ViewMenu.Test" ) );
        subMenu.add( checkItem );

        this.add( subMenu );
    }
}
