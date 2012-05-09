// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Image;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A node that is created for a specific width by horizontally tiling a set of images.
 * The left and right images can be thought of as "book ends", with the center image tiled to fill the space in the middle.
 * This allows us to create (for example) control panels that have 3D-looking backgrounds, but can adjust to fit i18n.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HorizontalTiledNode extends PImage {

    private static final double X_OVERLAP = 1; // overlap between tiles, to hide seams

    public HorizontalTiledNode( double totalWidth, Image leftImage, Image centerImage, Image rightImage ) {

        PImage leftNode = new PImage( leftImage );
        PImage centerNode = new PImage( centerImage );
        PImage rightNode = new PImage( rightImage );

        if ( leftNode.getHeight() != centerNode.getHeight() || centerNode.getHeight() != rightNode.getHeight() ) {
            throw new IllegalArgumentException( "all images must have the same height" );
        }

        // compute the number of tiles required to fill the center
        double leftWidth = leftNode.getFullBoundsReference().getWidth();
        double centerWidth = centerNode.getFullBoundsReference().getWidth();
        double rightWidth = rightNode.getFullBoundsReference().getWidth();
        if ( centerNode.getWidth() > ( leftWidth + rightWidth + ( 2 * X_OVERLAP ) ) ) {
            throw new IllegalArgumentException( "center image is too wide, it will overlap other images" );
        }
        double tiledWidth = totalWidth - leftWidth - rightWidth;

        // parent all nodes to this node, which will later be converted to an image
        PNode parentNode = new PNode();

        // left
        parentNode.addChild( leftNode );

        // right
        parentNode.addChild( rightNode );
        rightNode.setOffset( totalWidth - rightWidth, 0 );

        // tile the center, with overlap between tiles to hide seams
        PNode previousNode = leftNode;
        while ( tiledWidth > 0 ) {
            PImage tileNode = new PImage( centerImage );
            parentNode.addChild( tileNode );
            tileNode.setOffset( previousNode.getFullBoundsReference().getMaxX() - X_OVERLAP, 0 );
            // If tile extends too far into right side, shift the tile to the left.
            if ( tileNode.getFullBoundsReference().getMaxX() > rightNode.getFullBoundsReference().getMinX() + X_OVERLAP ) {
                tileNode.setOffset( rightNode.getFullBoundsReference().getMinX() + X_OVERLAP - tileNode.getFullBoundsReference().getWidth(), 0 );
            }
            tiledWidth = tiledWidth - centerWidth + X_OVERLAP;
            previousNode = tileNode;
        }

        // convert to image
        setImage( parentNode.toImage() );
    }
}
