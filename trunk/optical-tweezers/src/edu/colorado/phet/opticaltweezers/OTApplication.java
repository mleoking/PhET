/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.menu.DeveloperMenu;
import edu.colorado.phet.opticaltweezers.menu.OptionsMenu;
import edu.colorado.phet.opticaltweezers.module.DNAModule;
import edu.colorado.phet.opticaltweezers.module.MotorsModule;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;
import edu.colorado.phet.opticaltweezers.persistence.GlobalConfig;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.persistence.OTPersistenceManager;
import edu.colorado.phet.opticaltweezers.util.ArgUtils;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

/**
 * HAApplication
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OTApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Provide this program argument to enable developer-only features.
    private static final String DEVELOPER_ARG = "-dev";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsModule _physicsModule;
    private DNAModule _dnaModule;
    private MotorsModule _motorsModule;
    
    // PersistanceManager handles loading/saving application configurations.
    private OTPersistenceManager _persistenceManager;
    
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
     * @param frameSetup
     */
    public OTApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
        initModules();
        initMenubar( args );
    }
    
    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the modules.
     */
    private void initModules() {
        _physicsModule = new PhysicsModule();
        addModule( _physicsModule );
        
        _dnaModule = new DNAModule();
        addModule( _dnaModule );
        
        _motorsModule = new MotorsModule();
        addModule( _motorsModule );
    }
    
    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();
        
        if ( _persistenceManager == null ) {
            _persistenceManager = new OTPersistenceManager( this );
        }
        
        // File menu
        {
            JMenuItem saveItem = new JMenuItem( SimStrings.get( "menu.file.save" ) );
            saveItem.setMnemonic( SimStrings.get( "menu.file.save.mnemonic" ).charAt(0) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _persistenceManager.save();
                }
            } );
            
            JMenuItem loadItem = new JMenuItem( SimStrings.get( "menu.file.load" ) );
            loadItem.setMnemonic( SimStrings.get( "menu.file.load.mnemonic" ).charAt(0) );
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
        OptionsMenu optionsMenu = new OptionsMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        if ( ArgUtils.contains( args, DEVELOPER_ARG ) ) {
            DeveloperMenu developerMenu = new DeveloperMenu();
            getPhetFrame().addMenu( developerMenu );
        }
        
        // Help menu additions
        {
//            HelpMenu helpMenu = frame.getHelpMenu();
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves global state.
     * 
     * @param appConfig
     */
    public void save( OTConfig appConfig ) {
        
        GlobalConfig config = appConfig.getGlobalConfig();
        
        config.setCvsTag( OTVersion.CVS_TAG );
        config.setVersionNumber( OTVersion.NUMBER );
    }

    /**
     * Loads global state.
     * 
     * @param appConfig
     */
    public void load( OTConfig appConfig ) {
        
        GlobalConfig config = appConfig.getGlobalConfig();
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     * 
     * @param args command line arguments
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public static void main( final String[] args ) {

        /* 
         * Wrap the body of main in invokeLater, so that all initialization occurs 
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the 
         * event dispatch thread. Since we don't have an easy way to separate Swing and 
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                // Initialize look-and-feel
                PhetLookAndFeel laf = new PhetLookAndFeel();
                laf.initLookAndFeel();

                // Initialize localization.
                SimStrings.init( args, OTConstants.LOCALIZATION_BUNDLE_BASENAME );

                // Title, etc.
                String title = SimStrings.get( "OTApplication.title" );
                String description = SimStrings.get( "OTApplication.description" );
                String version = OTVersion.NUMBER;

                // Frame setup
                int width = OTConstants.APP_FRAME_SIZE.width;
                int height = OTConstants.APP_FRAME_SIZE.height;
                FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

                // Create the application.
                OTApplication app = new OTApplication( args, title, description, version, frameSetup );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
