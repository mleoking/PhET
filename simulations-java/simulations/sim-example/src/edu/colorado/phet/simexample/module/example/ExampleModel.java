/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.simexample.module.example;

import edu.colorado.phet.simexample.defaults.ExampleDefaults;
import edu.colorado.phet.simexample.model.ExampleModelElement;
import edu.colorado.phet.simexample.model.SimExampleClock;

/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final SimExampleClock clock;
    private final ExampleModelElement exampleModelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModel( SimExampleClock clock ) {
        super();
        
        this.clock = clock;
        
        exampleModelElement = new ExampleModelElement( 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_WIDTH,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_HEIGHT,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION, 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        this.clock.addClockListener( exampleModelElement );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public SimExampleClock getClock() {
        return clock;
    }
    
    public ExampleModelElement getExampleModelElement() {
        return exampleModelElement;
    }
}
