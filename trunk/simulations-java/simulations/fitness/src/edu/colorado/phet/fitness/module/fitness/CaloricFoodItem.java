package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.fitness.control.CaloricItem;
import edu.colorado.phet.fitness.FitnessStrings;

/**
 * Created by: Sam
 * Apr 23, 2008 at 6:31:17 PM
 */
public class CaloricFoodItem extends CaloricItem {
    private double lipids;//grams
    private double carbs;
    private double protein;
    private boolean removable;

    public CaloricFoodItem( String name, String image, double cal, double lipids, double carbs, double protein ) {
        this( name, image, cal, lipids, carbs, protein, true );
    }

    public CaloricFoodItem( String name, String image, double cal, double lipids, double carbs, double protein, boolean removable ) {
        super( name, image, cal );
        this.lipids = lipids;
        this.carbs = carbs;
        this.protein = protein;
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }

    public double getLipids() {
        return lipids;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getProtein() {
        return protein;
    }

    public double getLipidCalories() {
        return lipids * 9;
    }

    public double getCarbCalories() {
        return carbs * 4;
    }

    public double getProteinCalories() {
        return protein * 4;
    }

    public String toString() {
        return getName() + ", calories=" + getCalories() + ", lipids=" + lipids + ", carbs=" + carbs + ", proteins=" + protein + ", image=" + getImage();
    }

    public String getLabelText() {
        return "<html>One " + getName() + " per day<br>(" + getCalories() + " "+ FitnessStrings.KCAL_PER_DAY+")</html>";
    }
}
