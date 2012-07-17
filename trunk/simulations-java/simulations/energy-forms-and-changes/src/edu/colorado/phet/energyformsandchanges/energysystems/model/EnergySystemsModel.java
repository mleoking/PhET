// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

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

    private static final ImmutableVector2D OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL = new ImmutableVector2D( 0, -0.4 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // Boolean property that controls whether the energy chunks are visible to the user.
    public final BooleanProperty energyChunksVisible = new BooleanProperty( false );

    // Carousels that control the positions of the energy sources, converters,
    // and users.
    public final EnergySystemElementCarousel energySourcesCarousel = new EnergySystemElementCarousel( new ImmutableVector2D( -0.1, 0 ), OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL );
    public final EnergySystemElementCarousel energyConvertersCarousel = new EnergySystemElementCarousel( new ImmutableVector2D( 0, 0 ), OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL );
    public final EnergySystemElementCarousel energyUsersCarousel = new EnergySystemElementCarousel( new ImmutableVector2D( 0.1, 0 ), OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL );
    private final List<Carousel> carousels = new ArrayList<Carousel>() {{
        add( energySourcesCarousel );
        add( energyConvertersCarousel );
        add( energyUsersCarousel );
    }};

    // TODO temp.
    public final List<ShapeModelElement> shapeModelElementList = new ArrayList<ShapeModelElement>();

    // Energy sources.
    public final TeaPot teaPot = new TeaPot();
    public final Sun sun = new Sun();

    // Energy Converters.
    public final WaterPoweredGenerator waterPoweredGenerator = new WaterPoweredGenerator();
    public final SolarPanel solarPanel = new SolarPanel();

    // Energy users.
    public final IncandescentLightBulb incandescentLightBulb = new IncandescentLightBulb();
    public final FluorescentLightBulb fluorescentLightBulb = new FluorescentLightBulb();
    public final BeakerHeater beakerHeater = new BeakerHeater();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergySystemsModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        energySourcesCarousel.add( teaPot );
        energySourcesCarousel.add( sun );
        energyConvertersCarousel.add( waterPoweredGenerator );
        energyConvertersCarousel.add( solarPanel );
        energyUsersCarousel.add( beakerHeater );
        energyUsersCarousel.add( incandescentLightBulb );
        energyUsersCarousel.add( fluorescentLightBulb );
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
