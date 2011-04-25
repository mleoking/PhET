// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Sensor for wave values, reads the wave amplitude as a function of time and location.  Two probes can be used to compare values.
 */
public class WaveSensor {
    private static final double DELTA = 1;//offset the probe so it isn't taking data by default

    //Set the relative location of the probes and body in model coordinates (SI)
    //REVIEW: These values for initial probe and body locations look very specific.  Where did
    // they come from, and why are there so many digits?
    public final Probe probe1 = new Probe( -4.173076923076922E-7 - DELTA, 9.180769230769231E-7 - DELTA );
    public final Probe probe2 = new Probe( -1.5440384615384618E-6 - DELTA, -1.2936538461538458E-6 - DELTA );
    public final Property<ImmutableVector2D> bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( 4.882500000000015E-6 - DELTA, -3.1298076923077013E-6 - DELTA ) );

    public final Clock clock;//Clock to observe the passage of time
    public final BooleanProperty visible = new BooleanProperty( false );//in the play area

    public WaveSensor( final Clock clock,
                       final Function1<ImmutableVector2D, Option<Double>> probe1Value,//Function for getting data from a probe at the specified point
                       final Function1<ImmutableVector2D, Option<Double>> probe2Value ) {
        this.clock = clock;

        //Read samples from the probes when the simulation time changes
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateProbeSample( probe1, probe1Value, clock );
                updateProbeSample( probe2, probe2Value, clock );
            }
        } );
    }

    //Read samples from the probes when the simulation time changes
    private void updateProbeSample( Probe probe, Function1<ImmutableVector2D, Option<Double>> probeValue, Clock clock ) {
        //Read the value from the probe function.  May be None if not intersecting a light ray
        final Option<Double> value = probeValue.apply( probe.position.getValue() );
        if ( value.isSome() ) {
            probe.addSample( new Option.Some<DataPoint>( new DataPoint( clock.getSimulationTime(), value.get() ) ) );
        }
        else {
            probe.addSample( new Option.None<DataPoint>() );
        }
    }

    public void translateBody( Dimension2D delta ) {
        bodyPosition.setValue( bodyPosition.getValue().plus( delta ) );
    }

    //Moves the sensor body and probes until the hot spot (center of one probe) is on the specified position.
    public void translateToHotSpot( Point2D position ) {
        translateAll( new ImmutableVector2D( position ).minus( probe1.position.getValue() ) );
    }

    //Translate the body and probes by the specified model delta
    public void translateAll( ImmutableVector2D delta ) {
        probe1.translate( delta );
        probe2.translate( delta );
        bodyPosition.setValue( bodyPosition.getValue().plus( delta ) );
    }

    //Model for a probe, including its position and recorded data series
    public static class Probe {
        public final Property<ImmutableVector2D> position;
        //Note that mutable data structures typically shouldn't be used with Property, but we are careful not to modify the ArrayList so it is okay (would be better if Java or we provided an immutable list class)
        public final Property<ArrayList<Option<DataPoint>>> series = new Property<ArrayList<Option<DataPoint>>>( new ArrayList<Option<DataPoint>>() );

        public Probe( double x, double y ) {
            position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        }

        public void translate( Dimension2D delta ) {
            position.setValue( position.getValue().plus( delta ) );
        }

        public void translate( ImmutableVector2D delta ) {
            position.setValue( position.getValue().plus( delta ) );
        }

        public void addSample( final Option<DataPoint> sample ) {
            series.setValue( new ArrayList<Option<DataPoint>>( series.getValue() ) {{
                add( sample );
            }} );
        }
    }
}