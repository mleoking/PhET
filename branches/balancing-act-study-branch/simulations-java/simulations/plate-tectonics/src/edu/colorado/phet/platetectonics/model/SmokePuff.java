// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for a volcano smoke puff (the position is at the bottom of the puff)
 */
public class SmokePuff {
    public final Property<Vector3F> position = new Property<Vector3F>( new Vector3F() );
    public final Property<Float> scale = new Property<Float>( 1f );
    public final Property<Float> alpha = new Property<Float>( 0f );
    public float age = 0;
}
