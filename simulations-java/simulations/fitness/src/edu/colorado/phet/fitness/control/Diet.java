package edu.colorado.phet.fitness.control;

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

    public double getCarb() {
        return carb;
    }

    public double getProtein() {
        return protein;
    }
}
