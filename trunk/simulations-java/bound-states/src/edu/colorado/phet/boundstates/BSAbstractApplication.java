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

import javax.swing.JMenuItem;

import edu.colorado.phet.boundstates.color.BSBlackColorScheme;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.color.BSColorsMenu;
import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.boundstates.module.BSManyWellsModule;
import edu.colorado.phet.boundstates.module.BSOneWellModule;
import edu.colorado.phet.boundstates.module.BSTwoWellsModule;
import edu.colorado.phet.boundstates.persistence.BSGlobalConfig;
import edu.colorado.phet.boundstates.persistence.BSPersistenceManager;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetFrameWorkaround;
import edu.colorado.phet.common.view.menu.HelpMenu;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;


/**
 * BSAbstractApplication is base class for all application in this "family".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
    public BSAbstractApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
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
     * Saves global state.
     * 
     * @param config
     */
    public void save( BSGlobalConfig config ) {
        
        // Application class name
        config.setApplicationClassName( this.getClass().getName() );
        
        // Version and build information
        config.setVersionNumber( BSVersion.NUMBER );
        config.setCvsTag( BSVersion.CVS_TAG );
        
        // Color scheme
        config.setColorSchemeName( _colorsMenu.getColorSchemeName() );
        config.setColorScheme( _colorsMenu.getColorScheme() );
        
        // Active module
        Module activeModule = getActiveModule();
        assert( activeModule instanceof BSAbstractModule ); // all modules are of this type
        BSAbstractModule module = (BSAbstractModule)activeModule;
        config.setActiveModuleId( module.getId() );
    }

    /**
     * Loads global state.
     * 
     * @param config
     */
    public void load( BSGlobalConfig config ) {

        String applicationClassName = config.getApplicationClassName();
        if ( ! this.getClass().getName().equals( applicationClassName ) ) {
            throw new IllegalStateException( "configuration file does not match this application" );
        }
        
        // Color scheme
        String colorSchemeName = config.getColorSchemeName();
        BSColorScheme colorScheme = config.getColorScheme().toBSColorScheme();
        _colorsMenu.setColorScheme( colorSchemeName, colorScheme );
        
        // Active module
        String id = config.getActiveModuleId();
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
}
