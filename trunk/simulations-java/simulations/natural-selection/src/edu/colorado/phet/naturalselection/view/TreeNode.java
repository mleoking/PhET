/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node to display a tree
 *
 * @author Jonathan Olson
 */
public class TreeNode extends NaturalSelectionSprite {

    private final double backgroundX;
    private final double backgroundY;
    private final double baseScale;

    private PImage treeImage;

    /**
     * Constructor. Creates a tree and the specified canvas location and scale, with the correct depth for bunnies
     * to be in front and behind the tree.
     *
     * @param spriteHandler Spritesnode
     * @param backgroundX   Canvas x-position (center of tree)
     * @param backgroundY   Canvas y-position (bottom of tree)
     * @param baseScale     Scale
     */
    public TreeNode( SpriteHandler spriteHandler, double backgroundX, double backgroundY, double baseScale ) {
        super( spriteHandler );

        this.backgroundX = backgroundX;
        this.backgroundY = backgroundY;
        this.baseScale = baseScale;

        // load the image
        treeImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_TREE );

        // offset the image so that the visual base of the tree is where the base of the image would be
        Point2D offset = NaturalSelectionConstants.IMAGE_TREE_OFFSET;
        treeImage.setOffset( offset.getX(), offset.getY() );

        addChild( treeImage );

        reposition();
    }

    public void reposition() {


        setSpriteLocation( backgroundX, 0, getInverseGroundZDepth( backgroundY ) );

        double scale = baseScale * spriteHandler.getSpriteTransform().getScaleY();

        setScale( scale );

        Point2D location = new Point2D.Double( backgroundX, backgroundY );

        spriteHandler.getSpriteTransform().transform( location, location );

        setOffset( location.getX() - scale * treeImage.getWidth() / 2, location.getY() - treeImage.getHeight() * scale );

    }
}
