// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fluidpressureandflow.common.model.Sensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.QUESTION_MARK;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.VALUE_WITH_UNITS_PATTERN;

/**
 * @author Sam Reid
 */
public abstract class SensorNode extends PNode {

    protected final Property<String> text;

    public SensorNode( final ModelViewTransform transform, final Sensor<Double> sensor, final Property<Unit> units ) {
        final Function0<String> getString = new Function0<String>() {
            public String apply() {
                String pattern = VALUE_WITH_UNITS_PATTERN;
                String value = QUESTION_MARK;
                if ( !Double.isNaN( sensor.getValue() ) ) {
                    value = units.get().getDecimalFormat().format( units.get().siToUnit( sensor.getValue() ) );
                }
                return MessageFormat.format( pattern, value, units.get().getAbbreviation() );
            }
        };
        text = new Property<String>( getString.apply() );
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                text.set( getString.apply() );
            }
        };
        sensor.addValueObserver( observer );
        units.addObserver( observer );

        addInputEventListener( new CursorHandler() );

        sensor.location.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
    }
}
