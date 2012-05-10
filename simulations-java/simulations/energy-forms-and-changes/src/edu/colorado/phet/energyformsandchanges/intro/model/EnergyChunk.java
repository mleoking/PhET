// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunk {

    public final Property<ImmutableVector2D> position;

    public EnergyChunk( double x, double y ) {
        this( new ImmutableVector2D( x, y ) );
    }

    public EnergyChunk( ImmutableVector2D initialPosition ) {
        this.position = new Property<ImmutableVector2D>( initialPosition );
    }
}
