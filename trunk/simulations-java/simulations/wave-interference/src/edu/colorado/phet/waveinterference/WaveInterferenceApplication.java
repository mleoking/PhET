// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 */

public class WaveInterferenceApplication extends PiccoloPhetApplication {

    public WaveInterferenceApplication( PhetApplicationConfig config ) {
        super( config );
        WaveInterferenceMenu menu = new WaveInterferenceMenu();
        addModule( new WaterModule() );
        addModule( new SoundModule( config.isDev() ) );
        LightModule lightModule = new LightModule();
        addModule( lightModule );
        menu.add( new ColorizeCheckBoxMenuItem( lightModule ) );
        if ( config.isDev() ) {
            getPhetFrame().addMenu( menu );
        }
        if ( getModules().length > 1 ) {
            for ( int i = 0; i < getModules().length; i++ ) {
                getModule( i ).setLogoPanelVisible( false );
            }
        }
    }

    public static void main( final String[] args ) {
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new WaveInterferenceApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, WaveInterferenceConstants.PROJECT_NAME );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }

}
