/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.util.VersionUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    public HelpMenu( final ApplicationModel appDescriptor ) {
        super( "Help" );
        this.setMnemonic( 'h' );

        final JMenuItem about = new JMenuItem( "About" );
        about.setMnemonic( 'a' );
        final String name = appDescriptor.getWindowTitle();
        String desc = appDescriptor.getDescription();
        String version = appDescriptor.getVersion();
        String message = name + "\n" + desc + "\nVersion: " + version + "\n";
        try {
            VersionUtils.VersionInfo[] inf = VersionUtils.readVersionInfo( appDescriptor.getName() );

            for( int i = 0; i < inf.length; i++ ) {
                VersionUtils.VersionInfo versionInfo = inf[i];
                message += versionInfo.toString();
                if( i < inf.length ) {
                    message += "\n";
                }
            }
            message += "\nJava Version: " + System.getProperty( "java.version" ) + "\nby " + System.getProperty( "java.vendor" );
            final String msg = message;
            about.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JOptionPane.showMessageDialog( about, msg, "About " + name, JOptionPane.INFORMATION_MESSAGE );
                }
            } );

        }
        catch( Exception e ) {
//        catch( IOException e ) {
            e.printStackTrace();
            message += "Could not load version info, error=" + e.toString();
            StackTraceElement[] st = e.getStackTrace();
            int numElementsToShow = 5;
            for( int i = 0; i < numElementsToShow; i++ ) {
                StackTraceElement stackTraceElement = st[i];
                message += stackTraceElement.toString() + "\n";
            }
        }
        add( about );
    }
}
