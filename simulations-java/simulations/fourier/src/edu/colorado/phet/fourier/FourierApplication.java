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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetApplicationConfig;
import edu.colorado.phet.common.view.PhetFrame;
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
     */
    public FourierApplication( PhetApplicationConfig config )
    {
        super( config );
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
            JMenuItem saveItem = new JMenuItem( FourierResources.getString( "FileMenu.save" ) );
            saveItem.setMnemonic( FourierResources.getChar( "FileMenu.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _persistenceManager.save();
                }
            } );
            
            JMenuItem loadItem = new JMenuItem( FourierResources.getString( "FileMenu.load" ) );
            loadItem.setMnemonic( FourierResources.getChar( "FileMenu.load.mnemonic", 'L' ) );
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

        PhetApplicationConfig config = new PhetApplicationConfig( args, FourierConstants.FRAME_SETUP, FourierResources.getResourceLoader() );
        
        // Create the application.
        FourierApplication app = new FourierApplication( config );
        
        // Start the application.
        app.startApplication();
    }
}
