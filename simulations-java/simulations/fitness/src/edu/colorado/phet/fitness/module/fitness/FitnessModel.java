/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.fitness.control.FoodItem;
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
    private FoodItem[] availableFoods = new FoodItem[]{
            new FoodItem( "burger.png", 279 ),
            new FoodItem( "strawberry.png", 28 ),//per cup
            new FoodItem( "bananasplit.png", 510 ),
            new FoodItem( "grapefruit.png", 74 ),//per cup
    };

    //values taken from http://www.hpathy.com/healthtools/calories-need.asp
    public static final Diet BALANCED_DIET = new Diet( "Balanced Diet", 870, 1583, 432 );
    public static final Diet FAST_FOOD_ONLY = new Diet( "Fast Food Only", 3000, 300, 150 );
    public static final Diet[] availableDiets = new Diet[]{
            BALANCED_DIET,
            FAST_FOOD_ONLY
    };

    public static final Exercise[] availableExercise = new Exercise[]{
            //http://www.nutristrategy.com/activitylist.htm
            new Exercise( "Swimming Laps (1 hour/day)", 590 ),//todo: make a function of weight
            new Exercise( "Basketball (1 hour/day)", 472 ),
            new Exercise( "Bowling (1 hour/day)", 177 ),
            new Exercise( "Dancing (1 hour/day)", 266 ),
    };
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

    public FoodItem[] getFoodItems() {
        return availableFoods;
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