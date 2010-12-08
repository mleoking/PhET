package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
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
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public abstract class SensorNode<T> extends PNode {
    
    /**
     * @param transform
     * @param sensor
     * @param unitsProperty
     */
    public SensorNode( final ModelViewTransform transform, final Sensor<T> sensor, final Property<Units.Unit> unitsProperty ) {
        
        // hot spot
        final double hotSpotRadius = 3;
        final Color hotSpotColor = Color.RED;
        PNode hotSpotNode = new PhetPPath( new Ellipse2D.Double( -hotSpotRadius, -hotSpotRadius, hotSpotRadius * 2, hotSpotRadius * 2 ), hotSpotColor );
        
        // value display
        final PText textNode = new PText( getText( sensor, unitsProperty ) ) {{
            setFont( new PhetFont( 18, true ) );
        }};
        
        // background box
        final PPath backgroundNode = new PhetPPath( Color.white, new BasicStroke( 1f ), Color.gray );
        
        // rendering order
        addChild( backgroundNode );
        addChild( textNode );
        addChild( hotSpotNode );
        
        addInputEventListener( new CursorHandler() );
        
        sensor.addLocationObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
        final SimpleObserver updateTextObserver = new SimpleObserver() {
            public void update() {
                
                // update the text and center it
                textNode.setText( getText( sensor, unitsProperty ) );
                final double textYSpacing = -10;
                textNode.setOffset( -textNode.getFullBoundsReference().getWidth() / 2, -textNode.getFullBoundsReference().getHeight() + textYSpacing );
                
                // update the background to enclose the textNode
                final double cornerRadius = 10;
                final double margin = 3;
                final double width = textNode.getFullBoundsReference().getWidth() + ( 2 * margin );
                final double height = textNode.getFullBoundsReference().getHeight() + ( 2 * margin );
                Shape backgroundShape = new RoundRectangle2D.Double( textNode.getFullBoundsReference().getMinX() - margin, textNode.getFullBoundsReference().getMinY() - margin, width, height, cornerRadius, cornerRadius );
                backgroundNode.setPathTo( backgroundShape );
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
