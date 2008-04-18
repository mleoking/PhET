package edu.colorado.phet.fitness.model;

import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:05:20 PM
 */
public class Human {

    private Gender gender = Gender.MALE;
    private String name = "Larry";
    private ArrayList listeners = new ArrayList();

    private DefaultTemporalVariable height = new DefaultTemporalVariable( 1.5 );//meters
    private DefaultTemporalVariable weight = new DefaultTemporalVariable( 75 );//kg
    private DefaultTemporalVariable age = new DefaultTemporalVariable( 20 * 525600.0 * 60 );//sec
    private DefaultTemporalVariable leanMuscleMass = new DefaultTemporalVariable( 60 );//kg

    private DefaultTemporalVariable lipids = new DefaultTemporalVariable( 100 );
    private DefaultTemporalVariable carbs = new DefaultTemporalVariable( 100 );
    private DefaultTemporalVariable proteins = new DefaultTemporalVariable( 100 );

    private DefaultTemporalVariable bmr = new DefaultTemporalVariable( 200 );
    private DefaultTemporalVariable activity = new DefaultTemporalVariable( 75 );
    private DefaultTemporalVariable exercise = new DefaultTemporalVariable( 50 );

    public Human() {
        addListener( new Adapter() {
            public void heightChanged() {
                updateBMR();
            }

            public void weightChanged() {
                updateBMR();
            }

            public void ageChanged() {
                updateBMR();
            }

            public void genderChanged() {
                updateBMR();
            }
        } );
    }

    private void updateBMR() {
        bmr.setValue( BasalMetabolicRate.getBasalMetabolicRateHarrisBenedict( getWeight(), getHeight(), getAge(), gender ) );
        System.out.println( "bmr.getValue() = " + bmr.getValue() );
    }

//    public Human( double age, double height, double weight, Gender gender, String name ) {
//        this.age = new ;
//        this.height = height;
//        this.weight = weight;
//        this.gender = gender;
//        this.name = name;
//    }

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

    public double getLeanMuscleMass() {
        return leanMuscleMass.getValue();
    }

//    public double getFatPercent() {
//        return 100 - leanMuscleMass.;
//    }

    public void setLeanMuscleMass( double value ) {
        this.leanMuscleMass.setValue( value );
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

//    public void setFatPercent( double value ) {
//        this.leanMuscleMass = 100 - value;
//        notifyFatPercentChanged();
//        notifyMusclePercentChanged();
//    }

    public DefaultTemporalVariable getLipids() {
        return lipids;
    }

    public DefaultTemporalVariable getCarbs() {
        return carbs;
    }

    public DefaultTemporalVariable getProteins() {
        return proteins;
    }

    public DefaultTemporalVariable getBmr() {
        return bmr;
    }

    public DefaultTemporalVariable getActivity() {
        return activity;
    }

    public DefaultTemporalVariable getExercise() {
        return exercise;
    }

    public void simulationTimeChanged( double simulationTimeChange ) {
        setAge( getAge() + simulationTimeChange );
        double caloriesGained = getDailyCaloricIntake() - getDailyCaloricExpense();
        double kgGained = FitnessUnits.caloriesToKG( caloriesGained );
        setWeight( getWeight() + kgGained );
    }

    private double getDailyCaloricExpense() {
        return bmr.getValue() + activity.getValue() + exercise.getValue();
    }

    private double getDailyCaloricIntake() {
        return lipids.getValue() + proteins.getValue() + carbs.getValue();
    }
    //    public double getDailyCaloricIntake() {
//        double sum = 0;
//        for ( Iterator iterator = foods.iterator(); iterator.hasNext(); ) {
//            FoodItem foodItem = (FoodItem) iterator.next();
//            sum += foodItem.getCalories();
//        }
//        return sum;
//    }

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
        return age.getValue();
    }

    public void setAge( double age ) {
        this.age.setValue( age );
        notifyAgeChanged();
    }

    private void notifyAgeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.ageChanged();
        }
    }

    public double getHeight() {
        return height.getValue();
    }

    public void setHeight( double height ) {
        this.height.setValue( height );
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
        return weight.getValue();
    }

    public void setWeight( double weight ) {
        this.weight.setValue( weight );
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

        void ageChanged();
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

        public void ageChanged() {
        }
    }


    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

//    public void addFoodItem( FoodItem foodItem ) {
//
//        HashSet orig = new HashSet( foods );
//        foods.add( foodItem );
//        if ( !orig.equals( foods ) ) {
//            System.out.println( "added foodItem = " + foodItem );
//            notifyFoodItemsChanged();
//        }
//    }
//
//    private void notifyFoodItemsChanged() {
//        for ( int i = 0; i < listeners.size(); i++ ) {
//            Listener listener = (Listener) listeners.get( i );
//            listener.foodItemsChanged();
//        }
//    }
//
//    public void removeFoodItem( FoodItem foodItem ) {
//
//        HashSet orig = new HashSet( foods );
//        foods.remove( foodItem );
//        if ( !orig.equals( foods ) ) {
//            System.out.println( "removed foodItem = " + foodItem );
//            notifyFoodItemsChanged();
//        }
//    }

}
