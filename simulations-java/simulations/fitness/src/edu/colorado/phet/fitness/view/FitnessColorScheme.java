package edu.colorado.phet.fitness.view;

import java.awt.*;

import edu.colorado.phet.fitness.util.FitnessUtil;

/**
 * Created by: Sam
 * Apr 17, 2008 at 8:32:19 PM
 */
public class FitnessColorScheme {
    public static final Color BMR = new Color( 189, 46, 13 );
    public static final Color ACTIVITY = new Color( 248, 125, 17 );
    public static final Color EXERCISE = new Color( 240, 248, 10 );

//    public static final Color PROTEIN = FitnessUtil.brighter( new Color( 17, 93, 24 ), 20 );
//    public static final Color CARBS = FitnessUtil.brighter( new Color( 29, 32, 175 ), 20 );
//    public static final Color FATS = FitnessUtil.brighter( new Color( 75, 3, 75 ), 20 );

    public static final Color PROTEIN = FitnessUtil.brighter( new Color( 17, 93, 24 ), 40 );
    public static final Color CARBS = FitnessUtil.brighter( new Color( 29, 32, 175 ), 40 );
    public static final Color FATS = FitnessUtil.brighter( new Color( 75, 3, 75 ), 40 );

    public static Color getBackgroundColor() {
        return new Color( 200, 240, 200 );
    }
}
