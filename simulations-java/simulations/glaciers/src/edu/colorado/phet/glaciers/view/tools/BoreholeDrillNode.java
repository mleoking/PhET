/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.BoreholeDrill;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BoreholeDrillNode is the visual representation of a borehole drill.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoreholeDrillNode extends AbstractToolNode {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BoreholeDrillNode( BoreholeDrill boreholeDrill, ModelViewTransform mvt, TrashCanIconNode trashCanIconNode ) {
        super( boreholeDrill, mvt, trashCanIconNode );
        
        PNode drillNode = new DrillNode();
        addChild( drillNode );
        drillNode.setOffset( 0, -drillNode.getFullBoundsReference().getHeight() ); // lower left
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private static class DrillNode extends PComposite {
        public DrillNode() {
            super();
            PImage imageNode = new PImage( GlaciersImages.BOREHOLE_DRILL );
            addChild( imageNode );
        }
    }
}
