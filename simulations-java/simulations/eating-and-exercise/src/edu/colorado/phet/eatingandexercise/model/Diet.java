// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.model;

/**
 * Created by: Sam
 * Apr 18, 2008 at 9:07:29 AM
 */
public class Diet {
    private String name;
    private double fat;
    private double carb;
    private double protein;

    public Diet( String name, double fat, double carb, double protein ) {
        this.name = name;
        this.fat = fat;
        this.carb = carb;
        this.protein = protein;
    }

    public String getName() {
        return name;
    }

    public double getFat() {
        return fat;
    }

    public double getTotal() {
        return fat + carb + protein;
    }

    public double getCarb() {
        return carb;
    }

    public double getProtein() {
        return protein;
    }

    public String toString() {
        return name + ", Fat Calories/Day: " + fat + ", Carb Calories/Day: " + carb + ", Protein Calories/Day: " + protein;
    }

    public Diet getInstanceOfMagnitude( double magnitude ) {
        double actual = getTotalCalories();
        return new Diet( name + " scaled", fat * magnitude / actual, carb * magnitude / actual, protein * magnitude / actual );
    }

    private double getTotalCalories() {
        return fat + carb + protein;
    }
}
