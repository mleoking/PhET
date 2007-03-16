/**
 * Class: ViewMenu
 * Package: edu.colorado.phet.common.view.components.menu
 * Author: Another Guy
 * Date: Sep 4, 2003
 */
package edu.colorado.phet.common_cck.view.components.menu;

import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;

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
