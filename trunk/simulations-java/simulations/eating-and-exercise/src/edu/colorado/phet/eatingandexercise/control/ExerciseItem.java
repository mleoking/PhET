package edu.colorado.phet.eatingandexercise.control;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;

/**
 * Created by: Sam
 * Apr 30, 2008 at 9:01:42 AM
 */
public class ExerciseItem extends CaloricItem {
    public ExerciseItem( String name, String image, double cal ) {
        super( name, image, cal );
    }

    public String getLabelText() {
        String onehour = EatingAndExerciseResources.getString( "one.hour" );
        return "<html>" + onehour + " " + getName() + " " + "<br>(" + EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT.format( getCalories() ) + " " + EatingAndExerciseStrings.KCAL_PER_DAY + ")</html>";
    }
}
