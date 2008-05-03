package edu.colorado.phet.fitness.control;

import edu.colorado.phet.fitness.FitnessStrings;

/**
 * Created by: Sam
 * Apr 30, 2008 at 9:01:42 AM
 */
public class ExerciseItem extends CaloricItem {
    public ExerciseItem( String name, String image, double cal ) {
        super( name, image, cal );
    }

    public String getLabelText() {
        return "<html>One hour " + getName() + " per day<br>(" + getCalories() + " " + FitnessStrings.KCAL_PER_DAY+")</html>";
    }
}
