// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.util;

import java.awt.geom.Point2D;
import java.util.List;

import edu.umd.cs.piccolo.PNode;

/**
 * A collection of utilities that are useful for laying out PNodes.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PNodeLayoutUtils {
    
    /* not intended for instantiation */
    private PNodeLayoutUtils() {}
    
    /**
     * Determines how far a node's offset is from the origin of its bounding box.
     * This is useful for setting layouts.
     * 
     * @param node
     * @return
     */
    public static Point2D getOriginOffset( PNode node ) {
        return new Point2D.Double( getOriginXOffset( node ), getOriginYOffset( node ) );
    }
    
    public static double getOriginXOffset( PNode node ) {
        return node.getFullBoundsReference().getX() - node.getXOffset();
    }
    
    public static double getOriginYOffset( PNode node ) {
        return node.getFullBoundsReference().getY() - node.getYOffset();
    }
    
    /**
     * Gets the max full width of a node's children.
     * @param node
     * @return
     */
    public static double getMaxFullWidthChildren( PNode node ) {
        return getMaxFullWidth( node.getChildrenReference() );
    }
    
    /**
     * Get the max full width in a list of nodes.
     * @param nodeList
     * @return
     */
    public static double getMaxFullWidth( List<PNode> nodeList ) {
        double max = 0;
        for ( PNode node : nodeList ) {
            double width = node.getFullBoundsReference().getWidth();
            if ( width > max ) {
                max = width;
            }
        }
        return max;
    }
    
    /**
     * Gets the max full height of a node's children.
     * @param node
     * @return
     */
    public static double getMaxFullHeightChildren( PNode node ) {
        return getMaxFullHeight( node.getChildrenReference() );
    }

    /**
     * Get the max full height in a list of nodes.
     * @param nodeList
     * @return
     */
    public static double getMaxFullHeight( List<PNode> nodeList ) {
        double max = 0;
        for ( PNode node : nodeList ) {
            double height = node.getFullBoundsReference().getHeight();
            if ( height > max ) {
                max = height;
            }
        }
        return max;
    }
    
    /**
     * Sums the full widths of a node's children.
     * @param node
     * @return
     */
    public static double sumFullWidthsChildren( PNode node ) {
        return sumFullWidths( node.getChildrenReference() );
    }
    
    /**
     * Sums the full widths of a list of nodes.
     * @param nodeList
     * @return
     */
    public static double sumFullWidths( List<PNode> nodeList ) {
        double sum = 0;
        for ( PNode node : nodeList ) {
            sum += node.getFullBoundsReference().getWidth();
        }
        return sum;
    }
    
    /**
     * Sums the full heights of a node's children.
     * @param node
     * @return
     */
    public static double sumFullHeightsChildren( PNode node ) {
        return sumFullHeights( node.getChildrenReference() );
    }
    
    /**
     * Sums the full heights of a list of nodes.
     * @param nodeList
     * @return
     */
    public static double sumFullHeights( List<PNode> nodeList ) {
        double sum = 0;
        for ( PNode node : nodeList ) {
            sum += node.getFullBoundsReference().getHeight();
        }
        return sum;
    }

}
