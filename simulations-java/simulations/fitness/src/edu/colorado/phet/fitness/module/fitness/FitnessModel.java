/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.fitness.control.CaloricItem;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.model.Diet;
import edu.colorado.phet.fitness.model.Human;

/**
 * FitnessModel is the model for FitnessModule.
 */
public class FitnessModel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final ConstantDtClock _clock;
    private final Human human = new Human();

    //http://www.calorie-count.com/calories/item/9316.html
    public static final FoodCalorieSet availableFoods = new FoodCalorieSet( new CaloricFoodItem[]{
            new CaloricFoodItem( "hamburger", "burger.png", 279, 13.5, 27.3, 12.9 ),
            new CaloricFoodItem( "cup strawberries", "strawberry.png", 49, 0.5, 11.7, 1.0 ),//per cup
            new CaloricFoodItem( "banana split", "bananasplit.png", 894, 43, 121, 15 ),
            new CaloricFoodItem( "cup grapefruit", "grapefruit.png", 97, 0.3, 24.5, 1.8 ),//per cup
    } );

    //values taken from http://www.hpathy.com/healthtools/calories-need.asp
    public static final Diet BALANCED_DIET = new Diet( "Balanced Diet", 870, 1583, 432 );
    public static final Diet FAST_FOOD_ONLY = new Diet( "Fast Food Only", 3000, 300, 150 );
    public static final Diet[] availableDiets = new Diet[]{
            BALANCED_DIET,
            FAST_FOOD_ONLY
    };
    public static final CalorieSet availableExercise = new CalorieSet( new CaloricItem[]{
            //http://www.nutristrategy.com/activitylist.htm
            new CaloricItem( "hour swimming laps", "swim.png", 590 ),//todo: make a function of weight
            new CaloricItem( "hour basketball", "basketball.png", 472 ),
            new CaloricItem( "hour bowling", "bowling.png", 177 ),
            new CaloricItem( "hour dancing", "dancing.png", 266 ),
    } );
    private boolean paused = false;
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessModel( ConstantDtClock clock ) {
        super();

        _clock = clock;

        _clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if ( !paused ) {
                    human.simulationTimeChanged( clockEvent.getSimulationTimeChange() );
                }
            }
        } );
    }

    public Diet[] getAvailableDiets() {
        return availableDiets;
    }
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return _clock;
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
        return new Diet( "User Specified", lipids, carbs, proteins );
    }

    //Todo: remove this workaround for performance/graphics problems
    public void setPaused( boolean paused ) {
        this.paused = paused;
    }
}