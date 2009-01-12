/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.defaults.SolutionsDefaults;
import edu.colorado.phet.acidbasesolutions.model.ExampleModelElement;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;

/**
 * SolutionsModel is the model for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ABSClock _clock;
    private final ExampleModelElement _exampleModelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsModel( ABSClock clock ) {
        super();
        
        _clock = clock;
        
        _exampleModelElement = new ExampleModelElement( 
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_WIDTH,
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_HEIGHT,
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_POSITION, 
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        _clock.addClockListener( _exampleModelElement );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ABSClock getClock() {
        return _clock;
    }
    
    public ExampleModelElement getExampleModelElement() {
        return _exampleModelElement;
    }
}
