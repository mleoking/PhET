package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;

/**
 * Interface for specifying how images are arranged in box.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ImageLayoutStrategy {
    
    public Point2D getOffset( PNode referenceNode, PNode layoutNode );
    
    /**
     * Stacks images vertically in the box.
     */
    public static class StackedLayoutStrategy implements ImageLayoutStrategy {

        private static final double Y_SPACING = 27;

        public Point2D getOffset( PNode node, PNode referenceNode ) {
            double x = -node.getFullBoundsReference().getWidth() / 2;
            double y = -PNodeLayoutUtils.getOriginYOffset( node ) - Y_SPACING;
            if ( referenceNode != null ) {
                y += referenceNode.getFullBoundsReference().getMinY();
            }
            return new Point2D.Double( x, y );
        }
    }
}