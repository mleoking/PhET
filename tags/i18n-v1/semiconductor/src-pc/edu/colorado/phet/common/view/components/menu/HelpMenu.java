/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.VersionUtils;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    
    static {
        SimStrings.setStrings( "localization/SemiConductorPCStrings" );
    }

    public HelpMenu( final PhetApplication app ) {
        super( SimStrings.get( "HelpMenu.HelpMenu" ) );
        this.setMnemonic( SimStrings.get( "HelpMenu.HelpMnemonic" ).charAt( 0 ) );

        final JMenuItem about = new JMenuItem( SimStrings.get( "HelpMenu.AboutMenuItem" ) );
        about.setMnemonic( SimStrings.get( "HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        final String name = app.getApplicationDescriptor().getWindowTitle();
        String desc = app.getApplicationDescriptor().getDescription();
        String version = app.getApplicationDescriptor().getVersion();
        String message = name + "\n" + desc + "\n" + SimStrings.get( "HelpMenu.VersionLabel" ) + ": " + version;
        VersionUtils.VersionInfo inf = VersionUtils.readVersionInfo( app );
        message += "\n" + SimStrings.get( "HelpMenu.BuilderNumberLabel" ) + ": " + inf.getBuildNumber();
        message += "\n" + SimStrings.get( "HelpMenu.BuilderTimeLabel" ) + ": " + inf.getBuildTime();
        final String msg = message;
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( about, msg, SimStrings.get( "HelpMenu.AboutLabel" ) + " " + name, JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( about );
    }
}
