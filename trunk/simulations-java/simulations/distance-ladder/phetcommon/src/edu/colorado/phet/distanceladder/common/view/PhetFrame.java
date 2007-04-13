/**
 * Class: PhetFrame
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 * Testing comment.asdf
 */
package edu.colorado.phet.distanceladder.common.view;

import edu.colorado.phet.distanceladder.common.application.PhetApplication;
import edu.colorado.phet.distanceladder.common.view.components.menu.HelpMenu;
import edu.colorado.phet.distanceladder.common.view.components.menu.PhetFileMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    PhetApplication app;
    private GraphicsSetup graphicsSetup = new BasicGraphicsSetup();

    public PhetFrame( PhetApplication app ) {
        super( app.getApplicationDescriptor().getWindowTitle() );
        this.app = app;
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( app );
        JMenu controlMenu = new JMenu( "Control" );
        JMenuItem showClockDialog = new JMenuItem( "FixedClock" );
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

    /**
     * Adds a JMenu before the Help Menu.
     *
     * @param menu
     */
    public void addMenu( JMenu menu ) {
        edu.colorado.phet.distanceladder.common.view.util.GraphicsUtil.addMenuAt( menu, getJMenuBar(), getJMenuBar().getComponentCount() - 1 );
    }

    public void addFileMenuSeparator() {
        defaultFileMenu.insertSeparator( defaultFileMenu.getComponentCount() + 1 );
    }

    public void addFileMenuItem( JMenuItem menuItem ) {
        defaultFileMenu.insert( menuItem, defaultFileMenu.getComponentCount() );
    }

    public void removeFileMenuItem( JMenuItem menuItem ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }

    public void paint( Graphics g ) {
        if( graphicsSetup != null ) {
            graphicsSetup.setup( (Graphics2D)g );
        }
        super.paint( g );
    }

    public void paintComponents( Graphics g ) {
        if( graphicsSetup != null ) {  //TODO this doesn't work.  I can still see the bad antialias in the jmenubar.
            graphicsSetup.setup( (Graphics2D)g );
        }
        super.paintComponents( g );
    }

    public void setFileMenu( PhetFileMenu defaultFileMenu ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }
}
