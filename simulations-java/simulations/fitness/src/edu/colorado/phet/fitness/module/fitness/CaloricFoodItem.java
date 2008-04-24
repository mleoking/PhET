package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.fitness.control.CaloricItem;

/**
 * Created by: Sam
 * Apr 23, 2008 at 6:31:17 PM
 */
public class CaloricFoodItem extends CaloricItem {
    private double lipids;
    private double carbs;
    private double protein;

    public CaloricFoodItem( String name, String image, double cal, double lipids, double carbs, double protein ) {
        super( name, image, cal );
        this.lipids = lipids;
        this.carbs = carbs;
        this.protein = protein;
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
        return lipids;
    }

    public double getCarbCalories() {
        return carbs;
    }

    public double getProteinCalories() {
        return protein;
    }

}
