/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingAtomModule;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;

/**
 * RutherfordScatteringApplication is the main class for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RutherfordScatteringApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public RutherfordScatteringApplication( PhetApplicationConfig config ) {
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
        addModule( new RutherfordAtomModule() );
        addModule( new PlumPuddingAtomModule() );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {
    // do nothing for this sim
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------
    
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new RutherfordScatteringApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, RSConstants.PROJECT_NAME );
        appConfig.launchSim();
    }
}
