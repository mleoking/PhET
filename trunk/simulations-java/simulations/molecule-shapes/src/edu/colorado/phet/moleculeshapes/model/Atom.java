package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

public class Atom {
    public final double radius;
    public final Property<ImmutableVector3D> position = new Property<ImmutableVector3D>( new ImmutableVector3D() );

    public Atom( double radius ) {
        this.radius = radius;
    }
}
