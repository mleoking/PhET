package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Interface for specifying how images are arranged in the Before and After boxes.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ImageLayoutStrategy {
    
    /**
     * Gets the offset of node, relative to referenceNode, inside the bounds of boxNode, with knowledge of a related controlNode.
     * @param node
     * @param referenceNode
     * @param boxNode
     * @param controlNode
     * @return
     */
    public Point2D getOffset( PNode node, PNode referenceNode, PNode boxNode, PNode controlNode );
    
    /**
     * Stacks images vertically in the box.
     */
    public static class StackedLayoutStrategy implements ImageLayoutStrategy {

        private static final double Y_MARGIN = 8;
        private static final double Y_SPACING = 27;

        public Point2D getOffset( PNode node, PNode referenceNode, PNode boxNode, PNode controlNode ) {
            double x = controlNode.getXOffset() - ( node.getFullBoundsReference().getWidth() / 2 );
            double y = boxNode.getFullBoundsReference().getHeight() - node.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( node ) - Y_MARGIN;
            if ( referenceNode != null ) {
                y = referenceNode.getFullBoundsReference().getMinY() - PNodeLayoutUtils.getOriginYOffset( node ) - Y_SPACING;
            }
            return new Point2D.Double( x, y );
        }
    }
    
    /**
     * Randomly places images in a box, images may overlap.
     */
    public static class RandomBoxLayoutStrategy implements ImageLayoutStrategy {
        
        private static final double MARGIN = 5;
        
        public Point2D getOffset( PNode node, PNode referenceNode, PNode boxNode, PNode controlNode ) {
            PBounds b = boxNode.getFullBoundsReference();
            double x = b.getX() + MARGIN + ( Math.random() * ( b.getWidth() - node.getFullBoundsReference().getWidth() - ( 2 * MARGIN ) ) );
            double y = b.getY() + MARGIN + ( Math.random() * ( b.getHeight() - node.getFullBoundsReference().getHeight() - ( 2 * MARGIN ) ) );
            return new Point2D.Double( x, y );
        }
    }
}