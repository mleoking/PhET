/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.BoreholeDrill;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * BoreholeDrillNode is the visual representation of a borehole drill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoreholeDrillNode extends AbstractToolNode {

    public BoreholeDrillNode( BoreholeDrill boreholeDrill, ModelViewTransform mvt ) {
        super( boreholeDrill, mvt );
        PImage imageNode = new PImage( GlaciersImages.BOREHOLE_DRILL );
        addChild( imageNode );
        imageNode.setOffset( 0, -imageNode.getFullBoundsReference().getHeight() ); // lower left
    }
    
    public void cleanup() {
        super.cleanup();
    }
}
