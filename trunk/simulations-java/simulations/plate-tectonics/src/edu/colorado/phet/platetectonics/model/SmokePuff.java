// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

public class SmokePuff {
    public final Property<ImmutableVector3F> position = new Property<ImmutableVector3F>( new ImmutableVector3F() );
    public final Property<Float> scale = new Property<Float>( 1f );
    public final Property<Float> alpha = new Property<Float>( 0f );
    public float age = 0;
}
