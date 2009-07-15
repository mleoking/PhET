/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.example;

import edu.colorado.phet.titration.defaults.ExampleDefaults;
import edu.colorado.phet.titration.model.ExampleModelElement;
import edu.colorado.phet.titration.model.SimTemplateClock;

/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final SimTemplateClock clock;
    private final ExampleModelElement exampleModelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModel( SimTemplateClock clock ) {
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
    
    public SimTemplateClock getClock() {
        return clock;
    }
    
    public ExampleModelElement getExampleModelElement() {
        return exampleModelElement;
    }
}
