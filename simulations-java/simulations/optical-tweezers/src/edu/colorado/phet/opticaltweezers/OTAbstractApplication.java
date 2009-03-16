/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.opticaltweezers.menu.DeveloperMenu;
import edu.colorado.phet.opticaltweezers.menu.OptionsMenu;
import edu.colorado.phet.opticaltweezers.module.OTAbstractModule;

/**
 * OTAbstractApplication is the base class for all flavors of this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTAbstractApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private static TabbedModulePanePiccolo _tabbedModulePane;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public OTAbstractApplication( PhetApplicationConfig config )
    {
        super( config );
        initTabbedPane();
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the tabbed pane.
     */
    private void initTabbedPane() {

        // Create our own tabbed pane type so we can set the tab color
        TabbedPaneType tabbedPaneType = new TabbedPaneType(){
            public ITabbedModulePane createTabbedPane() {
                _tabbedModulePane = new TabbedModulePanePiccolo();
                return _tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }

    /*
     * Initializes the modules.
     */
    protected abstract void initModules();

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( OTResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( OTResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( OTResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( OTResources.getChar( "menu.file.load.mnemonic", 'L' ) );
            loadItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    load();
                }
            } );

            frame.addFileMenuItem( saveItem );
            frame.addFileMenuItem( loadItem );
            frame.addFileMenuSeparator();
        }

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        DeveloperMenu developerMenu = new DeveloperMenu( this );
        if ( developerMenu.getMenuComponentCount() > 0 && isDeveloperControlsEnabled() ) {
            frame.addMenu( developerMenu );
        }
    }

    //----------------------------------------------------------------------------
    // Setters & getters
    //----------------------------------------------------------------------------

    public void setSelectedTabColor( Color color ) {
        _tabbedModulePane.setSelectedTabColor( color );
    }

    public Color getSelectedTabColor() {
        return _tabbedModulePane.getSelectedTabColor();
    }

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }

    public Color getControlPanelBackground() {
        return ( (OTAbstractModule) getModule( 0 ) ).getControlPanel().getBackground();
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    public abstract void save();

    /**
     * Loads a simulation configuration.
     */
    public abstract void load();
}
