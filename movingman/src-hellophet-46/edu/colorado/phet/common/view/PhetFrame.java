/**
 * Class: PhetFrame
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;

    public PhetFrame( PhetApplication app ) {
        super( app.getApplicationDescriptor().getWindowTitle() );
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = ( new HelpMenu( app.getApplicationDescriptor().getWindowTitle(),
                                        app.getApplicationDescriptor().getDescription(),
                                        app.getApplicationDescriptor().getVersion() ) );
        JMenu controlMenu = new JMenu( "Control" );
        JMenuItem showClockDialog = new JMenuItem( "Clock" );
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

    /**
     * Adds a JMenu before the Help Menu.
     * @param menu
     */
    public void addMenu( JMenu menu ) {
        edu.colorado.phet.common.view.util.GraphicsUtil.addMenuAt( menu, getJMenuBar(), getJMenuBar().getComponentCount() - 1 );
    }
//    private int lastFileMenuAdditionIndex=0;
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

    public void setFileMenu( PhetFileMenu defaultFileMenu ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }
}
