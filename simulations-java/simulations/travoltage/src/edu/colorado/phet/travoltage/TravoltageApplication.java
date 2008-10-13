/*  */
package edu.colorado.phet.travoltage;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:20:53 PM
 */

public class TravoltageApplication extends PhetApplication {
    
    public TravoltageApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new TravoltageModule() );
    }

    public static class TravoltageFrameSetup implements FrameSetup {
        public void initialize( JFrame frame ) {
            new FrameSetup.CenteredWithSize( 800, 600 ).initialize( frame );
        }
    }

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new TravoltageApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, TravoltageConstants.PROJECT_NAME );
        appConfig.setFrameSetup( new TravoltageFrameSetup() );
        appConfig.launchSim();
    }
}
