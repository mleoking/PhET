// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.PFrame;

/**
 * @author Sam Reid
 */
public class HSliderNode extends SliderNode {
    private PhetPPath trackNode;
    private KnobNode knobNode;

    public HSliderNode( final double min, final double max, final SettableProperty<Double> value ) {
        this( min, max, value, new Property<Boolean>( true ) );
    }

    public HSliderNode( final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( min, max, value, enabled );

        final int trackHeight = 5;

        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, 200, trackHeight );

        trackNode = new PhetPPath( trackPath, new GradientPaint( 0, trackHeight / 2, Color.white, 0, trackHeight, Color.gray, false ), new BasicStroke( 1 ), new GradientPaint( 0, 0, Color.gray, 0, trackHeight, Color.black, false ) );
        addChild( trackNode );
        knobNode = new KnobNode( KnobNode.DEFAULT_SIZE, new KnobNode.ColorScheme( new Color( 115, 217, 255 ) ) ) {{
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    setEnabled( enabled );
                }
            } );
            value.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    double viewX = getViewX( value );
                    setOffset( viewX - getFullBounds().getWidth() / 2, trackNode.getFullBounds().getHeight() / 2 - getFullBounds().getHeight() / 2 );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                private Point2D startPoint;
                public Double startValue;

                @Override public void mousePressed( PInputEvent event ) {
                    super.mousePressed( event );
                    startPoint = event.getPositionRelativeTo( HSliderNode.this );
                    startValue = value.get();
                }

                @Override public void mouseDragged( PInputEvent event ) {
                    Point2D point = event.getPositionRelativeTo( HSliderNode.this );
                    final ImmutableVector2D vector = new ImmutableVector2D( startPoint, point );

                    Point2D leftGlobal = trackNode.localToGlobal( new Point2D.Double( 0, 0 ) );
                    Point2D rightGlobal = trackNode.localToGlobal( new Point2D.Double( trackNode.getFullBounds().getWidth(), 0 ) );
                    final ImmutableVector2D unitVector = new ImmutableVector2D( leftGlobal, rightGlobal ).getNormalizedInstance();
                    double viewDelta = vector.dot( unitVector );

                    double modelDelta = ( max - min ) / trackNode.getFullBounds().getWidth() * viewDelta;
                    value.set( MathUtil.clamp( min, startValue + modelDelta, max ) );
                }
            } );
        }};
        addChild( knobNode );
    }

    protected double getViewX( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinX(), trackNode.getFullBounds().getWidth() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        addChild( label );
        label.setOffset( getViewX( value ) - label.getFullBounds().getWidth() / 2, knobNode.getFullBounds().getHeight() / 2 );
    }

    public static void main( String[] args ) {
        new PFrame( "test", false, new PCanvas() {{
            getLayer().addChild( new HSliderNode( 0, 100, new Property<Double>( 0.0 ) ) {{
                addLabel( 0.0, new PhetPText( "Low" ) );
                addLabel( 100.0, new PhetPText( "High" ) );
                setOffset( 150, 250 );
            }} );

            setPanEventHandler( null );
        }} ) {{
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}