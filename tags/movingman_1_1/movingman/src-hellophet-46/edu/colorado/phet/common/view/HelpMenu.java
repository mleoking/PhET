/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    public HelpMenu( final String name, String description, String version ) {
        super( "Help" );
        final JMenuItem about = new JMenuItem( "About" );
        about.setMnemonic( 'a' );
        this.setMnemonic( 'h' );
        final String message = name + "\n" + description + "\nVersion: " + version;
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( about, message, "About " + name, JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( about );
    }
}
