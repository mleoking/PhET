// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
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

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // The default width of the track (thin dimension)
    public static final double DEFAULT_TRACK_THICKNESS = 6;

    // The size of the track (how far the knob can move)
    public static final double DEFAULT_TRACK_LENGTH = 200;

    // Default paint for the track.
    public static final Paint DEFAULT_TRACK_PAINT = Color.BLACK;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    protected PhetPPath trackNode;
    protected KnobNode2 knobNode;

    // Root node to which all other nodes should be added.  This is done to
    // allow the offset of the composite node to be at (0, 0). Use this when
    // adding children in subclasses if you want to keep the origin at (0, 0).
    protected final PNode rootNode = new PNode();
    public final double trackLength;
    public final double trackThickness;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

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
        this( userComponent, min, max, new KnobNode2( KnobNode2.DEFAULT_SIZE, KnobNode2.DEFAULT_STYLE, new KnobNode2.ColorScheme( new Color( 115, 217, 255 ) ) ),
              DEFAULT_TRACK_THICKNESS, DEFAULT_TRACK_LENGTH, value, new Property<Boolean>( true ) );
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
    public VSliderNode2( IUserComponent userComponent, final double min, final double max, final KnobNode2 knobNode, double trackThickness, double trackLength, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( userComponent, min, max, value );
        this.trackLength = trackLength;
        this.trackThickness = trackThickness;
        this.knobNode = knobNode;

        // Add the root node, to which any other nodes should be added.
        addChild( rootNode );

        // Create the track.
        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, trackThickness, trackLength );
        trackNode = new PhetPPath( trackPath, DEFAULT_TRACK_PAINT, new BasicStroke( 1 ), Color.BLACK );

        // Hook up observers to control the enabled state and position of the
        // knob.
        enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                knobNode.setEnabled( enabled );
            }
        } );
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                double viewY = getViewY( value );
                knobNode.setOffset( trackNode.getFullBounds().getCenterX() - knobNode.getFullBounds().getWidth() / 2,
                                    viewY - knobNode.getFullBounds().getHeight() / 2 );
            }
        } );

        // Add the handler that will change the value of the controlled
        // property when the knob is moved.
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

        // Cursor handler.
        knobNode.addInputEventListener( new CursorHandler() );

        // Create an invisible background rectangle that is large enough to
        // encompass the full motion of the knob.  This makes the full bounds
        // of this node much easier to work with when doing layout.
        Rectangle2D leftKnobRect = getKnobRect( min );
        Rectangle2D rightKnobRect = getKnobRect( max );
        PhetPPath knobBackground = new PhetPPath( leftKnobRect.createUnion( rightKnobRect ), null, null, null ) {{
            setPickable( false );
            setChildrenPickable( false );
        }};
        rootNode.addChild( knobBackground );

        // Add the track and the knob.
        rootNode.addChild( trackNode );
        rootNode.addChild( knobNode );

        // Make sure the origin of this node is at (0, 0).
        adjustOrigin();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public void setTrackFillPaint( final Paint paint ) {
        trackNode.setPaint( paint );
    }

    /**
     * Get the point in screen coordinates that represents the end point of the
     * slider track.
     *
     * @return End point of the slider track.
     */
    protected Point2D.Double createEndPoint() {
        return new Point2D.Double( 0, trackNode.getFullBounds().getHeight() );
    }

    /**
     * Map a value to the corresponding position on the slider track.
     *
     * @param value
     * @return
     */
    protected double getViewY( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinY(), trackNode.getFullBounds().getHeight() ).evaluate( value );
    }

    /**
     * Add a label to appear at the specified location on the slider.
     *
     * @param value Numerical value at which the label should appear.  Must be
     *              within the range of the slider.
     * @param label PNode label that will appear at the specified location.
     */
    public void addLabel( double value, PNode label ) {
        rootNode.addChild( label );
        label.setOffset( knobNode.getFullBounds().getWidth() / 2 + trackThickness / 2, getViewY( value ) - label.getFullBounds().getHeight() / 2 );
        adjustOrigin();
    }

    /**
     * Adjust the origin of this node so that it is at (0, 0), which is the
     * convention for PNodes.  This is useful for resetting the origin when
     * other nodes are added in subclasses.
     */
    protected void adjustOrigin() {
        removeAllChildren();
        addChild( new ZeroOffsetNode( rootNode ) );
    }

    private Rectangle2D getKnobRect( double value ) {
        Rectangle2D leftKnobRect = knobNode.getFullBounds();
        final double x = trackNode.getFullBounds().getWidth() / 2 - knobNode.getFullBounds().getWidth() / 2;
        final double y = getViewY( value ) - knobNode.getFullBounds().getHeight() / 2;
        leftKnobRect.setRect( x, y, leftKnobRect.getWidth(), leftKnobRect.getHeight() );
        return leftKnobRect;
    }

    //-------------------------------------------------------------------------
    // Test Harness
    //-------------------------------------------------------------------------

    public static void main( String[] args ) {
        new PFrame( "test", false, new PhetPCanvas() {{

            Property<Double> sliderValue = new Property<Double>( 0.0 ) {{
                addObserver( new VoidFunction1<Double>() {
                    public void apply( Double newValue ) {
                        System.out.println( "sliderValue = " + newValue );
                    }
                } );
            }};

            // Test case: Default values.
            addWorldChild( new VSliderNode2( new UserComponent( "mySlider" ), -1, +1, sliderValue ) {{
                setOffset( 100, 200 );
                addLabel( +1, new PhetPText( "Positive", new PhetFont( 16 ) ) );
                addLabel( 0.0, new PhetPText( "0", new PhetFont( 16 ) ) );
                addLabel( -1, new PhetPText( "Negative", new PhetFont( 16 ) ) );
            }} );

            // Test case: Pointy knob, non-default track size.
            addWorldChild( new VSliderNode2( new UserComponent( "mySlider" ), -1, +1, new KnobNode2( 40, KnobNode2.Style.POINTED_RECTANGLE ), 10, 80, sliderValue, new BooleanProperty( true ) ) {{
                setOffset( 250, 200 );
                addLabel( +1, new PhetPText( "High", new PhetFont( 16 ) ) );
                addLabel( 0.0, new PhetPText( "0", new PhetFont( 16 ) ) );
                addLabel( -1, new PhetPText( "Low", new PhetFont( 16 ) ) );
            }} );

            // Test case: Helmet-style knob.
            addWorldChild( new VSliderNode2( new UserComponent( "mySlider" ), -1, +1, new KnobNode2( 30, KnobNode2.Style.HELMET, new KnobNode2.ColorScheme( new Color( 0, 200, 0 ) ) ), 5, 150, sliderValue, new BooleanProperty( true ) ) {{
                setOffset( 400, 200 );
                addLabel( +1, new PhetPText( "Lots", new PhetFont( 16 ) ) );
                addLabel( 0.0, new PhetPText( "0", new PhetFont( 16 ) ) );
                addLabel( -1, new PhetPText( "None", new PhetFont( 16 ) ) );
            }} );

            setPanEventHandler( null );
        }} ) {{
            setSize( 800, 600 );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}