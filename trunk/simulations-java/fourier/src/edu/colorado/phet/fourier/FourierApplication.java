/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.PropertiesLoader;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.OptionsMenu;
import edu.colorado.phet.fourier.module.D2CModule;
import edu.colorado.phet.fourier.module.DiscreteModule;
import edu.colorado.phet.fourier.module.GameModule;
import edu.colorado.phet.fourier.persistence.ConfigManager;


/**
 * FourierApplication is the main application for the PhET "Fourier Analysis" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Set this to true to test one module and disable all others.
    private static final boolean TEST_ONE_MODULE = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // PersistanceManager handles loading/saving application configurations.
    private ConfigManager _persistenceManager;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param args command line arguments
     * @param title
     * @param description
     * @param version
     * @param clock
     * @param useClockControlPanel
     * @param frameSetup
     */
    public FourierApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
        initModules();  
        initMenubar();
    }
    
    //----------------------------------------------------------------------------
    // Modules
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the modules.
     * 
     * @param clock
     */
    private void initModules() {
        DiscreteModule discreteModule = new DiscreteModule();
        addModule( discreteModule );
        GameModule gameModule = new GameModule();
        addModule( gameModule );
        D2CModule d2cModule = new D2CModule();
        addModule( d2cModule );
    }
    
    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the menubar.
     */
    private void initMenubar() {
     
        if ( _persistenceManager == null ) {
            _persistenceManager = new ConfigManager( this );
        }
        
        PhetFrame frame = getPhetFrame();
        
        // File menu
        {
            JMenuItem saveItem = new JMenuItem( SimStrings.get( "FileMenu.save" ) );
            saveItem.setMnemonic( SimStrings.get( "FileMenu.save.mnemonic" ).charAt(0) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _persistenceManager.save();
                }
            } );
            
            JMenuItem loadItem = new JMenuItem( SimStrings.get( "FileMenu.load" ) );
            loadItem.setMnemonic( SimStrings.get( "FileMenu.load.mnemonic" ).charAt(0) );
            loadItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _persistenceManager.load();
                }
            } );

            frame.addFileMenuItem( saveItem );
            frame.addFileMenuItem( loadItem );
            frame.addFileMenuSeparator();
        }
        
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point for the PhET Fourier application.
     * 
     * @param args command line arguments
     */
    public static void main( String[] args ) throws IOException {

        // Initialize localization.
        SimStrings.getInstance().init( args, FourierConstants.SIM_STRINGS_NAME );
        
        // Load simulation properties file
        Properties simulationProperties = PropertiesLoader.loadProperties( FourierConstants.SIM_PROPERTIES_NAME );
        
        // Title, etc.
        String title = SimStrings.get( "FourierApplication.title" );
        String description = SimStrings.get( "FourierApplication.description" );
        String version = PhetApplication.getVersionString( simulationProperties );
        
        // Frame setup
        int width = FourierConstants.APP_FRAME_WIDTH;
        int height = FourierConstants.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );
        
        // Create the application.
        FourierApplication app = new FourierApplication( args, title, description, version, frameSetup );
        app.setSimulationProperties( simulationProperties );
        
        // Start the application.
        app.startApplication();
    }
}
