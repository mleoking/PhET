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

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.components.menu.HelpMenu;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;
import edu.colorado.phet.common.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * PhetFrame
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFrame extends JFrame {
    HelpMenu helpMenu;
    private JMenu defaultFileMenu;
    private boolean paused; // state of the clock prior to being iconified
    private PhetApplication application;
    private ClockControlPanel clockControlPanel;
    private ContentPanel basicPhetPanel;

    public PhetFrame( PhetApplication application ) throws IOException {
        super( application.getApplicationModel().getWindowTitle() );
        this.application = application;
        final ApplicationModel model = application.getApplicationModel();
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }

            // Pause the clock if the simulation window is iconified.
            public void windowIconified( WindowEvent e ) {
                super.windowIconified( e );
                paused = model.getClock().isPaused(); // save clock state
                if( !paused ) {
                    model.getClock().setPaused( true );
                }
            }

            // Restore the clock state if the simulation window is deiconified.
            public void windowDeiconified( WindowEvent e ) {
                super.windowDeiconified( e );
                if( !paused ) {
                    model.getClock().setPaused( false );
                }
            }
        } );
        JMenuBar menuBar = new JMenuBar();
        try {
            this.helpMenu = new HelpMenu( model );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        defaultFileMenu = new PhetFileMenu();
        menuBar.add( defaultFileMenu );
        menuBar.add( helpMenu );
        setJMenuBar( menuBar );
        model.getFrameSetup().initialize( this );

        JComponent apparatusPanelContainer = createApparatusPanelContainer( application );

        if( model.getUseClockControlPanel() ) {
            clockControlPanel = new ClockControlPanel( model.getClock() );
        }
        basicPhetPanel = new ContentPanel( apparatusPanelContainer, null, null, clockControlPanel );
        setContentPane( basicPhetPanel );
    }

    public PhetApplication getApplication() {
        return application;
    }

    public ClockControlPanel getClockControlPanel() {
        return clockControlPanel;
    }

    private JComponent createApparatusPanelContainer( PhetApplication application ) {
        ApplicationModel model = application.getApplicationModel();
        if( model.numModules() == 1 ) {
            JPanel apparatusPanelContainer = new JPanel();
            apparatusPanelContainer.setLayout( new GridLayout( 1, 1 ) );
            if( model.moduleAt( 0 ).getApparatusPanel() == null ) {
                throw new RuntimeException( "Null Apparatus Panel in Module: " + model.moduleAt( 0 ).getName() );
            }
            apparatusPanelContainer.add( model.moduleAt( 0 ).getApparatusPanel() );
            return apparatusPanelContainer;
        }
        else {
            JComponent apparatusPanelContainer = new TabbedApparatusPanelContainer( application );
            return apparatusPanelContainer;
        }

    }

    public ContentPanel getBasicPhetPanel() {
        return basicPhetPanel;
    }

    //////////////////////////////////////////////
    // Menu setup methods
    //

    /**
     * Adds a JMenu before the Help Menu.
     *
     * @param menu
     */
    public void addMenu( JMenu menu ) {
        SwingUtils.addMenuAt( menu, getJMenuBar(), getJMenuBar().getComponentCount() - 1 );
    }

    /**
     * Adds a menu separator to the File menu
     */
    public void addFileMenuSeparator() {
        defaultFileMenu.insertSeparator( defaultFileMenu.getComponentCount() + 1 );
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

    private PhetFileMenu getFileMenu() {
        JMenu testMenu = getJMenuBar().getMenu( 0 );
        if( testMenu != null && testMenu instanceof PhetFileMenu ) {
            return (PhetFileMenu)testMenu;
        }
        return null;
    }
}
