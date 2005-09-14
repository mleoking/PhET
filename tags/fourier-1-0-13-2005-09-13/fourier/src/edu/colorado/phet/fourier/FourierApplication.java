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

import java.awt.Dimension;
import java.io.IOException;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetStartupWindow;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.DebugMenu;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.OptionsMenu;
import edu.colorado.phet.fourier.module.D2CModule;
import edu.colorado.phet.fourier.module.DiscreteModule;


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
    
    private static final boolean TEST_ONE_MODULE = false;
    
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
        initMenubar();
    }
    
    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------
    
    /**
     * Initializes the menubar.
     */
    private void initMenubar() {
     
        PhetFrame frame = getPhetFrame();
        
        // File menu
//        {
//            JMenuItem saveItem = new JMenuItem( SimStrings.get( "FileMenu.save" ) );
//            saveItem.setMnemonic( SimStrings.get( "FileMenu.save.mnemonic" ).charAt(0) );
//            
//            JMenuItem loadItem = new JMenuItem( SimStrings.get( "FileMenu.load" ) );
//            loadItem.setMnemonic( SimStrings.get( "FileMenu.load.mnemonic" ).charAt(0) );
//
//            frame.addFileMenuItem( saveItem );
//            frame.addFileMenuItem( loadItem );
//            frame.addFileMenuSeparator();
//        }
        
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
     * Main entry point for the PhET Color Vision application.
     * 
     * @param args command line arguments
     */
    public static void main( String[] args ) throws IOException {

        // Initialize localization.
        SimStrings.init( args, FourierConfig.LOCALIZATION_BUNDLE_BASENAME );
        
        // Open progress window
        String startupMessage = SimStrings.get( "FourierApplication.starting" );
        PhetStartupWindow startupWindow = new PhetStartupWindow( startupMessage );
        startupWindow.setIndeterminate( true );
        startupWindow.setVisible( true );
        
        // Get stuff needed to initialize the application model.
        String title = SimStrings.get( "FourierApplication.title" );
        String description = SimStrings.get( "FourierApplication.description" );
        String version = Version.NUMBER;
        int width = FourierConfig.APP_FRAME_WIDTH;
        int height = FourierConfig.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Clock
        double timeStep = FourierConfig.CLOCK_TIME_STEP;
        int waitTime = ( 1000 / FourierConfig.CLOCK_FRAME_RATE ); // milliseconds
        boolean isFixed = FourierConfig.CLOCK_TIME_STEP_IS_CONSTANT;
        AbstractClock clock = new SwingTimerClock( timeStep, waitTime, isFixed );
        boolean useClockControlPanel = true;
        
        // Create the application.
        FourierApplication app = new FourierApplication( args,
                 title, description, version, clock, useClockControlPanel, frameSetup );
        
        // Simulation Modules
        if ( TEST_ONE_MODULE ) {
            Module module = new DiscreteModule( clock );
            app.setModules( new Module[] { module } );
            app.setInitialModule( module );
        }
        else {
            DiscreteModule discreteModule = new DiscreteModule( clock );
            D2CModule d2cModule = new D2CModule( clock );
//            WavePulseShaperModule wavePulseShapeModule = new WavePulseShaperModule( clock );
//            SoundModule soundModule = new SoundModule( clock );
            
            app.setModules( new Module[] { 
                    discreteModule, 
                    d2cModule,  
//                    wavePulseShapeModule,
//                    soundModule
                    } );
            app.setInitialModule( discreteModule );
        }
        
        // Start the application.
        app.startApplication();
        
        startupWindow.setVisible( false );
    }
}
