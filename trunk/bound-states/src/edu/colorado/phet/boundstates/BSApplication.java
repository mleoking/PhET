/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.boundstates.color.BSBlackColorScheme;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.color.BSColorsMenu;
import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.boundstates.module.BSManyWellsModule;
import edu.colorado.phet.boundstates.module.BSOneWellModule;
import edu.colorado.phet.boundstates.module.BSTwoWellsModule;
import edu.colorado.phet.boundstates.persistence.BSConfig;
import edu.colorado.phet.boundstates.persistence.BSGlobalConfig;
import edu.colorado.phet.boundstates.persistence.BSPersistenceManager;
import edu.colorado.phet.boundstates.util.ArgUtils;
import edu.colorado.phet.boundstates.util.DialogUtils;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.menu.HelpMenu;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;


/**
 * BSApplication is the main application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
       
    // Command line args for choosing modules...
    private static final String ARG_ONE = "-one";
    private static final String ARG_TWO = "-two";
    private static final String ARG_MANY = "-many";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSOneWellModule _oneWellModule;
    private BSTwoWellsModule _twoWellsModule;
    private BSManyWellsModule _manyWellsModule;
    
    // PersistanceManager handles loading/saving application configurations.
    private BSPersistenceManager _persistenceManager;
    
    private BSColorsMenu _colorsMenu;
    
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
    public BSApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
        initModules( args );
        initMenubar();
    }
    
    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the modules.
     * Command line args are consulted to see which modules should be created.
     * If no command line args are found, then all of the modules are created.
     * 
     * @param clock
     */
    private void initModules( String[] args ) {
        
        final boolean hasOneWellModule = ArgUtils.contains( args, ARG_ONE );
        final boolean hasTwoWellsModule = ArgUtils.contains( args, ARG_TWO );
        final boolean hasManyWellsModule = ArgUtils.contains( args, ARG_MANY );
        final boolean hasAll = !( hasOneWellModule || hasTwoWellsModule || hasManyWellsModule );
        
        if ( hasOneWellModule || hasAll ) {
            _oneWellModule = new BSOneWellModule();
            addModule( _oneWellModule );
        }
        
        if ( hasTwoWellsModule || hasAll ) {
            _twoWellsModule = new BSTwoWellsModule();
            addModule( _twoWellsModule );
        }

        if ( hasManyWellsModule || hasAll ) {
            _manyWellsModule = new BSManyWellsModule();
            addModule( _manyWellsModule );
        }
        
        //  The first module has a wiggle me
        BSAbstractModule firstModule = (BSAbstractModule) getModules()[0];
        firstModule.setHasWiggleMe( true );
    }
    
    /*
     * Initializes the menubar.
     */
    private void initMenubar() {
     
        PhetFrame frame = getPhetFrame();
        
        if ( _persistenceManager == null ) {
            _persistenceManager = new BSPersistenceManager( this );
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
            
            saveItem.setEnabled( false );//XXX disable until working
            loadItem.setEnabled( false );//XXX disable until working
        }
        
        // Colors menu
        {
            _colorsMenu = new BSColorsMenu( this );
            if ( BSConstants.COLOR_SCHEME instanceof BSBlackColorScheme ) {
                _colorsMenu.selectBlack();
            }
            else {
                _colorsMenu.selectWhite();
            }
            getPhetFrame().addMenu( _colorsMenu );
        }
        
        // Help menu extensions
        HelpMenu helpMenu = getPhetFrame().getHelpMenu();
        if ( helpMenu != null ) {
            //XXX Add help menu items here.
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color scheme for all modules that support color schemes.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            if ( modules[i] instanceof BSAbstractModule ) {
                ((BSAbstractModule)modules[i]).setColorScheme( colorScheme );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // PhetApplication overrides
    //----------------------------------------------------------------------------
    
    public void startApplication() {
        super.startApplication();
        // Do things that need to be done after starting the application...
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves global state.
     * 
     * @param appConfig
     */
    public void save( BSConfig appConfig ) {
        
        BSGlobalConfig config = appConfig.getGlobalConfig();
        
        config.setCvsTag( BSVersion.CVS_TAG );
        config.setVersionNumber( BSVersion.NUMBER );
        
        // Color scheme
        config.setColorSchemeName( _colorsMenu.getColorSchemeName() );
        config.setColorScheme( _colorsMenu.getColorScheme() );
    }

    /**
     * Loads global state.
     * 
     * @param appConfig
     */
    public void load( BSConfig appConfig ) {
        
        BSGlobalConfig config = appConfig.getGlobalConfig();
        
        // Color scheme
        String colorSchemeName = config.getColorSchemeName();
        BSColorScheme colorScheme = config.getColorScheme().toBSColorScheme();
        _colorsMenu.setColorScheme( colorSchemeName, colorScheme );
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

                try {
                    // Initialize look-and-feel
                    PhetLookAndFeel laf = new PhetLookAndFeel();
                    laf.initLookAndFeel();

                    // Initialize localization.
                    SimStrings.init( args, BSConstants.LOCALIZATION_BUNDLE_BASENAME );

                    // Title, etc.
                    String title = SimStrings.get( "BSApplication.title" );
                    String description = SimStrings.get( "BSApplication.description" );
                    String version = BSVersion.NUMBER;

                    // Frame setup
                    int width = BSConstants.APP_FRAME_WIDTH;
                    int height = BSConstants.APP_FRAME_HEIGHT;
                    FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

                    // Create the application.
                    BSApplication app = new BSApplication( args, title, description, version, frameSetup );

                    // Start the application.
                    app.startApplication();
                }
                catch ( Exception e ) {
                    // Inform the user of the exception, then exit.
                    String pattern = SimStrings.get( "message.fatalException" );
                    Object[] objs = { e.getMessage() };
                    MessageFormat format = new MessageFormat( pattern );
                    String message = format.format( objs );
                    DialogUtils.showErrorDialog( null, message );
                    e.printStackTrace();
                    System.exit( 1 );
                }
            }
        } );
    }
}
