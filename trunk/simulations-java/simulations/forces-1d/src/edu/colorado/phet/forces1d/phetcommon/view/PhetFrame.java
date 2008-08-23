/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.forces1d.phetcommon.application.ApplicationModel;
import edu.colorado.phet.forces1d.phetcommon.application.Module;
import edu.colorado.phet.forces1d.phetcommon.application.ModuleManager;
import edu.colorado.phet.forces1d.phetcommon.application.PhetApplication;
import edu.colorado.phet.forces1d.phetcommon.model.clock.AbstractClock;
import edu.colorado.phet.forces1d.phetcommon.util.DebugMenu;
import edu.colorado.phet.forces1d.phetcommon.view.components.menu.HelpMenu;
import edu.colorado.phet.forces1d.phetcommon.view.components.menu.PhetFileMenu;
import edu.colorado.phet.forces1d.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.forces1d.phetcommon.view.util.SwingUtils;

/**
 * PhetFrame
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFrame extends JFrame {
    private HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    private boolean paused; // state of the clock prior to being iconified
    private PhetApplication application;
    private ClockControlPanel clockControlPanel;
    private ContentPanel basicPhetPanel;
    private FrameSetup frameSetup;
    private DebugMenu debugMenu;

    /**
     * todo: make clock control panel useage module-specific
     *
     * @param title
     * @param clock
     * @param frameSetup
     * @param useClockControlPanel
     * @throws HeadlessException
     */
    public PhetFrame( PhetApplication application, String title, final AbstractClock clock, FrameSetup frameSetup,
                      boolean useClockControlPanel, ModuleManager moduleManager,
                      String description, String version ) throws HeadlessException {
        super( title );
        this.application = application;
        this.frameSetup = frameSetup;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.addWindowListener( new WindowAdapter() {

            // Pause the clock if the simulation window is iconified.
            public void windowIconified( WindowEvent e ) {
                super.windowIconified( e );
                paused = clock.isPaused(); // save clock state
                if ( !paused ) {
                    clock.setPaused( true );
                }
            }

            // Restore the clock state if the simulation window is deiconified.
            public void windowDeiconified( WindowEvent e ) {
                super.windowDeiconified( e );
                if ( !paused ) {
                    clock.setPaused( false );
                }
            }
        } );

        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( application.getPhetFrame(), moduleManager, title, description, version );
        defaultFileMenu = new PhetFileMenu();
        menuBar.add( defaultFileMenu );
        menuBar.add( helpMenu );
        setJMenuBar( menuBar );

        if ( frameSetup != null ) {
            frameSetup.initialize( this );
        }

        if ( useClockControlPanel ) {
            try {
                clockControlPanel = new ClockControlPanel( clock );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param application
     * @throws IOException
     */
    public PhetFrame( PhetApplication application ) throws IOException {
        super( application.getApplicationModel().getWindowTitle() );
        this.application = application;
        final ApplicationModel model = application.getApplicationModel();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.addWindowListener( new WindowAdapter() {

            // Pause the clock if the simulation window is iconified.
            public void windowIconified( WindowEvent e ) {
                super.windowIconified( e );
                paused = model.getClock().isPaused(); // save clock state
                if ( !paused ) {
                    model.getClock().setPaused( true );
                }
            }

            // Restore the clock state if the simulation window is deiconified.
            public void windowDeiconified( WindowEvent e ) {
                super.windowDeiconified( e );
                if ( !paused ) {
                    model.getClock().setPaused( false );
                }
            }
        } );
        JMenuBar menuBar = new JMenuBar();
        this.helpMenu = new HelpMenu( application.getPhetFrame(), application );
        defaultFileMenu = new PhetFileMenu();
        menuBar.add( defaultFileMenu );
        menuBar.add( helpMenu );
        setJMenuBar( menuBar );
        model.getFrameSetup().initialize( this );

        JComponent apparatusPanelContainer = createApparatusPanelContainer( application, model.getModules() );

        if ( model.getUseClockControlPanel() ) {
            clockControlPanel = new ClockControlPanel( model.getClock() );
        }
        basicPhetPanel = new ContentPanel( apparatusPanelContainer, null, null, clockControlPanel );
        setContentPane( basicPhetPanel );
    }

    public void setModules( Module[] modules ) {
        JComponent apparatusPanelContainer = createApparatusPanelContainer( application, modules );
        basicPhetPanel = new ContentPanel( apparatusPanelContainer, null, null, clockControlPanel );
        setContentPane( basicPhetPanel );
    }

    /**
     * If we have a frame setup, we should not pack the frame
     */
    public void pack() {
        if ( frameSetup == null ) {
            super.pack();
        }
    }

    public PhetApplication getApplication() {
        return application;
    }

    public ClockControlPanel getClockControlPanel() {
        return clockControlPanel;
    }

    /**
     * Creates the JContainer that holds the apparatus panel(s) for the application. If the application
     * has only one module, the JContainer is a JPanel. If there is more than one, it is an instance
     * of a class in PhetCommon that manages a JTabbedPane and the appartus panels for the various
     * modules.
     *
     * @param application
     * @param modules
     * @return
     */
    private JComponent createApparatusPanelContainer( PhetApplication application, Module[] modules ) {
        JComponent apparatusPanelContainer = null;
        if ( modules.length == 1 ) {
            apparatusPanelContainer = new JPanel();
            apparatusPanelContainer.setLayout( new GridLayout( 1, 1 ) );
            if ( modules[0].getApparatusPanel() == null ) {
                throw new RuntimeException( "Null Apparatus Panel in Module: " + modules[0].getName() );
            }
            apparatusPanelContainer.add( modules[0].getApparatusPanel() );
        }
        else {
            apparatusPanelContainer = new TabbedApparatusPanelContainer( application );
        }
        return apparatusPanelContainer;
    }

    public ContentPanel getBasicPhetPanel() {
        return basicPhetPanel;
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
        if ( fileMenu != null ) {
            for ( int i = 0; i < fileMenu.getItemCount(); i++ ) {
                if ( fileMenu.getItem( i ) == menuItem ) {
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
        if ( fileMenu != null ) {
            for ( int i = 0; i < fileMenu.getItemCount(); i++ ) {
                if ( fileMenu.getItem( i ) == menuItem ) {
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
        if ( testMenu != null && testMenu instanceof PhetFileMenu ) {
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
        if ( testMenu != null && testMenu instanceof PhetFileMenu ) {
            getJMenuBar().remove( testMenu );
        }
        getJMenuBar().add( defaultFileMenu, 0 );
    }

    /**
     * Returns the leftmost menu on the menu bar
     *
     * @return
     */
    private PhetFileMenu getFileMenu() {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if ( testMenu != null && testMenu instanceof PhetFileMenu ) {
            return (PhetFileMenu) testMenu;
        }
        return null;
    }

    public HelpMenu getHelpMenu() {
        return helpMenu;
    }

    /**
     * Adds the "Debug" menu to the menu bar.
     */
    public void addDebugMenu() {
        if ( debugMenu == null ) {
            debugMenu = new DebugMenu( application );
            addMenu( debugMenu );
        }
    }

    /**
     * Gets the debug menu.
     * Clients can use this to add new items to the menu.
     *
     * @return DebugMenu
     */
    public DebugMenu getDebugMenu() {
        return debugMenu;
    }
}
