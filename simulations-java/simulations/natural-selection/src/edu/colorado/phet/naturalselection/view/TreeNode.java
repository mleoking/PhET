/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node to display a tree
 *
 * @author Jonathan Olson
 */
public class TreeNode extends NaturalSelectionSprite {

    /**
     * Constructor. Creates a tree and the specified canvas location and scale, with the correct depth for bunnies
     * to be in front and behind the tree.
     *
     * @param canvasX Canvas x-position (center of tree)
     * @param canvasY Canvas y-position (bottom of tree)
     * @param scale   Scale
     */
    public TreeNode( double canvasX, double canvasY, double scale ) {
        // load the image
        PImage treeImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_TREE );

        // offset the image so that the visual base of the tree is where the base of the image would be
        Point2D offset = NaturalSelectionConstants.IMAGE_TREE_OFFSET;
        treeImage.setOffset( offset.getX(), offset.getY() );

        addChild( treeImage );
        setSpriteLocation( canvasX, 0, getInverseGroundZDepth( canvasY ) );
        setScale( scale );
        setOffset( canvasX - scale * treeImage.getWidth() / 2, canvasY - treeImage.getHeight() * scale );
    }

}
