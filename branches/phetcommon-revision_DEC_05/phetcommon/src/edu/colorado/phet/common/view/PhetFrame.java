/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.components.menu.HelpMenu;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * PhetFrame
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFrame extends JFrame {
    private HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    private PhetApplication application;
    private FrameSetup frameSetup;

    /**
     * todo: make clock control panel useage module-specific
     *
     * @param title
     * @param frameSetup
     * @throws HeadlessException
     */
    public PhetFrame( PhetApplication application, String title, FrameSetup frameSetup,
                      final ModuleManager moduleManager,
                      String description, String version ) throws HeadlessException {
        super( title + " (" + version + ")" );
        this.application = application;
        this.frameSetup = frameSetup;

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.addWindowListener( new WindowAdapter() {

            // Pause the clock if the simulation window is iconified.
            public void windowIconified( WindowEvent e ) {
                moduleManager.pause();
            }

            // Restore the clock state if the simulation window is deiconified.
            public void windowDeiconified( WindowEvent e ) {
                moduleManager.resume();
            }
        } );

        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( moduleManager, title, description, version );
        defaultFileMenu = new PhetFileMenu();
        menuBar.add( defaultFileMenu );
        menuBar.add( helpMenu );
        setJMenuBar( menuBar );

        if( frameSetup != null ) {
            frameSetup.initialize( this );
        }

    }

    public void setModules( Module[] modules ) {
        JComponent contentPane = createContentPane( application, modules );
//        modulePanel = new ModulePanel( apparatusPanelContainer, null, null, null );
        setContentPane( contentPane );
    }

    /**
     * If we have a frame setup, we should not pack the frame
     */
    public void pack() {
        if( frameSetup == null ) {
            super.pack();
        }
    }

    public PhetApplication getApplication() {
        return application;
    }

    /**
     * Creates the JContainer that holds the apparatus panel(s) for the application. If the application
     * has only one module, the JContainer is a JPanel. If there is more than one, it is an instance
     * of a class in PhetCommon that manages a JTabbedPane and the appartus panels for the various
     * modules.
     *
     * @param application
     * @param modules
     * @return the container
     */
    private JComponent createContentPane( PhetApplication application, Module[] modules ) {
        JComponent apparatusPanelContainer = null;
        if( modules.length == 1 ) {
            return modules[0].getModulePanel();
        }
        else {
            apparatusPanelContainer = new TabbedModulePane( application, modules );
        }
        return apparatusPanelContainer;
    }

    //----------------------------------------------------------------
    // Menu setup methods
    //----------------------------------------------------------------

    /**
     * Adds a JMenu before the Help Menu.
     *
     * @param menu
     */
    public void addMenu( JMenu menu ) {
        SwingUtils.addMenuAt( menu, getJMenuBar(), getJMenuBar().getMenuCount() - 1 );
    }

    /**
     * Adds a menu separator to the File menu, just before the Exit menu item.
     */
    public void addFileMenuSeparator() {
        defaultFileMenu.insertSeparator( defaultFileMenu.getMenuComponentCount() - 1 );
    }

    /**
     * Adds a menu separator to the File menu after a specified menu item
     *
     * @param menuItem
     */
    public void addFileMenuSeparatorAfter( JMenuItem menuItem ) {
        JMenu fileMenu = getFileMenu();
        if( fileMenu != null ) {
            for( int i = 0; i < fileMenu.getItemCount(); i++ ) {
                if( fileMenu.getItem( i ) == menuItem ) {
                    fileMenu.insertSeparator( i + 1 );
                    return;
                }
            }
        }
    }

    /**
     * Adds a menu separator to the File menu before a specified menu item
     *
     * @param menuItem
     */
    public void addFileMenuSeparatorBefore( JMenuItem menuItem ) {
        JMenu fileMenu = getFileMenu();
        if( fileMenu != null ) {
            for( int i = 0; i < fileMenu.getItemCount(); i++ ) {
                if( fileMenu.getItem( i ) == menuItem ) {
                    fileMenu.insertSeparator( i );
                    return;
                }
            }
        }
    }

    /**
     * Adds a menu item to the File menu, just before the Exit menu item.
     *
     * @param menuItem
     */
    public void addFileMenuItem( JMenuItem menuItem ) {
        defaultFileMenu.insert( menuItem, defaultFileMenu.getMenuComponentCount() - 1 );
    }

    /**
     * Removes a menu item from the File menu
     *
     * @param menuItem
     */
    public void removeFileMenuItem( JMenuItem menuItem ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }

    /**
     * Sets a specified menu in the leftmost postition of the menu bar
     *
     * @param defaultFileMenu
     */
    public void setFileMenu( PhetFileMenu defaultFileMenu ) {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }

    /**
     * Returns the leftmost menu on the menu bar
     *
     * @return the leftmost menu on the menu bar
     */
    private PhetFileMenu getFileMenu() {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            return (PhetFileMenu)testMenu;
        }
        return null;
    }

    public HelpMenu getHelpMenu() {
        return helpMenu;
    }

    /**
     * Adds the "Debug" menu to the menu bar.
     */
//    public void addDebugMenu() {
//        if( debugMenu == null ) {
//            debugMenu = new DebugMenu( application );
//            addMenu( debugMenu );
//        }
//    }

//    /**
//     * Gets the debug menu.
//     * Clients can use this to add new items to the menu.
//     *
//     * @return DebugMenu
//     */
//    public DebugMenu getDebugMenu() {
//        return debugMenu;
//    }
    public void removeMenu( JMenu menu ) {
        getJMenuBar().remove( menu );
    }
}
