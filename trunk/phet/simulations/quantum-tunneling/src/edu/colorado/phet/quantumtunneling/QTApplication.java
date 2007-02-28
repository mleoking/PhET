/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetFrameWorkaround;
import edu.colorado.phet.common.view.menu.HelpMenu;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.color.BlackColorScheme;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.color.QTColorSchemeMenu;
import edu.colorado.phet.quantumtunneling.debug.QTDeveloperMenu;
import edu.colorado.phet.quantumtunneling.module.QTModule;
import edu.colorado.phet.quantumtunneling.persistence.QTConfig;
import edu.colorado.phet.quantumtunneling.persistence.QTGlobalConfig;
import edu.colorado.phet.quantumtunneling.persistence.QTPersistenceManager;
import edu.colorado.phet.quantumtunneling.util.ArgUtils;


/**
 * QTApplication is the main application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
       
    // Provide this program argument to enable developer-only features.
    private static final String DEVELOPER_ARG = "-dev";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTModule _module;
    
    // PersistanceManager handles loading/saving application configurations.
    private QTPersistenceManager _persistenceManager;
    
    private QTColorSchemeMenu _colorSchemeMenu;
    
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
    public QTApplication( String[] args, 
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
     * 
     * @param clock
     */
    private void initModules() {
        _module = new QTModule();
        addModule( _module );
    }
    
    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {
     
        PhetFrame frame = getPhetFrame();
        
        if ( _persistenceManager == null ) {
            _persistenceManager = new QTPersistenceManager( this );
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
        {
            // Color Scheme submenu
            _colorSchemeMenu = new QTColorSchemeMenu( _module );
            if ( QTConstants.COLOR_SCHEME instanceof BlackColorScheme ) {
                _colorSchemeMenu.selectBlack();
            }
            else {
                _colorSchemeMenu.selectWhite();
            }
            getPhetFrame().addMenu( _colorSchemeMenu );
        }
   
        // Developer menu
        if ( ArgUtils.contains( args, DEVELOPER_ARG ) ) {
            QTDeveloperMenu developerMenu = new QTDeveloperMenu( _module );
            getPhetFrame().addMenu( developerMenu );
        }
        
        // Help menu extensions
        HelpMenu helpMenu = getPhetFrame().getHelpMenu();
        if ( helpMenu != null ) {
            //XXX Add help menu items here.
        }
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /*
     * Workaround for Swing/AWT paint priority problem.
     * See PhetFrameWorkaround javadoc for details.
     */
    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves global state.
     * 
     * @param appConfig
     */
    public void save( QTConfig appConfig ) {
        
        QTGlobalConfig config = appConfig.getGlobalConfig();
        
        config.setCvsTag( QTVersion.CVS_TAG );
        config.setVersionNumber( QTVersion.NUMBER );
        
        // Color scheme
        config.setColorSchemeName( _colorSchemeMenu.getColorSchemeName() );
        config.setColorScheme( _colorSchemeMenu.getColorScheme() );
    }

    /**
     * Loads global state.
     * 
     * @param appConfig
     */
    public void load( QTConfig appConfig ) {
        
        QTGlobalConfig config = appConfig.getGlobalConfig();
        
        // Color scheme
        String colorSchemeName = config.getColorSchemeName();
        QTColorScheme colorScheme = config.getColorScheme().toQTColorScheme();
        _colorSchemeMenu.setColorScheme( colorSchemeName, colorScheme );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     * <p>
     * Supported command line arguments:
     * <ul>
     * <li>-dev : enabled developer controls
     * <li>user.language=value : sets the locale used by SimStrings
     * </ul>
     * 
     * @param args command line arguments
     */
    public static void main( final String[] args ) throws IOException {

        // Initialize localization.
        SimStrings.init( args, QTConstants.LOCALIZATION_BUNDLE_BASENAME );

        // Title, etc.
        String title = SimStrings.get( "title.quantumTunneling" );
        String description = SimStrings.get( "QTApplication.description" );
        String version = QTVersion.NUMBER;

        // Frame setup
        int width = QTConstants.APP_FRAME_WIDTH;
        int height = QTConstants.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Create the application.
        QTApplication app = new QTApplication( args, title, description, version, frameSetup );

        // Start the application.
        app.startApplication();
    }
}
