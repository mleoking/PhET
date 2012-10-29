// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A drop of water, generally used to create a stream of water.
 *
 * @author John Blanco
 */
public class WaterDrop {
    public static final double MASS = 1; // In kg, used for motion calculations.
    public final Property<Vector2D> offsetFromParent;
    public final Property<Vector2D> velocity;
    public final Property<Dimension2D> size;

    public WaterDrop( Vector2D initialOffsetFromParent, Vector2D initialVelocity, Dimension2D size ) {
        this.offsetFromParent = new Property<Vector2D>( initialOffsetFromParent );
        this.velocity = new Property<Vector2D>( initialVelocity );
        this.size = new Property<Dimension2D>( size );
    }
}
