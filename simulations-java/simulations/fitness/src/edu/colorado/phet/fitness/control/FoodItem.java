package edu.colorado.phet.fitness.control;

/**
 * Created by: Sam
 * Apr 17, 2008 at 11:41:43 AM
 */
public class FoodItem {
    private String image;
    private double calories;

    public FoodItem( String image, double calories ) {
        this.image = image;
        this.calories = calories;
    }

    public String getImage() {
        return image;
    }
}
