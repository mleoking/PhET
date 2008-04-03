/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import edu.colorado.phet.fitness.model.SimTemplateClock;
import edu.colorado.phet.fitness.model.Human;

/**
 * FitnessModel is the model for FitnessModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FitnessModel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final SimTemplateClock _clock;
    private final Human human=new Human( );

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

    public Human getFitnessModelElement() {
        return human;
    }
}