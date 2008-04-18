/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.fitness.control.FoodItem;
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
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessModel( ConstantDtClock clock ) {
        super();

        _clock = clock;

        _clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                human.simulationTimeChanged( clockEvent.getSimulationTimeChange() );
            }
        } );
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


}