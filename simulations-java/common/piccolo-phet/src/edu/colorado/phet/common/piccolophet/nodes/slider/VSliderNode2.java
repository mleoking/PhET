// Copyright 2002-2012, University of Colorado
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
 * A vertical slider that is implemented purely in Piccolo, i.e. Java Swing is
 * not used.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class VSliderNode2 extends SliderNode {

    //the default width of the track (thin dimension)
    public static final double DEFAULT_TRACK_THICKNESS = 6;

    //The size of the track (how far the knob can move)
    public static final double DEFAULT_TRACK_LENGTH = 200;

    protected PhetPPath trackNode;
    protected KnobNode knobNode;

    // Root node to which all other nodes should be added.  This is done to
    // allow the offset of the composite node to be at (0, 0). Use this when
    // adding children in subclasses if you want to keep the origin at (0, 0).
    protected final PNode rootNode = new PNode();
    public final double trackLength;
    public final double trackThickness;

    /**
     * Constructor that uses defaults for several of the slider parameters.
     *
     * @param userComponent
     * @param min
     * @param max
     * @param value
     */
    public VSliderNode2( IUserComponent userComponent, double min, double max, SettableProperty<Double> value ) {
        this( userComponent, min, max, DEFAULT_TRACK_THICKNESS, DEFAULT_TRACK_LENGTH, value, new Property<Boolean>( true ) );
    }

    /**
     * Constructor that uses default knob.
     *
     * @param userComponent
     * @param min
     * @param max
     * @param value
     */
    public VSliderNode2( IUserComponent userComponent, final double min, final double max, double trackThickness, double trackLength, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        this( userComponent, min, max, new KnobNode( KnobNode.DEFAULT_SIZE, new KnobNode.ColorScheme( new Color( 115, 217, 255 ) ) ), DEFAULT_TRACK_THICKNESS, DEFAULT_TRACK_LENGTH, value, new Property<Boolean>( true ) );
    }

    /**
     * Main constructor.
     *
     * @param userComponent
     * @param min
     * @param max
     * @param trackThickness
     * @param trackLength
     * @param value
     * @param enabled
     */
    public VSliderNode2( IUserComponent userComponent, final double min, final double max, final KnobNode knobNode, double trackThickness, double trackLength, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( userComponent, min, max, value );
        this.trackLength = trackLength;
        this.trackThickness = trackThickness;
        this.knobNode = knobNode;
        addChild( rootNode );

        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, trackThickness, trackLength );
        trackNode = new PhetPPath( trackPath, new GradientPaint( 0, 0, Color.gray, 0, (float) trackLength, Color.white, false ), new BasicStroke( 1 ), Color.BLACK );
        rootNode.addChild( trackNode );
        enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                knobNode.setEnabled( enabled );
            }
        } );
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                double viewY = getViewY( value );
                System.out.println( "value = " + value );
                System.out.println( "knobNode.getFullBoundsReference().getY() - knobNode.getFullBoundsReference().getMinY() = " + ( knobNode.getFullBoundsReference().getY() - knobNode.getFullBoundsReference().getMinY() ) );
                knobNode.setOffset( trackNode.getFullBounds().getCenterX() - knobNode.getFullBounds().getWidth() / 2,
                                    viewY - knobNode.getFullBounds().getHeight() / 2 );
            }
        } );
        knobNode.addInputEventListener( new CursorHandler() );
        knobNode.addInputEventListener( new PDragSequenceEventHandler() {

            private Point2D startPoint;
            public Double startValue;

            @Override public void startDrag( PInputEvent event ) {
                super.startDrag( event );
                dragStarted();
                startPoint = event.getPositionRelativeTo( VSliderNode2.this );
                startValue = value.get();
            }

            @Override public void drag( PInputEvent event ) {
                super.drag( event );
                dragged();
                Point2D point = event.getPositionRelativeTo( VSliderNode2.this );
                final ImmutableVector2D vector = new ImmutableVector2D( startPoint, point );

                Point2D minGlobal = trackNode.localToGlobal( new Point2D.Double( 0, 0 ) );
                Point2D maxGlobal = trackNode.localToGlobal( createEndPoint() );
                final ImmutableVector2D unitVector = new ImmutableVector2D( minGlobal, maxGlobal ).getNormalizedInstance();
                double viewDelta = vector.dot( unitVector );

                double modelDelta = ( max - min ) / trackNode.getFullBounds().getHeight() * viewDelta;
                value.set( MathUtil.clamp( min, startValue + modelDelta, max ) );
            }

            @Override protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                dragEnded();
            }
        } );

        // Create an invisible rectangle that will account for where the knob
        // is able to move so that the overall node's full bounds takes this
        // into account.
        Rectangle2D leftKnobRect = getKnobRect( min );
        Rectangle2D rightKnobRect = getKnobRect( max );
        PhetPPath knobBackground = new PhetPPath( leftKnobRect.createUnion( rightKnobRect ), null, null, null ) {{
            setPickable( false );
            setChildrenPickable( false );
        }};
        rootNode.addChild( knobBackground );

        rootNode.addChild( knobNode );

        adjustOrigin();
    }

    @Override public void setTrackFillPaint( final Paint paint ) {
        trackNode.setPaint( paint );
    }

    private Rectangle2D getKnobRect( double value ) {
        Rectangle2D leftKnobRect = knobNode.getFullBounds();
        final double x = trackNode.getFullBounds().getWidth() / 2 - knobNode.getFullBounds().getWidth() / 2;
        final double y = getViewY( value ) - knobNode.getFullBounds().getHeight() / 2;
        leftKnobRect.setRect( x, y, leftKnobRect.getWidth(), leftKnobRect.getHeight() );
        return leftKnobRect;
    }

    protected Point2D.Double createEndPoint() {
        return new Point2D.Double( 0, trackNode.getFullBounds().getHeight() );
    }

    protected double getViewY( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinY(), trackNode.getFullBounds().getHeight() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        rootNode.addChild( label );
        label.setOffset( knobNode.getFullBounds().getWidth() / 2 + trackThickness / 2, getViewY( value ) - label.getFullBounds().getHeight() / 2 );
        adjustOrigin();
    }

    //Adjust the origin of this node so that it is at (0, 0) in screen coordinates.
    //This will only work if all items have been added to the root node.
    protected void adjustOrigin() {
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

            VSliderNode2 sliderNode = new VSliderNode2( new UserComponent( "mySlider" ), -1, +1, sliderValue ) {{
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