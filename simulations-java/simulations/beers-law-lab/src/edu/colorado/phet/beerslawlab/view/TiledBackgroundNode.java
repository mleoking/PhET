// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Image;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

//TODO this implementation is not quite correct yet. There may be cases where seams are visible, and the final center tile might extend past the right tile.

/**
 * A background image that is created for a specific width by tiling a set of images.
 * The left and right images can be thought of as "book ends", with the center image tiled to fill the space in the middle.
 * This allows us to create (for example) control panels that have 3D-looking backgrounds, but can adjust to fit i18n.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TiledBackgroundNode extends PImage {

    public TiledBackgroundNode( double width, Image leftImage, Image centerImage, Image rightImage ) {

        PImage leftNode = new PImage( leftImage );
        PImage centerNode = new PImage( centerImage );
        PImage rightNode = new PImage( rightImage );
        assert ( leftNode.getHeight() == centerNode.getHeight() && centerNode.getHeight() == rightNode.getHeight() ); // all images have the same height

        // compute the number of tiles required to fill the center
        double leftWidth = leftNode.getFullBoundsReference().getWidth();
        double centerWidth = leftNode.getFullBoundsReference().getWidth();
        double rightWidth = leftNode.getFullBoundsReference().getWidth();
        double tiledWidth = width - leftWidth - rightWidth + 2; // +2 for overlap
        int numberOfTiles = (int) ( tiledWidth + 1 ) / (int) ( centerWidth + 1 );

        PNode parentNode = new PNode();

        // left
        parentNode.addChild( leftNode );

        // tiled center
        PNode previousNode = leftNode;
        for ( int i = 0; i < numberOfTiles; i++ ) {
            PImage tileNode = new PImage( centerImage );
            parentNode.addChild( tileNode );
            tileNode.setOffset( previousNode.getFullBoundsReference().getMaxX() - 1, previousNode.getYOffset() );
            previousNode = tileNode;
        }

        // right
        parentNode.addChild( rightNode );
        rightNode.setOffset( previousNode.getFullBoundsReference().getMaxX() - 1, previousNode.getYOffset() );

        // convert to image
        setImage( parentNode.toImage() );
    }
}
