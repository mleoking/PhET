/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view.sprites;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Tree;
import edu.colorado.phet.naturalselection.view.LandscapeNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node to display a tree
 *
 * @author Jonathan Olson
 */
public class TreeNode extends NaturalSelectionSprite implements Rescalable {

    private final double baseScale;

    private PImage treeImage;

    /**
     * Constructor. Creates a tree and the specified canvas location and scale, with the correct depth for bunnies
     * to be in front and behind the tree.
     *
     * @param landscapeNode Spritesnode
     * @param tree          The model tree
     */
    public TreeNode( LandscapeNode landscapeNode, Tree tree ) {
        super( landscapeNode, tree.getPosition() );

        this.baseScale = tree.getBaseScale();

        // load the image
        treeImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_TREE );

        // offset the image so that the visual base of the tree is where the base of the image would be
        Point2D offset = NaturalSelectionConstants.IMAGE_TREE_OFFSET;
        treeImage.setOffset( offset.getX() - treeImage.getWidth() / 2, offset.getY() - treeImage.getHeight() );

        addChild( treeImage );

        rescale();
    }

    public void rescale() {
        double scale = baseScale * landscapeNode.getSpriteTransform().getScaleY();

        setScale( scale );
    }
}
