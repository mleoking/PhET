/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays a shrub in piccolo
 *
 * @author Jonathan Olson
 */
public class ShrubNode extends NaturalSelectionSprite {

    /**
     * Constructor. Creates a shrub at the specified canvas location with the specified scale, with the correct depth
     * so that bunnies display correctly in front and behind the shrub
     *
     * @param baseX Canvas X location (center of shrub)
     * @param baseY Canvas y location (bottom of shrub)
     * @param scale Scale
     */
    public ShrubNode( double baseX, double baseY, double scale ) {
        // load the image
        PImage shrubImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_SHRUB );

        // offset the shrub image so it looks like the base is where the bottom of the image was before
        Point2D offset = NaturalSelectionConstants.IMAGE_SHRUB_OFFSET;
        shrubImage.setOffset( offset.getX(), offset.getY() );

        addChild( shrubImage );
        setSpriteLocation( baseX, 0, getInverseGroundZDepth( baseY ) );
        setScale( scale );
        setOffset( baseX - scale * shrubImage.getWidth() / 2, baseY - shrubImage.getHeight() * scale );
    }

}