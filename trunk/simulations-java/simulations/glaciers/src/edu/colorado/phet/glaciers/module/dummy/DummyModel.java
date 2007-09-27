/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.dummy;

import edu.colorado.phet.glaciers.defaults.DummyDefaults;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.ModelViewTransform;
import edu.colorado.phet.glaciers.module.GlaciersAbstractModel;

/**
 * DummyModel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DummyModel extends GlaciersAbstractModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DummyModel( GlaciersClock clock ) {
        super( clock );
        
         _modelViewTransform = new ModelViewTransform( DummyDefaults.MODEL_TO_VIEW_SCALE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
}
