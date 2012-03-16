// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions;

import java.awt.Frame;

import edu.colorado.phet.advancedacidbasesolutions.module.comparing.ComparingModule;
import edu.colorado.phet.advancedacidbasesolutions.module.matchinggame.MatchingGameModule;
import edu.colorado.phet.advancedacidbasesolutions.module.solutions.SolutionsModule;
import edu.colorado.phet.advancedacidbasesolutions.persistence.AABSConfig;
import edu.colorado.phet.advancedacidbasesolutions.persistence.ComparingConfig;
import edu.colorado.phet.advancedacidbasesolutions.persistence.MatchingGameConfig;
import edu.colorado.phet.advancedacidbasesolutions.persistence.SolutionsConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class AdvancedAcidBaseSolutionsApplication extends PiccoloPhetApplication {

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
    public AdvancedAcidBaseSolutionsApplication( PhetApplicationConfig config )
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
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    @Override
    public void save() {
        
        AABSConfig appConfig = new AABSConfig();
        
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
            
            if ( object instanceof AABSConfig ) {
                AABSConfig appConfig = (AABSConfig) object;
                
                solutionsModule.load( appConfig.getSolutionsConfig() );
                comparingModule.load( appConfig.getComparingConfig() );
                matchingGameModule.load( appConfig.getMatchGameConfig() );
            }
            else {
                String message = AABSStrings.MESSAGE_NOT_A_CONFIG;
                PhetOptionPane.showErrorDialog( getPhetFrame(), message );
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
        new PhetApplicationLauncher().launchSim( args, AABSConstants.PROJECT_NAME, AdvancedAcidBaseSolutionsApplication.class );
    }
}
