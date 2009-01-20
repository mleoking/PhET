/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.colorado.phet.acidbasesolutions.developer.DeveloperMenu;
import edu.colorado.phet.acidbasesolutions.menu.OptionsMenu;
import edu.colorado.phet.acidbasesolutions.module.comparing.ComparingModule;
import edu.colorado.phet.acidbasesolutions.module.findunknown.FindUnknownModule;
import edu.colorado.phet.acidbasesolutions.module.matchinggame.MatchingGameModule;
import edu.colorado.phet.acidbasesolutions.module.solutions.SolutionsModule;
import edu.colorado.phet.acidbasesolutions.persistence.*;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;

/**
 * AcidBaseSolutionsApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class AcidBaseSolutionsApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private SolutionsModule _solutionsModule;
    private ComparingModule _comparingModule;
    private MatchingGameModule _matchingGameModule;
    private FindUnknownModule _findUnknownModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager _persistenceManager;

    private static TabbedModulePanePiccolo _tabbedModulePane;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public AcidBaseSolutionsApplication( PhetApplicationConfig config )
    {
        super( config );
        initTabbedPane();
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the tabbed pane.
     */
    private void initTabbedPane() {

        // Create our own tabbed pane type so we can set the tab color
        TabbedPaneType tabbedPaneType = new TabbedPaneType(){
            public ITabbedModulePane createTabbedPane() {
                _tabbedModulePane = new TabbedModulePanePiccolo();
                _tabbedModulePane.setLogoVisible( false );
                return _tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }
    
    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        _solutionsModule = new SolutionsModule( parentFrame );
        addModule( _solutionsModule );
        
        _comparingModule = new ComparingModule( parentFrame );
        addModule( _comparingModule );
        
        _matchingGameModule = new MatchingGameModule( parentFrame );
        addModule( _matchingGameModule );
        
        _findUnknownModule = new FindUnknownModule( parentFrame );
        addModule( _findUnknownModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( ABSResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( ABSResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( ABSResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( ABSResources.getChar( "menu.file.load.mnemonic", 'L' ) );
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
        OptionsMenu optionsMenu = new OptionsMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        DeveloperMenu developerMenu = new DeveloperMenu( this );
        if ( developerMenu.getMenuComponentCount() > 0 && isDeveloperControlsEnabled() ) {
            frame.addMenu( developerMenu );
        }
    }

    //----------------------------------------------------------------------------
    // Setters & getters
    //----------------------------------------------------------------------------

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }

    public PhetTabbedPane getTabbedPane() {
        return _tabbedModulePane;
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /*
     * Saves the simulation's configuration.
     */
    private void save() {
        
        ABSConfig appConfig = new ABSConfig();
        
        appConfig.setVersionString( getSimInfo().getVersion().toString() );
        appConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        appConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        appConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        appConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );
        
        SolutionsConfig solutionsConfig = _solutionsModule.save();
        appConfig.setSolutionsConfig( solutionsConfig );
        
        ComparingConfig comparingConfig = _comparingModule.save();
        appConfig.setComparingConfig( comparingConfig );
        
        MatchingGameConfig matchingGameConfig = _matchingGameModule.save();
        appConfig.setMatchGameConfig( matchingGameConfig );
        
        FindUnknownConfig findUnknownConfig = _findUnknownModule.save();
        appConfig.setFindUnknownConfig( findUnknownConfig );
        
        _persistenceManager.save( appConfig );
    }

    /*
     * Loads the simulation's configuration.
     */
    private void load() {
        
        Object object = _persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof ABSConfig ) {
                ABSConfig appConfig = (ABSConfig) object;
                
                _solutionsModule.load( appConfig.getSolutionsConfig() );
                _comparingModule.load( appConfig.getComparingConfig() );
                _matchingGameModule.load( appConfig.getMatchGameConfig() );
                _findUnknownModule.load( appConfig.getFindUnknownConfig() );
            }
            else {
                String message = ABSStrings.MESSAGE_NOT_A_CONFIG;
                String title = ABSStrings.TITLE_ERROR;
                JOptionPane.showMessageDialog( getPhetFrame(), message, title, JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        /* 
         * If you want to customize your application (look-&-feel, window size, etc) 
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, ABSConstants.PROJECT_NAME, AcidBaseSolutionsApplication.class );
    }
}
