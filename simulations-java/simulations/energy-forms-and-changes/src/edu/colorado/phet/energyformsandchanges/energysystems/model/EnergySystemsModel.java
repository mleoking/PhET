// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
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

    private static final Vector2D OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL = new Vector2D( 0, -0.4 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // Boolean property that controls whether the energy chunks are visible to the user.
    public final BooleanProperty energyChunksVisible = new BooleanProperty( false );

    // Carousels that control the positions of the energy sources, converters,
    // and users.
    public final EnergySystemElementCarousel energySourcesCarousel = new EnergySystemElementCarousel( new Vector2D( -0.15, 0 ), OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL );
    public final EnergySystemElementCarousel energyConvertersCarousel = new EnergySystemElementCarousel( new Vector2D( -0.025, 0 ), OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL );
    public final EnergySystemElementCarousel energyUsersCarousel = new EnergySystemElementCarousel( new Vector2D( 0.1, 0 ), OFFSET_BETWEEN_ELEMENTS_ON_CAROUSEL );
    private final List<Carousel> carousels = new ArrayList<Carousel>() {{
        add( energySourcesCarousel );
        add( energyConvertersCarousel );
        add( energyUsersCarousel );
    }};

    // TODO temp.
    public final List<ShapeModelElement> shapeModelElementList = new ArrayList<ShapeModelElement>();

    // Energy sources.
    public final FaucetAndWater faucet = new FaucetAndWater();
    public final Sun sun = new Sun();
    public final TeaPot teaPot = new TeaPot();

    // Energy Converters.
    public final WaterPoweredGenerator waterPoweredGenerator = new WaterPoweredGenerator();
    public final SolarPanel solarPanel = new SolarPanel();

    // Energy users.
    public final IncandescentLightBulb incandescentLightBulb = new IncandescentLightBulb();
    public final FluorescentLightBulb fluorescentLightBulb = new FluorescentLightBulb();
    public final BeakerHeater beakerHeater = new BeakerHeater();

    // List of all energy systems.
    private final List<EnergySystemElement> energySystemElements = Arrays.asList(
            faucet, sun, teaPot,
            waterPoweredGenerator, solarPanel,
            incandescentLightBulb, fluorescentLightBulb, beakerHeater
    );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergySystemsModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        energySourcesCarousel.add( faucet );
        energySourcesCarousel.add( sun );
        energySourcesCarousel.add( teaPot );
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
