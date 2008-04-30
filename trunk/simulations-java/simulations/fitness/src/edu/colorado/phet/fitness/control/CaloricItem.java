package edu.colorado.phet.fitness.control;

/**
 * Created by: Sam
 * Apr 23, 2008 at 1:34:08 PM
 */
public abstract class CaloricItem {
    private String name;
    private String image;
    private double cal;

    public CaloricItem( String name, String image, double cal ) {
        this.name = name;
        this.image = image;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getCalories() {
        return cal;
    }

    public String toString() {
        return "name=" + name + ", image=" + image + ", cal=" + cal;
    }

    public abstract String getLabelText();
}
