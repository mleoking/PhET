// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunk {

    public final Property<ImmutableVector2D> position;
    public final BooleanProperty visible;

    public EnergyChunk( double x, double y, BooleanProperty visible ) {
        this( new ImmutableVector2D( x, y ), visible );
    }

    public EnergyChunk( ImmutableVector2D initialPosition, BooleanProperty visible ) {
        this.position = new Property<ImmutableVector2D>( initialPosition );
        this.visible = visible;
    }

    public void translate( ImmutableVector2D movement ) {
        position.set( position.get().getAddedInstance( movement ) );
    }
}
