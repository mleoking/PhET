// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;

/**
 * Primary model class for the "Energy Systems" tab of the Energy Forms and
 * Changes simulation.
 *
 * @author John Blanco
 */
public class EnergySystemsModel {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // Boolean property that controls whether the energy chunks are visible to the user.
    public final BooleanProperty energyChunksVisible = new BooleanProperty( false );

    // Carousels that control the positions of the energy sources, converters,
    // and users.
    public final Carousel energySourcesCarousel = new Carousel( new ImmutableVector2D( 0, 0 ), new ImmutableVector2D( 0, 0 ) ); // TODO - Position and offset.
    public final Carousel energyConvertersCarousel = new Carousel( new ImmutableVector2D( 0, 0 ), new ImmutableVector2D( 0, 0 ) ); // TODO - Position and offset.
    public final Carousel energyUsersCarousel = new Carousel( new ImmutableVector2D( 0, 0 ), new ImmutableVector2D( 0, 0 ) ); // TODO - Position and offset.

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void reset() {
        // TODO.
    }

    public IClock getClock() {
        return clock;
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------
}
