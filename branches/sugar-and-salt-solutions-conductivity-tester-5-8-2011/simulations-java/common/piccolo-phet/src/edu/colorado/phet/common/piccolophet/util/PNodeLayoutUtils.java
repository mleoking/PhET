// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.util;

import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;

/**
 * A collection of utilities that are useful for laying out PNodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PNodeLayoutUtils {

    /* not intended for instantiation */
    private PNodeLayoutUtils() {
    }

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
     *
     * @param node
     * @return
     */
    public static double getMaxFullWidthChildren( PNode node ) {
        return getMaxFullWidth( node.getChildrenReference() );
    }

    /**
     * Get the max full width in a list of nodes.
     *
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
     *
     * @param node
     * @return
     */
    public static double getMaxFullHeightChildren( PNode node ) {
        return getMaxFullHeight( node.getChildrenReference() );
    }

    /**
     * Get the max full height in a list of nodes.
     *
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
     *
     * @param node
     * @return
     */
    public static double sumFullWidthsChildren( PNode node ) {
        return sumFullWidths( node.getChildrenReference() );
    }

    /**
     * Sums the full widths of a list of nodes.
     *
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
     *
     * @param node
     * @return
     */
    public static double sumFullHeightsChildren( PNode node ) {
        return sumFullHeights( node.getChildrenReference() );
    }

    /**
     * Sums the full heights of a list of nodes.
     *
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

    /**
     * Aligns node2 inside of node1.
     * Accounts for possible origin offsets in both nodes.
     *
     * @param node1
     * @param node2
     * @param verticalAlignment   SwingConstants.TOP, CENTER or BOTTOM
     * @param horizontalAlignment SwingConstants.LEFT, CENTER or RIGHT
     * @param xMargin
     * @param yMargin
     * @throws IllegalArgumentException
     */
    public static void alignInside( PNode node1, PNode node2, int verticalAlignment, int horizontalAlignment, double xMargin, double yMargin ) {

        double xOffset = 0;
        switch( horizontalAlignment ) {
            case SwingConstants.LEFT:
                xOffset = node2.getFullBoundsReference().getMinX() + xMargin;
                break;
            case SwingConstants.CENTER:
                xOffset = node2.getFullBoundsReference().getCenterX() - ( node1.getFullBoundsReference().getWidth() / 2 );
                break;
            case SwingConstants.RIGHT:
                xOffset = node2.getFullBoundsReference().getMaxX() - node1.getFullBoundsReference().getWidth() - xMargin;
                break;
            default:
                throw new IllegalArgumentException( "illegal value for horizontalAlignment: " + horizontalAlignment );
        }

        double yOffset = 0;
        switch( verticalAlignment ) {
            case SwingConstants.TOP:
                yOffset = node2.getFullBoundsReference().getMinY() + yMargin;
                break;
            case SwingConstants.CENTER:
                yOffset = node2.getFullBoundsReference().getCenterY() - ( node1.getFullBoundsReference().getHeight() / 2 );
                break;
            case SwingConstants.BOTTOM:
                yOffset = node2.getFullBoundsReference().getMaxY() - node1.getFullBoundsReference().getHeight() - yMargin;
                break;
            default:
                throw new IllegalArgumentException( "illegal value for verticalAlignment: " + verticalAlignment );
        }

        node1.setOffset( xOffset - getOriginXOffset( node1 ), yOffset - getOriginYOffset( node1 ) );
    }

    public static void alignInside( PNode node1, PNode node2, int verticalAlignment, int horizontalAlignment ) {
        alignInside( node1, node2, verticalAlignment, horizontalAlignment, 0, 0 );
    }
}
