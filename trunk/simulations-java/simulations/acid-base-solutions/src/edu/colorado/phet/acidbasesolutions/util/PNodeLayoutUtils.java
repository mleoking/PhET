package edu.colorado.phet.acidbasesolutions.util;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;

/**
 * A collection of utilites that are useful for PNodes.
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

}
