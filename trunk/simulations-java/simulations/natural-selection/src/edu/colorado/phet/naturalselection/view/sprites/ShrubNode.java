/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view.sprites;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Shrub;
import edu.colorado.phet.naturalselection.view.LandscapeNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays a shrub in piccolo
 *
 * @author Jonathan Olson
 */
public class ShrubNode extends NaturalSelectionSprite implements Rescalable {

    private final double baseScale;

    private PImage shrubImage;

    private Shrub shrub;

    private LandscapeNode landscapeNode;

    /**
     * Constructor. Creates a shrub at the specified canvas location with the specified scale, with the correct depth
     * so that bunnies display correctly in front and behind the shrub
     *
     * @param landscapeNode SpritesNode
     * @param shrub         The corresponding shrub to view
     */
    public ShrubNode( LandscapeNode landscapeNode, Shrub shrub ) {
        super( landscapeNode, shrub.getPosition() );

        this.landscapeNode = landscapeNode;
        this.shrub = shrub;

        baseScale = shrub.getBaseScale();

        // load the image
        shrubImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_SHRUB );

        // offset the shrub image so it looks like the base is where the bottom of the image was before
        Point2D offset = NaturalSelectionConstants.IMAGE_SHRUB_OFFSET;
        shrubImage.setOffset( offset.getX() - shrubImage.getWidth() / 2, offset.getY() - shrubImage.getHeight() );

        addChild( shrubImage );

        rescale();

    }

    public void rescale() {
        double scale = baseScale * landscapeNode.getSpriteTransform().getScaleY();

        setScale( scale );
    }
}