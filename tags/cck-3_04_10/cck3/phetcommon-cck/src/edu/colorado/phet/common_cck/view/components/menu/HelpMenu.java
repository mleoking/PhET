/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common_cck.view.components.menu;

import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.util.VersionUtils;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    public HelpMenu( final ApplicationModel appDescriptor ) {
        super( SimStrings.get( "Common.HelpMenu.Title" ) );
        this.setMnemonic( SimStrings.get( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );

        final JMenuItem about = new JMenuItem( SimStrings.get( "Common.HelpMenu.About" ) );
        about.setMnemonic( SimStrings.get( "Common.HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        final String name = appDescriptor.getWindowTitle();
        String desc = appDescriptor.getDescription();
        String version = appDescriptor.getVersion();
        String message = name + "\n" + desc + "\n" + SimStrings.get( "Common.HelpMenu.VersionLabel" ) + ": " + version + "\n";
        try {
            VersionUtils.VersionInfo[] inf = VersionUtils.readVersionInfo( appDescriptor.getName() );

            for( int i = 0; i < inf.length; i++ ) {
                VersionUtils.VersionInfo versionInfo = inf[i];
                message += versionInfo.toString();
                if( i < inf.length ) {
                    message += "\n";
                }
            }
            message += "\n" + SimStrings.get( "Common.HelpMenu.JavaVersion" ) + ": " + System.getProperty( "java.version" ) + "\n" + SimStrings.get( "Common.HelpMenu.By" ) + " " + System.getProperty( "java.vendor" );
            final String msg = message;
            about.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JOptionPane.showMessageDialog( about, msg, SimStrings.get( "Common.HelpMenu.AboutTitle" ) + " " + name, JOptionPane.INFORMATION_MESSAGE );
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
