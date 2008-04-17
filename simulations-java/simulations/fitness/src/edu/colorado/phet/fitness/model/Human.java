package edu.colorado.phet.fitness.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import edu.colorado.phet.fitness.control.FoodItem;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:05:20 PM
 */
public class Human {
    private double age = 20 * 525600.0 * 60;//seconds
    private double height = 1.5;//meters
    private double weight = 75;//kg
    private Gender gender = Gender.MALE;
    private String name = "Larry";
    private ArrayList listeners = new ArrayList();
    private double musclePercent = 60;

    public HashSet foods = new HashSet();

    public Human() {
    }

    public Human( double age, double height, double weight, Gender gender, String name ) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.name = name;
    }

    /**
     * http://usmilitary.about.com/od/airforcejoin/a/afmaxweight.htm
     * The formula to compute BMI is
     * weight (in pounds) divided by the square of height (in inches),
     * multiplied by 704.5
     * <p/>
     * (Don't worry about that though, the below chart shows the maximum and minimum weights using the formula).
     *
     * @return
     */
    public double getBMIOrig() {
        return getWeightPounds() / Math.pow( getHeightInches(), 2 ) * 704.5;
    }

    public double getBMI() {
        return getWeight() / Math.pow( getHeight(), 2 );
    }

    private double getHeightInches() {
        return getHeight() / 0.0254;
    }

    private double getWeightPounds() {
        return getWeight() * 2.20462262;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public double getMusclePercent() {
        return musclePercent;
    }

    public double getFatPercent() {
        return 100 - musclePercent;
    }

    public void setMusclePercent( double value ) {
        this.musclePercent = value;
        notifyMusclePercentChanged();
        notifyFatPercentChanged();
    }

    private void notifyFatPercentChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.fatPercentChanged();
        }
    }

    private void notifyMusclePercentChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.musclePercentChanged();
        }
    }

    public void setFatPercent( double value ) {
        this.musclePercent = 100 - value;
        notifyFatPercentChanged();
        notifyMusclePercentChanged();
    }

    public double getDailyCaloricIntake() {
        double sum = 0;
        for ( Iterator iterator = foods.iterator(); iterator.hasNext(); ) {
            FoodItem foodItem = (FoodItem) iterator.next();
            sum += foodItem.getCalories();
        }
        return sum;
    }

    public static class Gender {
        public static Gender MALE = new Gender( "male" );
        public static Gender FEMALE = new Gender( "female" );
        private String name;

        private Gender( String name ) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public double getAge() {
        return age;
    }

    public void setAge( double age ) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight( double height ) {
        this.height = height;
        notifyHeightChanged();
        notifyBMIChanged();
    }

    private void notifyBMIChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.bmiChanged();
        }
    }

    private void notifyHeightChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.heightChanged();
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight( double weight ) {
        this.weight = weight;
        notifyWeightChanged();
        notifyBMIChanged();
    }

    private void notifyWeightChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.weightChanged();
        }
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender( Gender gender ) {
        if ( this.gender != gender ) {
            this.gender = gender;
            notifyGenderChanged();
        }
    }

    private void notifyGenderChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).genderChanged();
        }
    }

    public static interface Listener {
        void bmiChanged();

        void heightChanged();

        void weightChanged();

        void genderChanged();

        void musclePercentChanged();

        void fatPercentChanged();

        void foodItemsChanged();
    }

    public static class Adapter implements Listener {

        public void bmiChanged() {
        }

        public void heightChanged() {
        }

        public void weightChanged() {
        }

        public void genderChanged() {
        }

        public void musclePercentChanged() {
        }

        public void fatPercentChanged() {
        }

        public void foodItemsChanged() {
        }
    }


    public void addListener( Listener listener ) {
        listeners.add( listener );
    }


    public void addFoodItem( FoodItem foodItem ) {

        HashSet orig = new HashSet( foods );
        foods.add( foodItem );
        if ( !orig.equals( foods ) ) {
            System.out.println( "added foodItem = " + foodItem );
            notifyFoodItemsChanged();
        }
    }

    private void notifyFoodItemsChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.foodItemsChanged();
        }
    }

    public void removeFoodItem( FoodItem foodItem ) {

        HashSet orig = new HashSet( foods );
        foods.remove( foodItem );
        if ( !orig.equals( foods ) ) {System.out.println( "removed foodItem = " + foodItem );
            notifyFoodItemsChanged();
        }
    }

}
