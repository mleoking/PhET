/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.simtemplate.module.example;

import edu.colorado.phet.simtemplate.defaults.ExampleDefaults;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.SimTemplateClock;

/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final SimTemplateClock _clock;
    private final ExampleModelElement _exampleModelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModel( SimTemplateClock clock ) {
        super();
        
        _clock = clock;
        
        _exampleModelElement = new ExampleModelElement( 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_WIDTH,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_HEIGHT,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION, 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        _clock.addClockListener( _exampleModelElement );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public SimTemplateClock getClock() {
        return _clock;
    }
    
    public ExampleModelElement getExampleModelElement() {
        return _exampleModelElement;
    }
}
