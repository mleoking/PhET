// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
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
    public final Carousel energyConvertersCarousel = new Carousel( new ImmutableVector2D( -0.01, 0 ), new ImmutableVector2D( 0, -0.12 ) ); // TODO - Position and offset.
    public final Carousel energyUsersCarousel = new Carousel( new ImmutableVector2D( 0, 0 ), new ImmutableVector2D( 0, 0 ) ); // TODO - Position and offset.
    private final List<Carousel> carousels = new ArrayList<Carousel>() {{
        add( energySourcesCarousel );
        add( energyConvertersCarousel );
        add( energyUsersCarousel );
    }};

    // TODO temp.
    public final List<ShapeModelElement> shapeModelElementList = new ArrayList<ShapeModelElement>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergySystemsModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // TODO: This is temporary. Add some shapes to the carousels for testing purposes.
        ShapeModelElement redRectangle = new ShapeModelElement( new Rectangle2D.Double( -0.05, -0.05, 0.1, 0.1 ), new Color( 210, 105, 30 ) );
        shapeModelElementList.add( redRectangle );
        energyConvertersCarousel.add( redRectangle );

        ShapeModelElement purpleRoundedRect = new ShapeModelElement( new RoundRectangle2D.Double( -0.05, -0.05, 0.1, 0.1, 0.05, 0.05 ), new Color( 153, 50, 204 ) );
        shapeModelElementList.add( purpleRoundedRect );
        energyConvertersCarousel.add( purpleRoundedRect );

        ShapeModelElement greenCircle = new ShapeModelElement( new Ellipse2D.Double( -0.05, -0.05, 0.1, 0.1 ), new Color( 0, 128, 0 ) );
        shapeModelElementList.add( greenCircle );
        energyConvertersCarousel.add( greenCircle );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void reset() {
        // TODO.
    }

    public IClock getClock() {
        return clock;
    }

    private void stepInTime( double dt ) {
        for ( Carousel carousel : carousels ) {
            carousel.stepInTime( dt );
        }
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------
}
