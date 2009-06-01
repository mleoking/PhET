/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Shrub;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays a shrub in piccolo
 *
 * @author Jonathan Olson
 */
public class ShrubNode extends NaturalSelectionSprite {

    private final double backgroundX;
    private final double backgroundY;
    private final double baseScale;

    private PImage shrubImage;

    private Shrub shrub;

    /**
     * Constructor. Creates a shrub at the specified canvas location with the specified scale, with the correct depth
     * so that bunnies display correctly in front and behind the shrub
     *
     * @param spriteHandler SpritesNode
     * @param shrub         The corresponding shrub to view
     */
    public ShrubNode( SpriteHandler spriteHandler, Shrub shrub ) {
        super( spriteHandler );

        this.shrub = shrub;

        backgroundX = shrub.getBackgroundX();
        backgroundY = shrub.getBackgroundY();
        baseScale = shrub.getBaseScale();

        // load the image
        shrubImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_SHRUB );

        // offset the shrub image so it looks like the base is where the bottom of the image was before
        Point2D offset = NaturalSelectionConstants.IMAGE_SHRUB_OFFSET;
        shrubImage.setOffset( offset.getX(), offset.getY() );

        addChild( shrubImage );

        reposition();

    }

    public void reposition() {


        setSpriteLocation( backgroundX, 0, getInverseGroundZDepth( backgroundY ) );

        double scale = baseScale * spriteHandler.getSpriteTransform().getScaleY();

        setScale( scale );

        Point2D location = new Point2D.Double( backgroundX, backgroundY );

        spriteHandler.getSpriteTransform().transform( location, location );

        setOffset( location.getX() - scale * shrubImage.getWidth() / 2, location.getY() - shrubImage.getHeight() * scale );

    }
}