/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Valley;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ValleyNode extends PComposite {

    private Valley _valley;
    private ModelViewTransform _mvt;
    
    public ValleyNode( Valley valley, ModelViewTransform mvt ) {
        super();
        
        _valley = valley;
        _mvt = mvt;
        
        PImage imageNode = new PImage( GlaciersImages.VALLEY );
        addChild( imageNode );
        setPickable( false );
    }
    
    public void cleanup() {}
}
