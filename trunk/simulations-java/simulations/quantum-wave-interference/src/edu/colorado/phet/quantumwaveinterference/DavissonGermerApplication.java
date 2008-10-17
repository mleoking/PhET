/*  */
package edu.colorado.phet.quantumwaveinterference;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.quantumwaveinterference.davissongermer.DGModule;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 */

public class DavissonGermerApplication extends PiccoloPhetApplication {

    public DavissonGermerApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new DGModule( this, createClock() ) );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    protected PhetFrame createPhetFrame( PhetApplication phetApplication ) {
        return new PhetFrameWorkaround( phetApplication );
    }

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new DavissonGermerApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, QWIConstants.PROJECT_NAME, QWIConstants.FLAVOR_DAVISSON_GERMER );
        appConfig.setLookAndFeel( new QWIPhetLookAndFeel() );
        appConfig.setFrameSetup( new QWIFrameSetup() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
