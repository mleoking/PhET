/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.VersionUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    public HelpMenu( final PhetApplication app ) {
        super( "Help" );
        final JMenuItem about = new JMenuItem( "About" );
        about.setMnemonic( 'a' );
        this.setMnemonic( 'h' );
        final String name = app.getApplicationDescriptor().getWindowTitle();
        String desc = app.getApplicationDescriptor().getDescription();
        String version = app.getApplicationDescriptor().getVersion();
        String message = name + "\n" + desc + "\nVersion: " + version;
        VersionUtils.VersionInfo inf = VersionUtils.readVersionInfo( app );
        if( inf == null ) {
            message += "No Version Info";
        }
        else {
            message += "\nBuild Number: " + inf.getBuildNumber() + "\nBuild Time: " + inf.getBuildTime();
        }
        final String msg = message;
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( about, msg, "About " + name, JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( about );
    }
}
