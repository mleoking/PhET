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
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.menu.HelpMenu;
import edu.colorado.phet.quantumtunneling.color.BlackColorScheme;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.color.QTColorSchemeMenu;
import edu.colorado.phet.quantumtunneling.debug.QTDeveloperMenu;
import edu.colorado.phet.quantumtunneling.module.QTModule;
import edu.colorado.phet.quantumtunneling.persistence.QTConfig;
import edu.colorado.phet.quantumtunneling.persistence.QTGlobalConfig;


/**
 * QTApplication is the main application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTApplication extends NonPiccoloPhetApplication {

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
    private XMLPersistenceManager _persistenceManager;

    private QTColorSchemeMenu _colorSchemeMenu;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public QTApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar( config.getCommandLineArgs() );
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
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( QTResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( QTResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( QTResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( QTResources.getChar( "menu.file.load.mnemonic", 'L' ) );
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
     * Saves the simulation's configuration.
     */
    public void save() {

        QTConfig appConfig = new QTConfig();

        // Global config
        QTGlobalConfig globalConfig = new QTGlobalConfig();
        appConfig.setGlobalConfig( globalConfig );
        globalConfig.setVersionNumber( getApplicationConfig().getVersion().toString() );
        globalConfig.setColorSchemeName( _colorSchemeMenu.getColorSchemeName() );
        globalConfig.setColorScheme( _colorSchemeMenu.getColorScheme() );

        // Modules config
        appConfig.setModuleConfig( _module.save() );
        
        _persistenceManager.save( appConfig );
    }

    /**
     * Loads a simulation configuration.
     */
    public void load() {

        Object object = _persistenceManager.load();

        if ( object != null ) {

            if ( object instanceof QTConfig ) {

                QTConfig appConfig = (QTConfig) object;

                // Global config
                QTGlobalConfig globalConfig = appConfig.getGlobalConfig();
                String colorSchemeName = globalConfig.getColorSchemeName();
                QTColorScheme colorScheme = globalConfig.getColorScheme().toQTColorScheme();
                _colorSchemeMenu.setColorScheme( colorSchemeName, colorScheme );
                
                // Modules config
                _module.load( appConfig.getModuleConfig() );
            }
            else {
                String message = QTResources.getString( "message.notAConfigFile" );
                String title = QTResources.getString( "title.error" );
                DialogUtils.showErrorDialog( getPhetFrame(), message, title );
            }
        }
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

                // Config
                PhetApplicationConfig config = new PhetApplicationConfig( args, QTConstants.FRAME_SETUP,  QTResources.getResourceLoader() );

                // Create the application.
                QTApplication app = new QTApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
