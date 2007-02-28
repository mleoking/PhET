/**
 * Class: PhetFrame
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 * Testing comment.asdf
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.components.menu.HelpMenu;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;

    static {
        SimStrings.setStrings( "localization/ConductivityPCStrings" );
    }
    
    public PhetFrame( PhetApplication app ) {
        super( app.getApplicationDescriptor().getWindowTitle() );
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( app );
        JMenu controlMenu = new JMenu( SimStrings.get( "PhetFrame.ControlMenu" ) );
        JMenuItem showClockDialog = new JMenuItem( SimStrings.get( "PhetFrame.FixedClockMenuItem" ) );
        showClockDialog.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                clockDialog.setVisible(true);
            }
        } );

        defaultFileMenu = new PhetFileMenu();
        menuBar.add( defaultFileMenu );
        menuBar.add( helpMenu );

        setJMenuBar( menuBar );

        app.getApplicationDescriptor().getFrameSetup().initialize( this );
    }

}
