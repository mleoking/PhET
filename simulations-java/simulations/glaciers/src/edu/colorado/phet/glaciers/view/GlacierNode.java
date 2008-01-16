/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.model.Glacier;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * GlacierNode is the visual representation of a glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierNode extends PComposite {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private ModelViewTransform _mvt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacierNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        _glacier = glacier;
        _mvt = mvt;
        
        //XXX
    }
}
