/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.util.VersionUtils;
import edu.colorado.phet.common.application.ApplicationModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {
    public HelpMenu( final ApplicationModel appDescriptor ) {
        //    public HelpMenu( final PhetApplication app ) {
        super( "Help" );
        this.setMnemonic( 'h' );

        final JMenuItem about = new JMenuItem( "About" );
        about.setMnemonic( 'a' );
        final String name = appDescriptor.getWindowTitle();
        String desc = appDescriptor.getDescription();
        String version = appDescriptor.getVersion();
        String message = name + "\n" + desc + "\nVersion: " + version;
        VersionUtils.VersionInfo inf = VersionUtils.readVersionInfo();
        message += "\nBuild Number: " + inf.getBuildNumber() + "\nBuild Time: " + inf.getBuildTime();
        final String msg = message;
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JOptionPane.showMessageDialog( about, msg, "About " + name, JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        add( about );
    }
}
