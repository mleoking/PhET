/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:18:50 AM
 */

public class ModuleApplication {

    public void startApplication( String[] args, final Module module ) {
        PhetApplicationConfig applicationConfig1 = new PhetApplicationConfig( args, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                PhetApplication moduleApplication = new ModulePhetApplication( config );
                moduleApplication.addModule( module );
                return moduleApplication;
            }
        }, "wave-interference" );
        applicationConfig1.setLookAndFeel( new WaveIntereferenceLookAndFeel() );
        applicationConfig1.launchSim();
    }

    private class ModulePhetApplication extends PhetApplication {
        public ModulePhetApplication( PhetApplicationConfig config ) {
            super( config );
        }
    }
}
