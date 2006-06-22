/**
 * Class: PhetFrame
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 * Testing comment.asdf
 */
package edu.colorado.phet.common_cck.view;

import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.view.components.menu.HelpMenu;
import edu.colorado.phet.common_cck.view.components.menu.PhetFileMenu;
import edu.colorado.phet.common_cck.view.util.SwingUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;

    public PhetFrame( ApplicationModel appDescriptor ) {
        super( appDescriptor.getWindowTitle() );
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }

        } );
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( appDescriptor );
        defaultFileMenu = new PhetFileMenu();
        menuBar.add( defaultFileMenu );
        menuBar.add( helpMenu );
        setJMenuBar( menuBar );
        appDescriptor.getFrameSetup().initialize( this );
    }

    public void setVisible( boolean b ) {
        super.setVisible( b );
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
