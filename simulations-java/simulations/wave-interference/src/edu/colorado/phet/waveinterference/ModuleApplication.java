/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.phetcommon.application.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:18:50 AM
 */

public class ModuleApplication {

    public void startApplication( String[] args, final Module module ) {
        PhetApplicationConfig applicationConfig1 = new PhetApplicationConfig( args, null, "wave-interference" );
        applicationConfig1.setLookAndFeel( new WaveIntereferenceLookAndFeel() );
        new PhetApplicationLauncher().launchSim( applicationConfig1, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                PhetApplication moduleApplication = new ModulePhetApplication( config );
                moduleApplication.addModule( module );
                return moduleApplication;
            }
        } );
    }

    private class ModulePhetApplication extends PhetApplication {
        public ModulePhetApplication( PhetApplicationConfig config ) {
            super( config );
        }
    }
}
