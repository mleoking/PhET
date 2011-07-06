package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A particle is an indivisible object with a position such as Na+ or a sugar molecule.
 *
 * @author Sam Reid
 */
public class Particle {
    //Interface for setting and observing the position
    public final Property<ImmutableVector2D> position;

    public Particle( ImmutableVector2D position ) {
        this.position = new Property<ImmutableVector2D>( position );
    }
}