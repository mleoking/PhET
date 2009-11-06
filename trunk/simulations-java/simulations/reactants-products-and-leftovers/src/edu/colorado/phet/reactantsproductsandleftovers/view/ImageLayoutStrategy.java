package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Interface for specifying how images are arranged in box.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ImageLayoutStrategy {
    
    /**
     * Gets the offset of node relative to referenceNode inside the bounds of boxNode.
     * @param node
     * @param referenceNode
     * @param boxNode
     * @return
     */
    public Point2D getOffset( PNode node, PNode referenceNode, PNode boxNode );
    
    /**
     * Stacks images vertically in the box.
     */
    public static class StackedLayoutStrategy implements ImageLayoutStrategy {

        private static final double Y_SPACING = 27;

        public Point2D getOffset( PNode node, PNode referenceNode, PNode boxNode ) {
            double x = -node.getFullBoundsReference().getWidth() / 2;
            double y = -PNodeLayoutUtils.getOriginYOffset( node ) - Y_SPACING;
            if ( referenceNode != null ) {
                y += referenceNode.getFullBoundsReference().getMinY();
            }
            return new Point2D.Double( x, y );
        }
    }
    
    /**
     * Randomly places images in a box.
     */
    public static class RandomBoxLayoutStrategy implements ImageLayoutStrategy {
        
        public Point2D getOffset( PNode node, PNode referenceNode, PNode boxNode ) {
            PBounds b = boxNode.getFullBoundsReference();
            double x =  b.getX() + ( Math.random() * b.getWidth() );
            double y = b.getY() + ( Math.random() * b.getHeight() );
            return new Point2D.Double( x, y );
        }
    }
}