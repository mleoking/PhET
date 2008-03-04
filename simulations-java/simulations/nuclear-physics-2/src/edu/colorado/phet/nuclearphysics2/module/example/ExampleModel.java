/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.example;

import edu.colorado.phet.nuclearphysics2.defaults.ExampleDefaults;
import edu.colorado.phet.nuclearphysics2.model.ExampleModelElement;
import edu.colorado.phet.nuclearphysics2.model.NulcearPhysics2Clock;

/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final NulcearPhysics2Clock _clock;
    private final ExampleModelElement _exampleModelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModel( NulcearPhysics2Clock clock ) {
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
    
    public NulcearPhysics2Clock getClock() {
        return _clock;
    }
    
    public ExampleModelElement getExampleModelElement() {
        return _exampleModelElement;
    }
}
