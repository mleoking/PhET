package edu.colorado.phet.eatingandexercise;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

/**
 * Created by: Sam
 * Sep 11, 2008 at 10:21:11 PM
 */
public class EatingAndExerciseApplicationConfig extends PhetApplicationConfig {
    public EatingAndExerciseApplicationConfig( String[] args ) {
        super( args, EatingAndExerciseConstants.FRAME_SETUP, EatingAndExerciseResources.getResourceLoader() );
        setLookAndFeel( new EatingAndExerciseLookAndFeel() );
        setApplicationConstructor( new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new EatingAndExerciseApplication( config );
            }
        } );
    }

}