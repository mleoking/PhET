// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.model.CalorieSet;

/**
 * Created by: Sam
 * Apr 23, 2008 at 6:42:31 PM
 */
public class FoodCalorieSet extends CalorieSet {
    public FoodCalorieSet() {
    }

    public FoodCalorieSet( CaloricFoodItem[] caloricItems ) {
        super( caloricItems );
    }

    public double getTotalProteinCalories() {
        double sum = 0;
        for ( int i = 0; i < getItemCount(); i++ ) {
            sum += ( (CaloricFoodItem) getItem( i ) ).getProteinCalories();
        }
        return sum;
    }

    public double getTotalLipidCalories() {
        double sum = 0;
        for ( int i = 0; i < getItemCount(); i++ ) {
            sum += ( (CaloricFoodItem) getItem( i ) ).getLipidCalories();
        }
        return sum;
    }

    public double getTotalCarbCalories() {
        double sum = 0;
        for ( int i = 0; i < getItemCount(); i++ ) {
            sum += ( (CaloricFoodItem) getItem( i ) ).getCarbCalories();
        }
        return sum;
    }
}
