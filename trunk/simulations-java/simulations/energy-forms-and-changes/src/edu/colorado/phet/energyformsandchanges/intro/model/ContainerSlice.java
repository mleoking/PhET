// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * This class represents a "slice" within a 2D container that can be used to
 * add some limited 3D capabilities.  The slice consists of a 2D shape and
 * a Z value representing its position in Z space.
 *
 * @author John Blanco
 */
public class ContainerSlice {
    private final double zPosition;
    private Shape shape;

    public ContainerSlice( Shape shape, double zPosition, ObservableProperty<ImmutableVector2D> anchorPoint ) {
        this.shape = shape;
        this.zPosition = zPosition;

        // Monitor the anchor position and move as it does.
        anchorPoint.addObserver( new ChangeObserver<ImmutableVector2D>() {
            public void update( ImmutableVector2D newPosition, ImmutableVector2D oldPosition ) {
                ImmutableVector2D translation = newPosition.getSubtractedInstance( oldPosition );
                ContainerSlice.this.shape = AffineTransform.getTranslateInstance( translation.getX(), translation.getY() ).createTransformedShape( ContainerSlice.this.shape );
            }
        } );
    }

    public double getZPosition() {
        return zPosition;
    }

    public Shape getShape() {
        return shape;
    }
}
