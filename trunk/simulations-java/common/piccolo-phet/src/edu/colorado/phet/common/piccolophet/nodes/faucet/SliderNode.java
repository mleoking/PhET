// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.faucet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.PFrame;

/**
 * Rewrite for SliderNode, should work at different orientations and support tick labels, etc.
 * See #1767
 *
 * @author Sam Reid
 */
public class SliderNode extends PNode {
    public final SettableProperty<Boolean> enabled = new Property<Boolean>( true );
    private PhetPPath trackNode;
    private double min;
    private double max;
    private KnobNode knobNode;

    public SliderNode( final double min, final SettableProperty<Double> value, final double max ) {
        this( min, value, max, new BooleanProperty( true ) );
    }

    public SliderNode( final double min, final SettableProperty<Double> value, final double max, final ObservableProperty<Boolean> enabled ) {
        this.min = min;
        this.max = max;
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
//        addChild( new PhetPPath( new Ellipse2D.Double( 0, 0, 20, 20 ), Color.blue ) {{
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
                    startPoint = event.getPositionRelativeTo( SliderNode.this );
                    startValue = value.get();
                }

                @Override public void mouseDragged( PInputEvent event ) {
                    Point2D point = event.getPositionRelativeTo( SliderNode.this );
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

    private double getViewX( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinX(), trackNode.getFullBounds().getWidth() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        addChild( label );
        label.setOffset( getViewX( value ) - label.getFullBounds().getWidth() / 2, knobNode.getFullBounds().getHeight() / 2 );
    }

    public static void main( String[] args ) {
        new PFrame( "test", false, new PCanvas() {{
            final SliderNode slider1 = new SliderNode( 0, new Property<Double>( 0.0 ), 100 ) {{
                setOffset( 150, 250 );
            }};
            getLayer().addChild( slider1 );

            final SliderNode slider2 = new SliderNode( 0, new Property<Double>( 0.0 ) {{
                addObserver( new VoidFunction1<Double>() {
                    public void apply( Double angle ) {
                        slider1.setRotation( angle );
                    }
                } );
            }}, Math.PI * 2 ) {{
                setOffset( 400, 250 );
            }};
            getLayer().addChild( slider2 );

            setPanEventHandler( null );
        }} ) {{
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }

    public static class TestSliderForStatesOfMatter {
        public static void main( String[] args ) {
            new PFrame( "test", false, new PCanvas() {{

                SliderNode sliderNode = new SliderNode( -1, new Property<Double>( 0.0 ), +1 );
                sliderNode.addLabel( +1, new ZeroOffsetNode( new PhetPText( "Add", new PhetFont( 16 ) ) {{rotate( Math.PI / 2 );}} ) );
                sliderNode.addLabel( 0.0, new ZeroOffsetNode( new PhetPText( "  0", new PhetFont( 16 ) ) {{rotate( Math.PI / 2 );}} ) );
                sliderNode.addLabel( -1, new ZeroOffsetNode( new PhetPText( "Remove", new PhetFont( 16 ) ) {{rotate( Math.PI / 2 );}} ) );
                sliderNode.setRotation( 2 * 3.0 / 4.0 * Math.PI );

                getLayer().addChild( new ZeroOffsetNode( sliderNode ) {{
                    setOffset( 200, 200 );
                }} );

                setPanEventHandler( null );
            }} ) {{
                setSize( 800, 600 );
                setDefaultCloseOperation( EXIT_ON_CLOSE );
            }}.setVisible( true );
        }
    }
}