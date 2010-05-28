/**
 * Class: SoundApplication
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;


public class SoundApplication extends PiccoloPhetApplication {
    
    public SoundApplication( PhetApplicationConfig config ) {
        super( config );

        //TODO performance tanks when window is made bigger
        getPhetFrame().setResizable( false );
        
        // Set up the modules
        addModule( new SingleSourceListenModule( ) );
        addModule( new SingleSourceMeasureModule( this ) );
        addModule( new TwoSpeakerInterferenceModule() );
        addModule( new WallInterferenceModule() );
        addModule( new SingleSourceWithBoxModule() );
    }
    
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new SoundApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, SoundConfig.PROJECT_NAME );
        appConfig.setFrameSetup( new FrameSetup.CenteredWithSize( 900, 750 ) );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
