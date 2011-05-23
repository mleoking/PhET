// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 */

public class TheRampApplication extends PiccoloPhetApplication {

    private final RampModule simpleRampModule;
    private final RampModule advancedFeatureModule;

    public TheRampApplication( PhetApplicationConfig config ) {
        super( config );
        simpleRampModule = new SimpleRampModule( getPhetFrame(), createClock() );
        advancedFeatureModule = new RampModule( getPhetFrame(), createClock() );
        setModules( new Module[] { simpleRampModule, advancedFeatureModule } );
    }

    private IClock createClock() {
        return new SwingClock( 30, 1.0 / 30.0 );
    }

    public void startApplication() {
        super.startApplication();
        simpleRampModule.getPhetPCanvas().requestFocus();
        simpleRampModule.applicationStarted();
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, TheRampConstants.PROJECT_NAME, TheRampApplication.class );
    }
}