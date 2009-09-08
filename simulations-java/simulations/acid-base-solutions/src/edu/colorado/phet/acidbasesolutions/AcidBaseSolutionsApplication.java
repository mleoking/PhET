/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import java.awt.Color;
import java.awt.Frame;

import javax.swing.JOptionPane;

import edu.colorado.phet.acidbasesolutions.developer.DeveloperMenu;
import edu.colorado.phet.acidbasesolutions.module.comparing.ComparingModule;
import edu.colorado.phet.acidbasesolutions.module.matchinggame.MatchingGameModule;
import edu.colorado.phet.acidbasesolutions.module.solutions.SolutionsModule;
import edu.colorado.phet.acidbasesolutions.persistence.ABSConfig;
import edu.colorado.phet.acidbasesolutions.persistence.ComparingConfig;
import edu.colorado.phet.acidbasesolutions.persistence.MatchingGameConfig;
import edu.colorado.phet.acidbasesolutions.persistence.SolutionsConfig;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

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

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager persistenceManager;

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
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

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
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        // File->Save/Load
        final PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();
        if ( persistenceManager == null ) {
            persistenceManager = new XMLPersistenceManager( frame );
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

    public SolutionsModule dev_getSolutionsModule() {
        return solutionsModule;
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    @Override
    public void save() {
        
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
        
        persistenceManager.save( appConfig );
    }

    /**
     * Loads the simulation's configuration.
     */
    @Override
    public void load() {
        
        Object object = persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof ABSConfig ) {
                ABSConfig appConfig = (ABSConfig) object;
                
                solutionsModule.load( appConfig.getSolutionsConfig() );
                comparingModule.load( appConfig.getComparingConfig() );
                matchingGameModule.load( appConfig.getMatchGameConfig() );
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
