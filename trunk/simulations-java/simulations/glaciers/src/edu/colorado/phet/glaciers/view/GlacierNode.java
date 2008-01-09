/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.model.Glacier;
import edu.umd.cs.piccolox.nodes.PComposite;


public class GlacierNode extends PComposite {

    private Glacier _glacier;
    private ModelViewTransform _mvt;
    
    public GlacierNode( Glacier glacier, ModelViewTransform mvt ) {
        super();
        
        _glacier = glacier;
        _mvt = mvt;
        
        //XXX
    }
}
