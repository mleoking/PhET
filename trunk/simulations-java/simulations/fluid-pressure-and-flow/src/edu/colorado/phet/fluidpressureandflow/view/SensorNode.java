// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function0;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fluidpressureandflow.model.Sensor;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class SensorNode<T> extends PNode {

    protected final Property<String> textProperty;

    public SensorNode( final ModelViewTransform transform, final Sensor<T> sensor, final Property<Units.Unit> unitsProperty ) {
        final Function0<String> getString = new Function0<String>() {
            public String apply() {
                String pattern = "{0} {1}"; //TODO i18n
                String value = "?"; //TODO i18n
                if ( !Double.isNaN( sensor.getScalarValue() ) ) {
                    value = unitsProperty.getValue().getDecimalFormat().format( unitsProperty.getValue().siToUnit( sensor.getScalarValue() ) );
                }
                String units = unitsProperty.getValue().getAbbreviation();
                return MessageFormat.format( pattern, value, units );
            }
        };
        textProperty = new Property<String>( getString.apply() );
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                textProperty.setValue( getString.apply() );
            }
        };
        sensor.addValueObserver( observer );
        unitsProperty.addObserver( observer );

        addInputEventListener( new CursorHandler() );

        sensor.addLocationObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
    }
}
