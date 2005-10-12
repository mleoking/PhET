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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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
import edu.colorado.phet.fourier.persistence.FourierConfig;
import edu.colorado.phet.fourier.persistence.ObjectIO;
import edu.colorado.phet.fourier.view.HarmonicColors;


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
    // Instance data
    //----------------------------------------------------------------------------
    
    private DiscreteModule _discreteModule;
    private GameModule _gameModule;
    private D2CModule _d2cModule;
    
    private String _configDirectoryName; // where to look for user config files
    
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
        _configDirectoryName = System.getProperty( "user.home" );
        initModules( clock );  
        initMenubar();
    }
    
    //----------------------------------------------------------------------------
    // Modules
    //----------------------------------------------------------------------------
    
    private void initModules( AbstractClock clock ) {
        if ( TEST_ONE_MODULE ) {
            Module module = new DiscreteModule( clock );
            setModules( new Module[] { module } );
            setInitialModule( module );
        }
        else {
            _discreteModule = new DiscreteModule( clock );
            _gameModule = new GameModule( clock );
            _d2cModule = new D2CModule( clock );

            setModules( new Module[] { 
                    _discreteModule,
                    _gameModule,
                    _d2cModule
                    } );
            setInitialModule( _discreteModule );
        }
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
        {
            JMenuItem saveItem = new JMenuItem( SimStrings.get( "FileMenu.save" ) );
            saveItem.setMnemonic( SimStrings.get( "FileMenu.save.mnemonic" ).charAt(0) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleSaveConfig();
                }
            } );
            
            JMenuItem loadItem = new JMenuItem( SimStrings.get( "FileMenu.load" ) );
            loadItem.setMnemonic( SimStrings.get( "FileMenu.load.mnemonic" ).charAt(0) );
            loadItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleLoadConfig();
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
    
    /*
     * Handles the "Save configuration..." menu item in the File menu.
     */
    private void handleSaveConfig() {
       
        JFrame frame = getPhetFrame();
        
        // Choose the file to save.
        JFileChooser fileChooser = new JFileChooser( _configDirectoryName );
        fileChooser.setDialogTitle( SimStrings.get( "Save.title" ) );
        int rval = fileChooser.showSaveDialog( frame );
        _configDirectoryName = fileChooser.getCurrentDirectory().getAbsolutePath();
        File selectedFile = fileChooser.getSelectedFile();
        if ( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
            return;
        }

        _configDirectoryName = selectedFile.getParentFile().getAbsolutePath();

        // If the file exists, confirm overwrite.
        if ( selectedFile.exists() ) {
            String title = SimStrings.get( "Save.confirm.title" );
            String message = SimStrings.get( "Save.confirm.message" );
            System.out.println( "message=" + message );//XXX
            int reply = JOptionPane.showConfirmDialog( frame, message, title, JOptionPane.YES_NO_OPTION );
            if ( reply != JOptionPane.YES_OPTION ) {
                return;
            }
        }

        // Save the modules into a configuration.
        FourierConfig config = new FourierConfig();
        _discreteModule.save( config );
        _gameModule.save( config );
        _d2cModule.save( config );

        // Save global stuff into the configuration
        {
            // Version & build info
            config.getGlobalConfig().setVersionNumber( Version.NUMBER );
            config.getGlobalConfig().setBuildNumber( Version.BUILD );

            // Harmonic colors
            Color[] harmonicColors = new Color[HarmonicColors.getInstance().getNumberOfColors()];
            for ( int i = 0; i < harmonicColors.length; i++ ) {
                harmonicColors[i] = HarmonicColors.getInstance().getColor( i );
            }
            config.getGlobalConfig().setHarmonicColors( harmonicColors );
        }

        // Save the configuration to the selected file.
        String filename = selectedFile.getAbsolutePath();
        try {
            ObjectIO.write( config, filename );
        }
        catch ( Exception e ) {
            String title = SimStrings.get( "Save.error.title" );
            String format = SimStrings.get( "Save.error.message" );
            Object[] args = { filename, e.getMessage() };
            String message = MessageFormat.format( format, args );
            JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
            e.printStackTrace();
        }
    }
    
    /*
     * Handles the "Load configuration..." menu item in the File menu.
     */
    private void handleLoadConfig() {
        JFrame frame = getPhetFrame();
        
        // Choose the file to load.
        JFileChooser fileChooser = new JFileChooser( _configDirectoryName );
        fileChooser.setDialogTitle( SimStrings.get( "Load.title" ) );
        int rval = fileChooser.showOpenDialog( frame );
        _configDirectoryName = fileChooser.getCurrentDirectory().getAbsolutePath();
        File selectedFile = fileChooser.getSelectedFile();
        if ( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
            return;
        }

        // Read the configuration object from the file.
        Object object = null;
        String filename = selectedFile.getAbsolutePath();
        try {
            object = ObjectIO.read( filename );
        }
        catch ( Exception e ) {
            String title = SimStrings.get( "Save.error.title" );
            String format = SimStrings.get( "Save.error.message" );
            Object[] args = { filename, e.getMessage() };
            String message = MessageFormat.format( format, args );
            JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
            e.printStackTrace();
            return;
        }
            
        // Check the object's type
        FourierConfig config = null;
        if ( object instanceof FourierConfig ) {
            config = (FourierConfig) object;
        }
        else {
            String title = SimStrings.get( "Save.error.title" );
            String format = SimStrings.get( "Save.error.message" );
            Object[] args = { filename, SimStrings.get( "Save.error.notConfig" ) };
            String message = MessageFormat.format( format, args );
            JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
            return;
        }

        frame.setCursor( FourierConstants.WAIT_CURSOR );

        // Load the modules from the configuration.
        _discreteModule.load( config );
        _gameModule.load( config );
        _d2cModule.load( config );

        // Load global stuff from the configuration
        Color[] harmonicColors = config.getGlobalConfig().getHarmonicColors();
        for ( int i = 0; i < harmonicColors.length; i++ ) {
            HarmonicColors.getInstance().setColor( i, harmonicColors[i] );
        }

        frame.setCursor( FourierConstants.DEFAULT_CURSOR );
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
