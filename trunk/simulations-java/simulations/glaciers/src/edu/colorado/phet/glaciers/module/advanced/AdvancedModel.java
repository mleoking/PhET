/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import edu.colorado.phet.glaciers.model.AbstractModel;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.ModelViewTransform;
import edu.colorado.phet.glaciers.model.Valley;

/**
 * AdvancedModel is the model for AdvancedModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedModel extends AbstractModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AdvancedModel( GlaciersClock clock, Valley valley, Glacier glacier, Climate climate, ModelViewTransform modelViewTransform ) {
        super( clock, valley, glacier, climate, modelViewTransform );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
}
