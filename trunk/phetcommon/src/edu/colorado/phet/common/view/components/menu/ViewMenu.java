/**
 * Class: ViewMenu
 * Package: edu.colorado.phet.common.view.components.menu
 * Author: Another Guy
 * Date: Sep 4, 2003
 */
package edu.colorado.phet.common.view.components.menu;

import javax.swing.*;

public class ViewMenu extends JMenu {

    public ViewMenu() {
        super("View");
        this.setMnemonic('V');
//        JMenuItem menuItem = new JMenuItem( "Look and Feel" );
//        menuItem.setMnemonic( 'L' );
//        menuItem.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                new LookAndFeelMenu.
//            }
//        } );
        JMenu subMenu = new JMenu();
        subMenu.setText("Look and Feel");
        subMenu.setMnemonic('L');

        // bold checkbox item
        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem();
        checkItem.setText("test");
        subMenu.add(checkItem);

        this.add(subMenu);
    }
}
