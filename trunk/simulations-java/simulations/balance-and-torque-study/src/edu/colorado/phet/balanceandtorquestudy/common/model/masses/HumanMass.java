// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

/**
 * Base class for a class that represents a person, a.k.a. a human, in the
 * model.  The human can be grabbed from a tool box and placed on a balance, so
 * there needs to be a standing and sitting image representation.
 *
 * @author John Blanco
 */
public class HumanMass extends ImageMass {

    private final BufferedImage standingImage;
    private final double standingHeight;
    private final BufferedImage sittingImage;
    private final double sittingHeight;
    private final double sittingCenterOfMassXOffset;

    /**
     * Constructor.
     *
     * @param mass
     * @param standingImage
     * @param standingHeight
     * @param sittingImage
     * @param sittingHeight
     * @param initialPosition
     * @param sittingCenterOfMassXOffset
     * @param isMystery
     */
    public HumanMass( IUserComponent userComponent, double mass, BufferedImage standingImage, double standingHeight, BufferedImage sittingImage,
                      double sittingHeight, Point2D initialPosition, double sittingCenterOfMassXOffset, boolean isMystery ) {
        super( userComponent, mass, standingImage, standingHeight, initialPosition, isMystery );
        this.standingImage = standingImage;
        this.standingHeight = standingHeight;
        this.sittingImage = sittingImage;
        this.sittingHeight = sittingHeight;
        this.sittingCenterOfMassXOffset = sittingCenterOfMassXOffset;
    }

    @Override public void setOnPlank( boolean onPlank ) {
        if ( onPlank ) {
            heightProperty.set( sittingHeight );
            if ( getPosition().getX() > 0 ) {
                imageProperty.set( sittingImage );
                setCenterOfMassXOffset( sittingCenterOfMassXOffset );
            }
            else {
                // Reverse image if on other side of balance.
                imageProperty.set( BufferedImageUtils.flipX( sittingImage ) );
                setCenterOfMassXOffset( -sittingCenterOfMassXOffset );
            }
        }
        else {
            heightProperty.set( standingHeight );
            imageProperty.set( standingImage );
            setCenterOfMassXOffset( 0 );
        }
    }
}
