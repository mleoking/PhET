package edu.colorado.phet.eatingandexercise.control;

import edu.colorado.phet.eatingandexercise.FitnessResources;
import edu.colorado.phet.eatingandexercise.FitnessStrings;

/**
 * Created by: Sam
 * Apr 30, 2008 at 9:01:42 AM
 */
public class ExerciseItem extends CaloricItem {
    public ExerciseItem( String name, String image, double cal ) {
        super( name, image, cal );
    }

    public String getLabelText() {
//        String perday = FitnessResources.getString( "per.day" );
        String onehour = FitnessResources.getString( "one.hour" );
//        return "<html>" + onehour + " " + getName() + " " + perday + "<br>(" + FitnessStrings.KCAL_PER_DAY_FORMAT.format( getCalories() ) + " " + FitnessStrings.KCAL_PER_DAY + ")</html>";
        return "<html>" + onehour + " " + getName() + " "+"<br>(" + FitnessStrings.KCAL_PER_DAY_FORMAT.format( getCalories() ) + " " + FitnessStrings.KCAL_PER_DAY + ")</html>";
    }
}
