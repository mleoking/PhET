// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;

/**
 * Horizontal slider that uses only Piccolo, i.e. there is no Swing involved.
 *
 * @author Sam Reid
 */
public class HSliderNode extends SliderNode {

    private static final double DEFAULT_TRACK_WIDTH = 200;
    private static final double DEFAULT_TRACK_HEIGHT = 5;

    private PhetPPath trackNode;
    private PNode knobNode;

    // Root node to which all other nodes should be added.  This is done to
    // allow the offset of this node to be at (0, 0).  Use this when adding
    // children in subclasses if you want to keep the origin at (0, 0).
    protected PNode rootNode = new PNode();

    public HSliderNode( IUserComponent userComponent, double min, double max, SettableProperty<Double> value ) {
        this( userComponent, min, max, value, new Property<Boolean>( true ) );
    }

    public HSliderNode( IUserComponent userComponent, double min, double max, SettableProperty<Double> value, ObservableProperty<Boolean> enabled ) {
        this( userComponent, min, max, DEFAULT_TRACK_WIDTH, DEFAULT_TRACK_HEIGHT, value, enabled );
    }

    public HSliderNode( IUserComponent userComponent, final double min, final double max, double trackWidth, double trackHeight,
                        final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( userComponent, min, max, value );
        addChild( rootNode );

        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, trackWidth, trackHeight );

        trackNode = new PhetPPath( trackPath, getTrackFillPaint( trackWidth, trackHeight ), new BasicStroke( 1 ), getTrackStrokePaint( trackWidth, trackHeight ) );
        rootNode.addChild( trackNode );

        double knobSize = KnobNode.DEFAULT_SIZE < trackWidth / 5 ? KnobNode.DEFAULT_SIZE : trackWidth / 5;

        //Create the knob node here then wrap in zero offset node to account for offset, see #3245
        KnobNode _knobNode = new KnobNode( knobSize, new KnobNode.ColorScheme( new Color( 115, 217, 255 ) ) ) {{
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    setEnabled( enabled );
                }
            } );
            addInputEventListener( new CursorHandler() );

            addInputEventListener( new PDragSequenceEventHandler() {

                private Point2D startPoint;
                public Double startValue;

                @Override public void startDrag( PInputEvent event ) {
                    super.startDrag( event );
                    dragStarted();
                    startPoint = event.getPositionRelativeTo( HSliderNode.this );
                    startValue = value.get();
                }

                @Override public void drag( PInputEvent event ) {
                    super.drag( event );
                    dragged();
                    Point2D point = event.getPositionRelativeTo( HSliderNode.this );
                    final ImmutableVector2D vector = new ImmutableVector2D( startPoint, point );

                    final ImmutableVector2D unitVector = new ImmutableVector2D( 1, 0 );
                    double viewDelta = vector.dot( unitVector );

                    double modelDelta = ( max - min ) / trackNode.getFullBounds().getWidth() * viewDelta;

                    //If the slider became disabled while user was dragging, do not allow value to be changed
                    if ( enabled.get() ) {
                        value.set( MathUtil.clamp( min, startValue + modelDelta, max ) );
                    }
                }

                @Override protected void endDrag( PInputEvent event ) {
                    super.endDrag( event );
                    dragEnded();
                }
            } );
        }};
        knobNode = new ZeroOffsetNode( _knobNode );

        SimpleObserver updateKnobPosition = new SimpleObserver() {
            public void update() {
                double viewX = getViewX( value.get() );
                knobNode.setOffset( viewX - knobNode.getFullBounds().getWidth() / 2,
                                    trackNode.getFullBounds().getCenterY() - knobNode.getFullBounds().getHeight() / 2 );
            }
        };
        value.addObserver( updateKnobPosition );

        //Create an invisible rectangle that will account for where the knob could be
        //This is so that the knob won't overlap other layout elements that are positioned using this node's full bounds
        Rectangle2D leftKnobRect = getKnobRect( min );
        Rectangle2D rightKnobRect = getKnobRect( max );
        PhetPPath knobBackground = new PhetPPath( leftKnobRect.createUnion( rightKnobRect ), null, null, null ) {{
            setPickable( false );
            setChildrenPickable( false );
        }};
        rootNode.addChild( knobBackground );

        //Add the knob itself
        rootNode.addChild( knobNode );

        adjustOrigin();
    }

    private Rectangle2D getKnobRect( double value ) {
        Rectangle2D leftKnobRect = knobNode.getFullBounds();
        final double x = getViewX( value ) - knobNode.getFullBounds().getWidth() / 2;
        final double y = trackNode.getFullBounds().getHeight() / 2 - knobNode.getFullBounds().getHeight() / 2;
        leftKnobRect.setRect( x, y, leftKnobRect.getWidth(), leftKnobRect.getHeight() );
        return leftKnobRect;
    }

    protected Paint getTrackFillPaint( double trackWidth, double trackHeight ) {
        return new GradientPaint( 0, 0, Color.white, (float)trackWidth, 0, Color.gray, false );
    }

    protected Paint getTrackStrokePaint( double trackWidth, double trackHeight ) {
        return Color.BLACK;
    }

    protected double getViewX( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinX(), trackNode.getFullBounds().getMaxX() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        label.setOffset( getViewX( value ) - label.getFullBounds().getWidth() / 2, knobNode.getFullBounds().getMaxY() );

        //At discussion on 10/6/2011 we decided every label should have a tick mark that extends to the track but is also visible when the knob is over it
        float tickStrokeWidth = 1.5f;
        final PhetPPath tickMark = new PhetPPath( new Line2D.Double( getViewX( value ), 0, getViewX( value ), knobNode.getFullBounds().getHeight() / 2 + 6 ), new BasicStroke( tickStrokeWidth ), Color.darkGray );
        rootNode.addChild( tickMark );

        rootNode.addChild( label );

        //Make the tick mark appear behind the track and knob
        tickMark.moveToBack();

        adjustOrigin();
    }

    /**
     * Adjust the origin of this node so that it is at (0, 0) in screen
     * coordinates.  This will only work if all items have been added to the
     * root node.
     */
    protected void adjustOrigin() {
        removeAllChildren();
        addChild( new ZeroOffsetNode( rootNode ) );
    }

    public static void main( String[] args ) {

        //Init sim sharing manager so it doesn't exception when trying to record messages
        String[] myArgs = { "-study" };
        SimSharingManager.init( new PhetApplicationConfig( myArgs, "myProject" ) );
//        PDebug.debugBounds = true;

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame( "test", false, new PCanvas() {{

                    //Emulate sim environment as closely as possible for debugging.
                    setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
                    setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
                    setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

                    Property<Double> sliderValue = new Property<Double>( 0.0 ) {{
                        addObserver( new VoidFunction1<Double>() {
                            public void apply( Double newValue ) {
                                System.out.println( "sliderValue = " + newValue );
                            }
                        } );
                    }};

                    final HSliderNode sliderNode = new HSliderNode( new UserComponent( "mySlider" ), 0, 100, 300, 10, sliderValue, new Property<Boolean>( true ) ) {{
                        addLabel( 0.0, new PhetPText( "None" ) );
                        addLabel( 100.0, new PhetPText( "Lots" ) );
                        setOffset( 150, 250 );
                    }};
                    getLayer().addChild( sliderNode );

                    setPanEventHandler( null );
                }} ) {{
                    setSize( 800, 600 );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}