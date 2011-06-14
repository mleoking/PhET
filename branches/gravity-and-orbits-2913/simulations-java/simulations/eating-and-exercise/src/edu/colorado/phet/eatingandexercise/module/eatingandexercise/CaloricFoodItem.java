// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.control.CaloricItem;

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
        String one = EatingAndExerciseResources.getString( "one" );
        return "<html>" + one + " " + getName() + " <br>(" + EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT.format( getCalories() ) + " " + EatingAndExerciseStrings.KCAL_PER_DAY + ")</html>";
    }

    public void setTotalCalories( double totalCalories ) {
        double scale = totalCalories / getCalories();
        double newLipidCalories = getLipidCalories() * scale;
        double newCarbCalories = getCarbCalories() * scale;
        double newProteinCalories = getProteinCalories() * scale;
        this.lipids = newLipidCalories / 9;
        this.carbs = newCarbCalories / 4;
        this.protein = newProteinCalories / 4;
        super.setCalories( getTotalCalories() );
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
