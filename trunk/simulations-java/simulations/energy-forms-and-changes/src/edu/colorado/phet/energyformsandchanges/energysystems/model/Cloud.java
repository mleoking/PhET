// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.CLOUD_1;

/**
 * Class that represents a cloud in the view.
 *
 * @author John Blanco
 */
public class Cloud {

    private static final double CLOUD_WIDTH = 0.035; // In meters, though obviously not to scale.  Empirically determined.
    public static final ModelElementImage CLOUD_IMAGE = new ModelElementImage( CLOUD_1, CLOUD_WIDTH, new Vector2D( 0, 0 ) );
    private static final double CLOUD_HEIGHT = CLOUD_IMAGE.getHeight(); // In meters, though obviously not to scale.

    public final Vector2D offsetFromParent;
    private final ObservableProperty<Vector2D> parentPosition;
    public final Property<Double> existenceStrength = new Property<Double>( 1.0 );

    public Cloud( Vector2D offsetFromParent, ObservableProperty<Vector2D> parentPosition ) {
        this.offsetFromParent = offsetFromParent;
        this.parentPosition = parentPosition;
    }

    public Shape getCloudAbsorptionReflectionShape() {
        return new Ellipse2D.Double( parentPosition.get().x + offsetFromParent.x - CLOUD_WIDTH / 2,
                                     parentPosition.get().y + offsetFromParent.y - CLOUD_HEIGHT / 2,
                                     CLOUD_WIDTH,
                                     CLOUD_HEIGHT );
    }

    // Get the center position in model coordinates.
    public Vector2D getCenterPosition() {
        return parentPosition.get().plus( offsetFromParent );
    }
}
