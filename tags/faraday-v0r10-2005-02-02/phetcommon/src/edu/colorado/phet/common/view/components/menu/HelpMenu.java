/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.util.VersionUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * HelpMenu
 *
 * @author ?
 * @version $Revision$
 */
public class HelpMenu extends JMenu {
    private ImageIcon icon;

    public HelpMenu( final ApplicationModel appDescriptor ) throws IOException {
        super( SimStrings.get( "Common.HelpMenu.Title" ) );

        icon = new ImageIcon( ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" ) );

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

                    JOptionPane.showMessageDialog( about, msg, SimStrings.get( "Common.HelpMenu.AboutTitle" ) + " " + name, JOptionPane.INFORMATION_MESSAGE, icon );
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
