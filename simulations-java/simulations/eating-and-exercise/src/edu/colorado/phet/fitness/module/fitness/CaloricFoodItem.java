package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.colorado.phet.fitness.control.CaloricItem;

/**
 * Created by: Sam
 * Apr 23, 2008 at 6:31:17 PM
 */
public class CaloricFoodItem extends CaloricItem {
    private double lipids;//grams
    private double carbs;
    private double protein;
    private boolean removable;

    public CaloricFoodItem( String name, String image, double lipids, double carbs, double protein ) {
        this( name, image, lipids, carbs, protein, true );
    }

    public CaloricFoodItem( String name, String image, double lipids, double carbs, double protein, boolean removable ) {
        super( name, image, lipids * 9 + carbs * 4 + protein * 4 );
        this.lipids = lipids;
        this.carbs = carbs;
        this.protein = protein;
        this.removable = removable;
//        System.out.println( "cal=" + cal + ", tcal=" + getTotalCalories() + ", c=" + getCalories() );
    }

    private double getTotalCalories() {
        return getLipidCalories() + getCarbCalories() + getProteinCalories();
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
        String one = FitnessResources.getString( "one" );
        return "<html>" + one + " " + getName() + " <br>(" + FitnessStrings.KCAL_PER_DAY_FORMAT.format( getCalories() ) + " " + FitnessStrings.KCAL_PER_DAY + ")</html>";
//        String perDay = FitnessResources.getString( "per.day" );
//        return "<html>" + one + " " + getName() + " " + perDay + "<br>(" + FitnessStrings.KCAL_PER_DAY_FORMAT.format( getCalories() ) + " " + FitnessStrings.KCAL_PER_DAY + ")</html>";
    }

    public Object clone() {
        CaloricFoodItem item = (CaloricFoodItem) super.clone();
        item.lipids = lipids;
        item.carbs = carbs;
        item.protein = protein;
        item.removable = removable;
        return item;
    }
}
