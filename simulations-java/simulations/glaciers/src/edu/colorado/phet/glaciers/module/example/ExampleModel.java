/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import edu.colorado.phet.glaciers.defaults.ExampleDefaults;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.ModelViewTransform;
import edu.colorado.phet.glaciers.module.GlaciersAbstractModel;

/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModel extends GlaciersAbstractModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ExampleModelElement _exampleModelElement;
    private final ModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModel( GlaciersClock clock ) {
        super( clock );
        
        _exampleModelElement = new ExampleModelElement( 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_SIZE,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION, 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        addModelElement( _exampleModelElement  );
        
         _modelViewTransform = new ModelViewTransform( ExampleDefaults.MODEL_TO_VIEW_SCALE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ExampleModelElement getExampleModelElement() {
        return _exampleModelElement;
    }
    
    public ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
}
