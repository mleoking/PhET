/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.phetgraphicsdemo;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;

/**
 * PhetgraphicsDemoApplication demonstrates how registration point, location
 * and transforms can be combined to create "self-contained behaviors" that
 * can be combined in composite graphics.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetgraphicsDemoApplication extends PhetApplication {

    public PhetgraphicsDemoApplication( PhetApplicationConfig config ) {
        super( config );
        TestModule module = new TestModule();
        addModule( module );
    }

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new PhetgraphicsDemoApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, PhetGraphicsDemoConstants.PROJECT_NAME );
        appConfig.launchSim();
    }
}
