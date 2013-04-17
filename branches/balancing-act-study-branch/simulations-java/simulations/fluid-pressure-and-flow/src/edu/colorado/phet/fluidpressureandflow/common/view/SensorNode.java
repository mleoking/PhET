// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fluidpressureandflow.common.model.Sensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.DASH;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.VALUE_WITH_UNITS_PATTERN;
import static java.text.MessageFormat.format;

/**
 * Abstract base class for sensor nodes that can be dragged by the user and read out a value such as pressure.
 *
 * @author Sam Reid
 */
public abstract class SensorNode extends PNode {

    //The value to display on the readout
    protected final Property<String> text;
    public final Sensor<Double> sensor;

    public SensorNode( final ModelViewTransform transform, final Sensor<Double> sensor, final Property<Unit> units ) {
        this.sensor = sensor;

        text = new Property<String>( getDisplayString( units.get(), sensor.getValue() ) );

        //Observe the sensor or units for changes
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                text.set( getDisplayString( units.get(), sensor.getValue() ) );
            }
        };
        sensor.addValueObserver( observer );
        units.addObserver( observer );

        //Make it draggable
        addInputEventListener( new CursorHandler() );
        sensor.location.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.location.get().toPoint2D() ) );
            }
        } );
    }

    //Function that transforms the value to the display readout
    public String getDisplayString( Unit unit, Option<Double> x ) {
        String pattern = VALUE_WITH_UNITS_PATTERN;
        String value = DASH;
        if ( x.isSome() ) {
            value = unit.getDecimalFormat().format( unit.siToUnit( x.get() ) );
        }
        return format( pattern, value, unit.getAbbreviation() );
    }
}