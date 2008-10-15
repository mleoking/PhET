/* Copyright 2006-2007, University of Colorado */

package edu.colorado.phet.boundstates;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.colorado.phet.boundstates.color.BSBlackColorScheme;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.color.BSColorsMenu;
import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.boundstates.module.BSManyWellsModule;
import edu.colorado.phet.boundstates.module.BSOneWellModule;
import edu.colorado.phet.boundstates.module.BSTwoWellsModule;
import edu.colorado.phet.boundstates.persistence.BSConfig;
import edu.colorado.phet.boundstates.persistence.BSGlobalConfig;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.menu.HelpMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;


/**
 * BSAbstractApplication is base class for all application in this "family".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BSAbstractApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Save/Load feature enable
    private static final boolean SAVE_LOAD_ENABLED = true;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BSOneWellModule _oneWellModule;
    private BSTwoWellsModule _twoWellsModule;
    private BSManyWellsModule _manyWellsModule;

    // PersistanceManager handles loading/saving application configurations.
    private XMLPersistenceManager _persistenceManager;

    private BSColorsMenu _colorsMenu;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public BSAbstractApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    protected abstract void initModules();

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        PhetFrame frame = getPhetFrame();

        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( BSResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( BSResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( BSResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( BSResources.getChar( "menu.file.load.mnemonic", 'L' ) );
            loadItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    load();
                }
            } );

            frame.addFileMenuItem( saveItem );
            frame.addFileMenuItem( loadItem );
            frame.addFileMenuSeparator();

            saveItem.setEnabled( SAVE_LOAD_ENABLED );
            loadItem.setEnabled( SAVE_LOAD_ENABLED );
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

    protected void addOneWellModule() {
        _oneWellModule = new BSOneWellModule();
        addModule( _oneWellModule );
    }

    public BSAbstractModule getOneWellModule() {
        return _oneWellModule;
    }

    protected void addTwoWellsModule() {
        _twoWellsModule = new BSTwoWellsModule();
        addModule( _twoWellsModule );
    }

    public BSAbstractModule getTwoWellsModule() {
        return _twoWellsModule;
    }

    protected void addManyWellsModule() {
        _manyWellsModule = new BSManyWellsModule();
        addModule( _manyWellsModule );
    }

    public BSAbstractModule getManyWellsModule() {
        return _manyWellsModule;
    }

    //----------------------------------------------------------------------------
    // PhetApplication overrides
    //----------------------------------------------------------------------------

    public void startApplication() {
        super.startApplication();
        // Do things that need to be done after starting the application...
    }

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
        
        BSConfig appConfig = new BSConfig();

        // Global config
        {
            BSGlobalConfig globalConfig = new BSGlobalConfig();
            appConfig.setGlobalConfig( globalConfig );
            globalConfig.setApplicationClassName( this.getClass().getName() );
            globalConfig.setVersionNumber( getPhetApplicationConfig().getVersion().toString() );
            globalConfig.setColorSchemeName( _colorsMenu.getColorSchemeName() );
            globalConfig.setColorScheme( _colorsMenu.getColorScheme() );

            // Active module
            Module activeModule = getActiveModule();
            assert ( activeModule instanceof BSAbstractModule ); // all modules are of this type
            BSAbstractModule module = (BSAbstractModule) activeModule;
            globalConfig.setActiveModuleId( module.getId() );
        }
        
        // Modules config
        appConfig.setOneWellModuleConfig( _oneWellModule.save() );
        appConfig.setTwoWellsModuleConfig( _twoWellsModule.save() );
        appConfig.setManyWellsModuleConfig( _manyWellsModule.save() );
        
        _persistenceManager.save( appConfig );
    }

    /**
     * Loads a configuration.
     *
     * @param config
     */
    public void load() {

        Object object = _persistenceManager.load();
        
        if ( object != null ) {
            
            if ( object instanceof BSConfig ) {

                BSConfig appConfig = (BSConfig) object;

                // Global config
                {
                    BSGlobalConfig globalConfig = appConfig.getGlobalConfig();

                    // Color scheme
                    String colorSchemeName = globalConfig.getColorSchemeName();
                    BSColorScheme colorScheme = globalConfig.getColorScheme().toBSColorScheme();
                    _colorsMenu.setColorScheme( colorSchemeName, colorScheme );

                    // Active module
                    String id = globalConfig.getActiveModuleId();
                    Module[] modules = getModules();
                    for ( int i = 0; i < modules.length; i++ ) {
                        assert ( modules[i] instanceof BSAbstractModule ); // all module are of this type
                        BSAbstractModule module = (BSAbstractModule) modules[i];
                        if ( id.equals( module.getId() ) ) {
                            setActiveModule( module );
                            break;
                        }
                    }
                }

                // Modules config
                _oneWellModule.load( appConfig.getOneWellModuleConfig() );
                _twoWellsModule.load( appConfig.getTwoWellsModuleConfig() );
                _manyWellsModule.load( appConfig.getManyWellsModuleConfig() );
            }
            else {
                String message = BSResources.getString( "message.notAConfigFile" );
                String title = BSResources.getString( "title.error" );
                JOptionPane.showMessageDialog( getPhetFrame(), message, title, JOptionPane.ERROR_MESSAGE );
            }
        }
    }
}
