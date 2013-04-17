// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A drop of water, generally used to create a stream of water.
 *
 * @author John Blanco
 */
public class WaterDrop {

    // The following constant is used to adjust the way in which the drop
    // elongates as its velocity increases.
    private static final double WIDTH_CHANGE_TWEAK_FACTOR = 2;

    public final Property<Vector2D> offsetFromParent;
    public final Property<Vector2D> velocity;
    public final Property<Dimension2D> size;

    public WaterDrop( Vector2D initialOffsetFromParent, Vector2D initialVelocity, final Dimension2D size ) {
        this.offsetFromParent = new Property<Vector2D>( initialOffsetFromParent );
        this.velocity = new Property<Vector2D>( initialVelocity );
        this.size = new Property<Dimension2D>( size );

        // Update the shape as the velocity increases. This makes it look more
        // realistic.
        velocity.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D velocity ) {
                double newWidth = (1 / (1 + velocity.magnitude() * WIDTH_CHANGE_TWEAK_FACTOR)) * size.getWidth();
                double newHeight = (size.getHeight() * size.getWidth()) / newWidth;
                WaterDrop.this.size.set( new PDimension( newWidth, newHeight ) );
            }
        } );
    }
}
