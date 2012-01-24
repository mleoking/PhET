// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.PFrame;

/**
 * A vertical slider that is implemented purely in Piccolo, i.e. Java Swing is not used.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class VSliderNode extends SliderNode {
    private PhetPPath trackNode;
    private PNode knobNode;
    private int trackWidth;

    // Root node to which all other nodes should be added.  This is done to
    // allow the offset of this node to be at (0, 0).  Use this when adding
    // children in subclasses if you want to keep the origin at (0, 0).
    protected PNode rootNode = new PNode();

    public VSliderNode( IUserComponent userComponent, double min, double max, SettableProperty<Double> value ) {
        this( userComponent, min, max, value, new Property<Boolean>( true ), 200 );
    }

    public VSliderNode( IUserComponent userComponent, final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled, int trackHeight ) {
        super( userComponent, min, max, value );
        addChild( rootNode );

        trackWidth = 6;
        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, trackWidth, trackHeight );
        trackNode = new PhetPPath( trackPath, getTrackFillPaint( trackWidth, trackHeight ), new BasicStroke( 1 ), getTrackStrokePaint( trackWidth, trackHeight ) );
        rootNode.addChild( trackNode );
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
                    double viewY = getViewY( value );
                    setOffset( trackNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, viewY - getFullBounds().getHeight() / 2 );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragSequenceEventHandler() {

                private Point2D startPoint;
                public Double startValue;

                @Override public void startDrag( PInputEvent event ) {
                    super.startDrag( event );
                    dragStarted();
                    startPoint = event.getPositionRelativeTo( VSliderNode.this );
                    startValue = value.get();
                }

                @Override public void drag( PInputEvent event ) {
                    super.drag( event );
                    dragged();
                    Point2D point = event.getPositionRelativeTo( VSliderNode.this );
                    final ImmutableVector2D vector = new ImmutableVector2D( startPoint, point );

                    Point2D minGlobal = trackNode.localToGlobal( new Point2D.Double( 0, 0 ) );
                    Point2D maxGlobal = trackNode.localToGlobal( new Point2D.Double( 0, trackNode.getFullBounds().getHeight() ) );
                    final ImmutableVector2D unitVector = new ImmutableVector2D( minGlobal, maxGlobal ).getNormalizedInstance();
                    double viewDelta = vector.dot( unitVector );

                    double modelDelta = ( min - max ) / trackNode.getFullBounds().getHeight() * viewDelta;
                    value.set( MathUtil.clamp( min, startValue + modelDelta, max ) );
                }

                @Override protected void endDrag( PInputEvent event ) {
                    super.endDrag( event );
                    dragEnded();
                }
            } );
        }};
        rootNode.addChild( knobNode );

        adjustOrigin();
    }

    public Paint getTrackStrokePaint( double trackWidth, double trackHeight ) {
        return new GradientPaint( 0, 0, Color.gray, 0, (float) trackWidth, Color.black, false );
    }

    public Paint getTrackFillPaint( double trackWidth, double trackHeight ) {
        return new GradientPaint( (float) ( trackWidth / 2.0 ), 0, Color.white, (float) trackWidth, 0, Color.gray, false );
    }

    protected double getViewY( double value ) {
        return new Function.LinearFunction( max, min, trackNode.getFullBounds().getMinY(), trackNode.getFullBounds().getHeight() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        rootNode.addChild( label );
        label.setOffset( knobNode.getFullBounds().getWidth() / 2 + trackWidth / 2, getViewY( value ) - label.getFullBounds().getHeight() / 2 );
        adjustOrigin();
    }

    /**
     * Adjust the origin of this node so that it is at (0, 0) in screen
     * coordinates.  This will only work if all items have been added to the
     * root node.
     */
    private void adjustOrigin() {
        removeAllChildren();
        addChild( new ZeroOffsetNode( rootNode ) );
    }

    public static void main( String[] args ) {
        new PFrame( "test", false, new PCanvas() {{

            Property<Double> sliderValue = new Property<Double>( 0.0 ) {{
                addObserver( new VoidFunction1<Double>() {
                    public void apply( Double newValue ) {
                        System.out.println( "sliderValue = " + newValue );
                    }
                } );
            }};

            VSliderNode sliderNode = new VSliderNode( new UserComponent( "mySlider" ), -1, +1, sliderValue ) {{
                setOffset( 200, 200 );
            }};
            sliderNode.addLabel( +1, new PhetPText( "Positive", new PhetFont( 16 ) ) );
            sliderNode.addLabel( 0.0, new PhetPText( "0", new PhetFont( 16 ) ) );
            sliderNode.addLabel( -1, new PhetPText( "Negative", new PhetFont( 16 ) ) );

            getLayer().addChild( sliderNode );

            setPanEventHandler( null );
        }} ) {{
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}
