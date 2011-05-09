// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.util.ArrayList;

/**
 * Created by: Sam
 * Apr 23, 2008 at 1:34:08 PM
 */
public abstract class CaloricItem implements Cloneable {
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

    public Object clone() {
        try {
            CaloricItem item = (CaloricItem) super.clone();
            item.name = name;
            item.image = image;
            item.cal = cal;
            return item;
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    protected void setCalories( double totalCalories ) {
        this.cal = totalCalories;
        notifyCaloriesChanged();
    }

    public static interface Listener {
        void caloriesChanged();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyCaloriesChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).caloriesChanged();
        }
    }
}
