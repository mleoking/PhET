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

    //Set the relative location of the probes
    public final Probe probe1 = new Probe( -4.173076923076922E-7 - DELTA, 9.180769230769231E-7 - DELTA );
    public final Probe probe2 = new Probe( -1.5440384615384618E-6 - DELTA, -1.2936538461538458E-6 - DELTA );
    public final Property<ImmutableVector2D> bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( 4.882500000000015E-6 - DELTA, -3.1298076923077013E-6 - DELTA ) );
    public final Clock clock;
    public final BooleanProperty visible = new BooleanProperty( false );//in the play area

    public WaveSensor( final Clock clock, final Function1<ImmutableVector2D, Option<Double>> probe1Value, final Function1<ImmutableVector2D, Option<Double>> probe2Value ) {
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateProbeSample( probe1, probe1Value, clock );
                updateProbeSample( probe2, probe2Value, clock );
            }
        } );
    }

    private void updateProbeSample( Probe probe, Function1<ImmutableVector2D, Option<Double>> probeValue, Clock clock ) {
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

    public void translateAll( ImmutableVector2D delta ) {
        probe1.translate( delta );
        probe2.translate( delta );
        bodyPosition.setValue( bodyPosition.getValue().plus( delta ) );
    }

    public static class Probe {
        public final Property<ImmutableVector2D> position;
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