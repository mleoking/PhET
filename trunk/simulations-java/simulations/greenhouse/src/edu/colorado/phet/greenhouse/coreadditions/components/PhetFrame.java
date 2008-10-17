/**
 * Class: PhetFrame
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.greenhouse.phetcommon.application.PhetApplication;
import edu.colorado.phet.greenhouse.phetcommon.view.components.menu.HelpMenu;
import edu.colorado.phet.greenhouse.phetcommon.view.components.menu.PhetFileMenu;

public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    PhetApplication app;

    public PhetFrame( PhetApplication app ) {
        super( app.getApplicationDescriptor().getWindowTitle() );
        this.app = app;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = ( new HelpMenu( this,app.getApplicationDescriptor().getWindowTitle(),
                                        app.getApplicationDescriptor().getDescription(),
                                        app.getApplicationDescriptor().getVersion() ) );
        JMenu controlMenu = new JMenu( SimStrings.get( "PhetFrame.ControlMenuTitle" ) );
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

    public PhetApplication getApp() {
        return app;
    }

    /**
     * Adds a JMenu before the Help Menu.
     *
     * @param menu
     */
    public void addMenu( JMenu menu ) {
        SwingUtils.addMenuAt( menu, getJMenuBar(), getJMenuBar().getComponentCount() - 1 );
    }

    public void addFileMenuSeparator() {
        defaultFileMenu.insertSeparator( defaultFileMenu.getComponentCount() + 1 );
    }

    public void addFileMenuItem( JMenuItem menuItem ) {
        defaultFileMenu.insert( menuItem, defaultFileMenu.getComponentCount() );
    }

    public void removeFileMenuItem( JMenuItem menuItem ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if ( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }

    public void setFileMenu( PhetFileMenu defaultFileMenu ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if ( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }
}
