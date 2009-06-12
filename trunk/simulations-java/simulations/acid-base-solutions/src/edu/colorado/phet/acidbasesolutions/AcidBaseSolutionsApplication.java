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
 * Main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class AcidBaseSolutionsApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private SolutionsModule solutionsModule;
    private ComparingModule comparingModule;
    private MatchingGameModule matchingGameModule;
    private FindUnknownModule findUnknownModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager persistenceManager;

    private static TabbedModulePanePiccolo tabbedModulePane;

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
                tabbedModulePane = new TabbedModulePanePiccolo();
                tabbedModulePane.setLogoVisible( false );
                return tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }
    
    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        solutionsModule = new SolutionsModule( parentFrame );
        addModule( solutionsModule );
        
        comparingModule = new ComparingModule( parentFrame );
        addModule( comparingModule );
        
        matchingGameModule = new MatchingGameModule( parentFrame );
        addModule( matchingGameModule );
        
        findUnknownModule = new FindUnknownModule( parentFrame );
        addModule( findUnknownModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( persistenceManager == null ) {
            persistenceManager = new XMLPersistenceManager( frame );
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
        return tabbedModulePane;
    }
    
    public SolutionsModule dev_getSolutionsModule() {
        return solutionsModule;
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
        
        SolutionsConfig solutionsConfig = solutionsModule.save();
        appConfig.setSolutionsConfig( solutionsConfig );
        
        ComparingConfig comparingConfig = comparingModule.save();
        appConfig.setComparingConfig( comparingConfig );
        
        MatchingGameConfig matchingGameConfig = matchingGameModule.save();
        appConfig.setMatchGameConfig( matchingGameConfig );
        
        FindUnknownConfig findUnknownConfig = findUnknownModule.save();
        appConfig.setFindUnknownConfig( findUnknownConfig );
        
        persistenceManager.save( appConfig );
    }

    /*
     * Loads the simulation's configuration.
     */
    private void load() {
        
        Object object = persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof ABSConfig ) {
                ABSConfig appConfig = (ABSConfig) object;
                
                solutionsModule.load( appConfig.getSolutionsConfig() );
                comparingModule.load( appConfig.getComparingConfig() );
                matchingGameModule.load( appConfig.getMatchGameConfig() );
                findUnknownModule.load( appConfig.getFindUnknownConfig() );
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
