package edu.colorado.phet.fitness.model;

import java.util.ArrayList;

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
    }


    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

}
