package edu.colorado.phet.eatingandexercise;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.eatingandexercise.view.EatingAndExerciseColorScheme;

/**
 * Created by: Sam
 * Sep 11, 2008 at 10:09:35 PM
 */
public class EatingAndExerciseLookAndFeel extends PhetLookAndFeel {
    public EatingAndExerciseLookAndFeel() {
        setFont( new PhetFont( 14, true ) );
        setBackgroundColor( EatingAndExerciseColorScheme.getBackgroundColor() );
        setTextFieldBackgroundColor( Color.white );
    }
}
