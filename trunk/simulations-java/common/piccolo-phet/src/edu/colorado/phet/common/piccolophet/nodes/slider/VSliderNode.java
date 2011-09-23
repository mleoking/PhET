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
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolox.PFrame;

/**
 * @author Sam Reid
 */
public class VSliderNode extends SliderNode {
    private PhetPPath trackNode;
    private PNode knobNode;
    private int trackWidth;

    public VSliderNode( final double min, final double max, final SettableProperty<Double> value ) {
        this( min, max, value, new Property<Boolean>( true ) );
    }

    public VSliderNode( final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( min, max, value, enabled );

        trackWidth = 6;
        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, trackWidth, 200 );
        trackNode = new PhetPPath( trackPath, new GradientPaint( trackWidth / 2, 0, Color.white, trackWidth, 0, Color.gray, false ), new BasicStroke( 1 ), new GradientPaint( 0, 0, Color.gray, 0, trackWidth, Color.black, false ) );
        addChild( trackNode );
        knobNode = new ZeroOffsetNode( new KnobNode( KnobNode.DEFAULT_SIZE, new KnobNode.ColorScheme( new Color( 115, 217, 255 ) ) ) {{
            rotate( Math.PI * 2 * 3.0 / 4.0 );
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    setEnabled( enabled );
                }
            } );

        }} ) {{
            value.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    System.out.println( "value = " + value );
                    double viewY = getViewY( value );
                    setOffset( trackNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, viewY - getFullBounds().getHeight() / 2 );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                private Point2D startPoint;
                public Double startValue;

                @Override public void mousePressed( PInputEvent event ) {
                    super.mousePressed( event );
                    startPoint = event.getPositionRelativeTo( VSliderNode.this );
                    startValue = value.get();
                }

                @Override public void mouseDragged( PInputEvent event ) {
                    Point2D point = event.getPositionRelativeTo( VSliderNode.this );
                    final ImmutableVector2D vector = new ImmutableVector2D( startPoint, point );

                    Point2D minGlobal = trackNode.localToGlobal( new Point2D.Double( 0, 0 ) );
                    Point2D maxGlobal = trackNode.localToGlobal( new Point2D.Double( 0, trackNode.getFullBounds().getHeight() ) );
                    final ImmutableVector2D unitVector = new ImmutableVector2D( minGlobal, maxGlobal ).getNormalizedInstance();
                    double viewDelta = vector.dot( unitVector );

                    double modelDelta = ( max - min ) / trackNode.getFullBounds().getHeight() * viewDelta;
                    value.set( MathUtil.clamp( min, startValue + modelDelta, max ) );
                }
            } );
        }};
        addChild( knobNode );
    }

    protected double getViewY( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinY(), trackNode.getFullBounds().getHeight() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        addChild( label );
        label.setOffset( knobNode.getFullBounds().getWidth() / 2 + trackWidth / 2, getViewY( value ) - label.getFullBounds().getHeight() / 2 );
    }

    public static void main( String[] args ) {
        PDebug.debugBounds = true;
        new PFrame( "test", false, new PCanvas() {{

            VSliderNode sliderNode = new VSliderNode( -1, +1, new Property<Double>( 0.0 ) ) {{
                setOffset( 200, 200 );
            }};
            sliderNode.addLabel( +1, new PhetPText( "Add", new PhetFont( 16 ) ) );
            sliderNode.addLabel( 0.0, new PhetPText( "0", new PhetFont( 16 ) ) );
            sliderNode.addLabel( -1, new PhetPText( "Remove", new PhetFont( 16 ) ) );

            getLayer().addChild( sliderNode );

            setPanEventHandler( null );
        }} ) {{
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}
