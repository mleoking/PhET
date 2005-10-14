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

import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.DebugMenu;
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
    private static final boolean TEST_ONE_MODULE = true;
    
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
            String title, String description, String version, AbstractClock clock,
            boolean useClockControlPanel, FrameSetup frameSetup )
    {
        super( args, title, description, version, clock, useClockControlPanel, frameSetup );
        initModules( clock );  
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
    private void initModules( AbstractClock clock ) {
        if ( TEST_ONE_MODULE ) {
            Module module = new GameModule( clock );
            setModules( new Module[] { module } );
            setInitialModule( module );
        }
        else {
            DiscreteModule discreteModule = new DiscreteModule( clock );
            GameModule gameModule = new GameModule( clock );
            D2CModule d2cModule = new D2CModule( clock );

            setModules( new Module[] { discreteModule, gameModule, d2cModule } );
            setInitialModule( discreteModule );
        }
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
        
        // Debug menu extensions
        DebugMenu debugMenu = frame.getDebugMenu();
        if ( debugMenu != null ) {
            //XXX Add debug menu items here.
        }
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
        SimStrings.init( args, FourierConstants.LOCALIZATION_BUNDLE_BASENAME );
        
        // Title, etc.
        String title = SimStrings.get( "FourierApplication.title" );
        String description = SimStrings.get( "FourierApplication.description" );
        String version = Version.NUMBER;
        
        // Clock
        double timeStep = FourierConstants.CLOCK_TIME_STEP;
        int waitTime = ( 1000 / FourierConstants.CLOCK_FRAME_RATE ); // milliseconds
        boolean isFixed = FourierConstants.CLOCK_TIME_STEP_IS_CONSTANT;
        AbstractClock clock = new SwingTimerClock( timeStep, waitTime, isFixed );
        boolean useClockControlPanel = true;
        
        // Frame setup
        int width = FourierConstants.APP_FRAME_WIDTH;
        int height = FourierConstants.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );
        
        // Create the application.
        FourierApplication app = new FourierApplication( args,
                 title, description, version, clock, useClockControlPanel, frameSetup );
        
        // Start the application.
        app.startApplication();
    }
}
