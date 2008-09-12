package edu.colorado.phet.eatingandexercise;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

/**
 * Created by: Sam
 * Sep 11, 2008 at 10:21:11 PM
 */
public class EatingAndExerciseApplicationConfig extends PhetApplicationConfig {
    private ApplicationConstructor applicationConstructor;
    private PhetLookAndFeel phetLookAndFeel;

    public EatingAndExerciseApplicationConfig( String[] args ) {
        super( args, EatingAndExerciseConstants.FRAME_SETUP, EatingAndExerciseResources.getResourceLoader() );
        setLookAndFeel( new EatingAndExerciseLookAndFeel() );
        setApplicationConstructor( new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new EatingAndExerciseApplication( config );
            }
        } );
    }

    private void setApplicationConstructor( ApplicationConstructor applicationConstructor ) {
        this.applicationConstructor = applicationConstructor;
    }

    private void setLookAndFeel( PhetLookAndFeel phetLookAndFeel ) {
        this.phetLookAndFeel = phetLookAndFeel;
    }

    public ApplicationConstructor getApplicationConstructor() {
        return applicationConstructor;
    }

    public PhetLookAndFeel getPhetLookAndFeel() {
        return phetLookAndFeel;
    }

    public void launchSim() {
        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                PhetLookAndFeel lookAndFeel = getPhetLookAndFeel();
                if ( lookAndFeel != null ) {
                    lookAndFeel.initLookAndFeel();
                }
                ApplicationConstructor applicationConstructor = getApplicationConstructor();
                if ( applicationConstructor != null ) {
                    PhetApplication app = applicationConstructor.getApplication( EatingAndExerciseApplicationConfig.this );
                    app.startApplication();
                }
            }
        } );
    }
}