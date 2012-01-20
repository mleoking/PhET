// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * This is an extension of the ImageMass class that adds a textual label.  This
 * was created in support of a request to label the mystery objects with
 * translatable labels.
 *
 * @author John Blanco
 */
public class LabeledImageMass extends ImageMass {
    private final String labelText;

    /**
     * Constructor.
     */
    public LabeledImageMass( IUserComponent userComponent, double mass, BufferedImage image, double height, Point2D initialPosition, String labelText, boolean isMystery ) {
        super( userComponent, mass, image, height, initialPosition, isMystery );
        this.labelText = labelText;
    }

    public String getLabelText() {
        return labelText;
    }

    @Override public Mass createCopy() {
        // TODO: Needs work on how to identify the mystery object.  Current approach is ambiguous - need label and instance.
        return new LabeledImageMass( BalanceAndTorqueSimSharing.UserComponents.mysteryObject, getMass(), imageProperty.get(), heightProperty.get(), positionProperty.get(), labelText, isMystery() );
    }
}
