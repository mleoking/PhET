/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.fitness.control.FoodItem;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.model.SimTemplateClock;

/**
 * FitnessModel is the model for FitnessModule.
 */
public class FitnessModel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final SimTemplateClock _clock;
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

    public FitnessModel( SimTemplateClock clock ) {
        super();

        _clock = clock;

//        _clock.addClockListener( _fitnessModelElement );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public SimTemplateClock getClock() {
        return _clock;
    }

    public FoodItem[] getFoodItems() {
        return availableFoods;
    }

    public Human getHuman() {
        return human;
    }

}