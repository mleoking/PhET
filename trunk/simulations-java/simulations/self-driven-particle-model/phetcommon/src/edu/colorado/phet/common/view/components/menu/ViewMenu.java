/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/components/menu/ViewMenu.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * ViewMenu
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class ViewMenu extends JMenu {

    public ViewMenu() {
        super( SimStrings.get( "Common.ViewMenu.Title" ) );
        this.setMnemonic( SimStrings.get( "Common.ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        //        JMenuItem menuItem = new JMenuItem( "Look and Feel" );
        //        menuItem.setMnemonic( 'L' );
        //        menuItem.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent e ) {
        //                new LookAndFeelMenu.
        //            }
        //        } );
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
