/**
 * Class: PhetFrame
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 * Testing comment.asdf
 */
package edu.colorado.phet.semiconductor.phetcommon.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.semiconductor.SemiconductorResources;
import edu.colorado.phet.semiconductor.phetcommon.application.PhetApplication;
import edu.colorado.phet.semiconductor.phetcommon.view.components.menu.HelpMenu;
import edu.colorado.phet.semiconductor.phetcommon.view.components.menu.PhetFileMenu;

public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    PhetApplication app;
    private GraphicsSetup graphicsSetup = new BasicGraphicsSetup();

    static {
        SimStrings.setStrings( "localization/SemiConductorPCStrings" );
    }

    public PhetFrame( PhetApplication app ) {
        super( app.getApplicationDescriptor().getWindowTitle() );
        this.app = app;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( this );
        JMenu controlMenu = new JMenu( SemiconductorResources.getString( "PhetFrame.ControlMenu" ) );
        JMenuItem showClockDialog = new JMenuItem( SemiconductorResources.getString( "PhetFrame.FixedClockMenuItem" ) );
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

    public PhetApplication getApp() {
        return app;
    }

    public void paint( Graphics g ) {
        if ( graphicsSetup != null ) {
            graphicsSetup.setup( (Graphics2D) g );
        }
        super.paint( g );
    }

    public void paintComponents( Graphics g ) {
        if ( graphicsSetup != null ) {  //TODO this doesn't work.  I can still see the bad antialias in the jmenubar.
            graphicsSetup.setup( (Graphics2D) g );
        }
        super.paintComponents( g );
    }

}
