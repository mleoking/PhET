/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.model.Diet;
import edu.colorado.phet.fitness.model.FitnessUnits;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.util.FitnessFileParser;
import edu.colorado.phet.fitness.FitnessResources;

/**
 * FitnessModel is the model for FitnessModule.
 */
public class FitnessModel {

    public static class Units {
        public static final Units ENGLISH = new Units( FitnessResources.getString( "units.english" ), FitnessResources.getString( "units.lbs" ), FitnessResources.getString( "units.feet.in" ) ) {
            public double modelToViewMass( double mass ) {
                return FitnessUnits.kgToPounds( mass );
            }

            public double viewToModelMass( double value ) {
                return FitnessUnits.poundsToKg( value );
            }

            public double modelToViewDistance( double distance ) {
                return FitnessUnits.metersToFeet( distance );
            }

            public double viewToModelDistance( double value ) {
                return FitnessUnits.feetToMeters( value );
            }
        };
        public static final Units METRIC = new Units( FitnessResources.getString( "units.metric" ), FitnessResources.getString( "units.kg" ), FitnessResources.getString( "units.meters" ) );
        private String shortName;
        private String massUnit;
        private String distanceUnit;

        public Units( String shortName, String massUnit, String distanceUnit ) {
            this.shortName = shortName;
            this.massUnit = massUnit;
            this.distanceUnit = distanceUnit;
        }

        public String toString() {
            return shortName;
        }

        public String getShortName() {
            return shortName;
        }

        public double modelToViewMass( double mass ) {
            return mass;
        }

        public String getMassUnit() {
            return massUnit;
        }

        public double viewToModelMass( double value ) {
            return value;
        }

        public String getDistanceUnit() {
            return distanceUnit;
        }

        public double modelToViewDistance( double distance ) {
            return distance;
        }

        public double viewToModelDistance( double value ) {
            return value;
        }
    }

    private Units units = Units.ENGLISH;
    public static final Units[] availableUnits = new Units[]{Units.ENGLISH, Units.METRIC};

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final FitnessClock clock;
    private final Human human = new Human();

    //http://www.calorie-count.com/calories/item/9316.html
    public static final FoodCalorieSet availableFoods = new FoodCalorieSet( FitnessFileParser.getFoodItems() );
    public static final CalorieSet availableExercise = new CalorieSet( FitnessFileParser.getExerciseItems() );

    //values taken from http://www.hpathy.com/healthtools/calories-need.asp
    public static final Diet BALANCED_DIET = new Diet( FitnessResources.getString( "diet.balanced" ), 870, 1583, 432 );
    public static final Diet FAST_FOOD_ONLY = new Diet( FitnessResources.getString( "diet.fast-food" ), 3000, 300, 150 );
    public static final Diet[] availableDiets = new Diet[]{
            BALANCED_DIET,
            FAST_FOOD_ONLY
    };

    private boolean paused = false;
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessModel( FitnessClock clock ) {
        super();

        this.clock = clock;

        this.clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if ( !paused ) {
                    human.simulationTimeChanged( clockEvent.getSimulationTimeChange() );
                    notifySimulationTimeChanged();
                }
            }
        } );
    }

    private void notifySimulationTimeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).simulationTimeChanged();
        }
    }

    public Diet[] getAvailableDiets() {
        return availableDiets;
    }
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public FitnessClock getClock() {
        return clock;
    }

    public CalorieSet getAvailableFoods() {
        return availableFoods;
    }

    public CalorieSet getAvailableExercise() {
        return availableExercise;
    }

    public Human getHuman() {
        return human;
    }

    //returns a named diet from this model, if one exits
    public static Diet getDiet( double lipids, double carbs, double proteins ) {
        for ( int i = 0; i < availableDiets.length; i++ ) {
            Diet availableDiet = availableDiets[i];
            if ( availableDiet.getFat() == lipids && availableDiet.getCarb() == carbs && availableDiet.getProtein() == proteins ) {
                return availableDiet;
            }
        }
        return new Diet( FitnessResources.getString( "diet.user-specified" ), lipids, carbs, proteins );
    }

    //Todo: remove this workaround for performance/graphics problems
    public void setPaused( boolean paused ) {
        this.paused = paused;
    }

    public void resetAll() {
        clock.setSimulationTime( 0.0 );
        human.resetAll();
        setUnits( Units.ENGLISH );
        getClock().pause();
    }

    public Units getUnits() {
        return units;
    }

    public void setUnits( Units units ) {
        if ( this.units != units ) {
            this.units = units;
            notifyUnitsChanged();
        }
    }

    private void notifyUnitsChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).unitsChanged();
        }
    }

    public static interface Listener {
        void unitsChanged();

        void simulationTimeChanged();
    }

    public static class Adapter implements Listener {
        public void unitsChanged() {
        }

        public void simulationTimeChanged() {
        }
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

}