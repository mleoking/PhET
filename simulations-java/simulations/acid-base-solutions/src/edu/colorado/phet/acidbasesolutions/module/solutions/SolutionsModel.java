/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.ExampleModelElement;
import edu.colorado.phet.acidbasesolutions.model.Solution;
import edu.colorado.phet.acidbasesolutions.module.ABSModel;

/**
 * SolutionsModel is the model for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsModel extends ABSModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ExampleModelElement _exampleModelElement;//XXX delete
    private final Solution _solution;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsModel( ABSClock clock ) {
        super( clock );
        
        _exampleModelElement = new ExampleModelElement( 
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_WIDTH,
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_HEIGHT,
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_POSITION, 
                SolutionsDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        addClockListener( _exampleModelElement );
        
        _solution = new Solution();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    //XXX delete
    public ExampleModelElement getExampleModelElement() {
        return _exampleModelElement;
    }
    
    public Solution getSolution() {
        return _solution;
    }
}
