package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Sensor;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public abstract class SensorNode<T> extends PNode {
    
    private static final double HOT_SPOT_RADIUS = 3;
    private static final Color HOT_SPOT_COLOR = Color.RED;
    
    /**
     * @param transform
     * @param sensor
     * @param unitsProperty
     */
    public SensorNode( final ModelViewTransform transform, final Sensor<T> sensor, final Property<Units.Unit> unitsProperty ) {
        
        // hot spot
        addChild( new PhetPPath( new Ellipse2D.Double( -HOT_SPOT_RADIUS, -HOT_SPOT_RADIUS, HOT_SPOT_RADIUS * 2, HOT_SPOT_RADIUS * 2 ), HOT_SPOT_COLOR ) );
        
        // value display
        final PText textNode = new PText( getText( sensor, unitsProperty ) ) {{
            setFont( new PhetFont( 18, true ) );
        }};
        addChild( textNode );
        
        addInputEventListener( new CursorHandler() );
        
        sensor.addLocationObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
        final SimpleObserver updateTextObserver = new SimpleObserver() {
            public void update() {
                textNode.setText( getText( sensor, unitsProperty ) );
                textNode.setOffset( -textNode.getFullBounds().getWidth() / 2, -textNode.getFullBounds().getHeight() );
            }
        };
        sensor.addValueObserver( updateTextObserver );
        unitsProperty.addObserver( updateTextObserver );
    }

    private String getText( Sensor<T> sensor, Property<Units.Unit> unitsProperty ) {
        String pattern = "{0} {1}"; //TODO i18n
        String value = "?"; //TODO i18n
        if ( !Double.isNaN( sensor.getScalarValue() ) ) {
            value = unitsProperty.getValue().getDecimalFormat().format( unitsProperty.getValue().siToUnit( sensor.getScalarValue() ) );
        }
        String units = unitsProperty.getValue().getAbbreviation();
        return MessageFormat.format( pattern, value, units );
    }
}
