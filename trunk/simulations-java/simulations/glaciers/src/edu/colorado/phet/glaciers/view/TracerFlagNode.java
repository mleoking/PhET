/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.TracerFlag;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * TracerFlagNode is the visual representation of a tracer flag.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TracerFlagNode extends AbstractToolNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TracerFlagNode( TracerFlag tracerFlag, ModelViewTransform mvt ) {
        super( tracerFlag, mvt );
        PImage imageNode = new PImage( GlaciersImages.TRACER_FLAG );
        addChild( imageNode );
        imageNode.setOffset( 0, -imageNode.getFullBoundsReference().getHeight() ); // lower left corner
    }
    
    public void cleanup() {
        super.cleanup();
    }
}
