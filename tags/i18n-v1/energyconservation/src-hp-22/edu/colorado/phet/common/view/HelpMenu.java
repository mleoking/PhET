/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    static {
        SimStrings.setStrings( "localization/HP-22Strings" );
    }
    
    public HelpMenu( final String name, String description, String version ) {
        super( SimStrings.get( "HelpMenu.MenuTitle" ) );
        final JMenuItem about = new JMenuItem( SimStrings.get( "HelpMenu.AboutMenuItem" ) );
        about.setMnemonic( SimStrings.get( "HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        this.setMnemonic( SimStrings.get( "HelpMenu.MenuMnemonic" ).charAt( 0 ) );
        final String message = name + "\n" + description + "\n"
                                    + SimStrings.get( "HelpMenu.VersionLabel" ) + ": " + version;
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( about, message, SimStrings.get( "HelpMenu.AboutLabel" )
                                                        + " " + name, JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( about );
    }
}
