/**
 * Class: SoundApplication
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;

public class SoundApplication extends PhetApplication {

    private static class SoundApplicationModel extends ApplicationModel {

        private final static String windowTitle = "Sound";
        private final static String description = "How sound waves work and are heard";
        private final static String version = "1.1";

        public SoundApplicationModel() {
            super( windowTitle, description, version );

            // Specify the clock
//            this.setClock( new ThreadedClock( SoundConfig.s_timeStep, SoundConfig.s_waitTime, true ));
            this.setClock( new SwingTimerClock( SoundConfig.s_timeStep, SoundConfig.s_waitTime ));
//            this.setClock( new SwingTimerClock( 1, 20, true ));
//            this.setClock( new SwingTimerClock( 5, 50 ));

            // Set up the modules
            Module singleSourceModule = new SingleSourceListenModule( this );
            Module measureModule = new SingleSourceMeasureModule( this );
            this.setModules( new Module[]{ singleSourceModule, measureModule } );
            this.setInitialModule( singleSourceModule );
        }
    }

    public SoundApplication() {
        super( new SoundApplicationModel( ) );
    }

    public static void main( String[] args ) {
        PhetApplication app = new SoundApplication();
        app.startApplication();
    }
}
