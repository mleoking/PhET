/**
 * Class: SoundApplication
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;


public class SoundApplication extends PiccoloPhetApplication {
    
    public SoundApplication( PhetApplicationConfig config ) {
        super( config );

        //TODO performance tanks when window is made bigger
        getPhetFrame().setResizable( false );
        
        // Set up the modules
        Module singleSourceModule = new SingleSourceListenModule( );
        addModule( singleSourceModule );
        Module measureModule = new SingleSourceMeasureModule( this );
        addModule( measureModule );
        Module twoSourceIntereferenceModule = new TwoSpeakerInterferenceModule();
        addModule( twoSourceIntereferenceModule );
        Module wallInterferenceModule = new WallInterferenceModule();
        addModule( wallInterferenceModule );
        Module evacuatedBoxModule = new SingleSourceWithBoxModule();
        addModule( evacuatedBoxModule );
    }
    
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new SoundApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, SoundConfig.PROJECT_NAME );
        appConfig.setFrameSetup( new FrameSetup.CenteredWithSize( 900, 750 ) );
        appConfig.launchSim();
    }
}
