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

import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.CommandLineUtils;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetFrameWorkaround;
import edu.colorado.phet.common.view.menu.HelpMenu;
import edu.colorado.phet.quantumtunneling.color.BlackColorScheme;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.color.QTColorSchemeMenu;
import edu.colorado.phet.quantumtunneling.debug.QTDeveloperMenu;
import edu.colorado.phet.quantumtunneling.module.QTModule;
import edu.colorado.phet.quantumtunneling.persistence.QTConfig;
import edu.colorado.phet.quantumtunneling.persistence.QTGlobalConfig;
import edu.colorado.phet.quantumtunneling.persistence.QTPersistenceManager;


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
     */
    public QTApplication( String[] args )
    {
        super( args, QTResources.getConfig(), QTConstants.FRAME_SETUP );
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
            JMenuItem saveItem = new JMenuItem( QTResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( QTResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _persistenceManager.save();
                }
            } );
            
            JMenuItem loadItem = new JMenuItem( QTResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( QTResources.getChar( "menu.file.load.mnemonic", 'L' ) );
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
        if ( CommandLineUtils.contains( args, DEVELOPER_ARG ) ) {
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
        
        config.setVersionNumber( QTResources.getVersion() );
        
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
     * </ul>
     * 
     * @param args command line arguments
     */
    public static void main( final String[] args ) throws IOException {

        // Create the application.
        QTApplication app = new QTApplication( args );
        
        // Start the application.
        app.startApplication();
    }
}
