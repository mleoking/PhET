/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * MVCApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MVCExampleApplication extends PiccoloPhetApplication {

    private static final String PROJECT_NAME = "mvc-example";
    public static final PhetResources RESOURCE_LOADER = new PhetResources( PROJECT_NAME );
    
    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public MVCExampleApplication( PhetApplicationConfig config )
    {
        super( config );
        Frame parentFrame = getPhetFrame();
        MVCModule module = new MVCModule( parentFrame );
        addModule( module );
    }
    
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MVCExampleApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, PROJECT_NAME );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
