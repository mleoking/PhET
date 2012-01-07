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

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
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
    private KnobNode knobNode;

    // Root node to which all other nodes should be added.  This is done to
    // allow the offset of this node to be at (0, 0).  Use this when adding
    // children in subclasses if you want to keep the origin at (0, 0).
    protected PNode rootNode = new PNode();

    public HSliderNode( final double min, final double max, final SettableProperty<Double> value ) {
        this( min, max, value, new Property<Boolean>( true ) );
    }

    public HSliderNode( final double min, final double max, final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        this( min, max, DEFAULT_TRACK_WIDTH, DEFAULT_TRACK_HEIGHT, value, enabled );
    }

    public HSliderNode( final double min, final double max, double trackWidth, double trackHeight,
                        final SettableProperty<Double> value, final ObservableProperty<Boolean> enabled ) {
        super( min, max, value );
        addChild( rootNode );

        final Rectangle2D.Double trackPath = new Rectangle2D.Double( 0, 0, trackWidth, trackHeight );

        trackNode = new PhetPPath( trackPath, getTrackFillPaint( trackPath ),
                                   new BasicStroke( 1 ), new GradientPaint( 0, 0, Color.gray, 0, (float) trackHeight, Color.black, false ) );
        rootNode.addChild( trackNode );

        double knobSize = KnobNode.DEFAULT_SIZE < trackWidth / 5 ? KnobNode.DEFAULT_SIZE : trackWidth / 5;
        knobNode = new KnobNode( knobSize, new KnobNode.ColorScheme( new Color( 115, 217, 255 ) ) ) {{
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    setEnabled( enabled );
                }
            } );
            addInputEventListener( new CursorHandler() );

            //TODO: sim sharing
//            new SimSharingDragSequenceEventHandler.DragFunction() {
//                            public void apply( SimSharingConstants.User.IAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
//                                SimSharingManager.sendUserEvent( simSharingObject, START_DRAG, new Parameter( VALUE, value.get() ), new Parameter( COMPONENT_TYPE, SLIDER ) );
//                            }
//
//                        }, new SimSharingDragSequenceEventHandler.DragFunction() {
//                            public void apply( String action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
//                                SimSharingManager.sendUserEvent( simSharingObject, END_DRAG, new Parameter( VALUE, value.get() ), new Parameter( COMPONENT_TYPE, SLIDER ) );
//                            }
//                        }, null

            addInputEventListener( dragHandler = new SimSharingDragSequenceEventHandler() {

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

    protected void dragEnded() {
    }

    protected void dragged() {
    }

    protected void dragStarted() {
    }

    private Rectangle2D getKnobRect( double value ) {
        Rectangle2D leftKnobRect = knobNode.getFullBounds();
        final double x = getViewX( value ) - knobNode.getFullBounds().getWidth() / 2;
        final double y = trackNode.getFullBounds().getHeight() / 2 - knobNode.getFullBounds().getHeight() / 2;
        leftKnobRect.setRect( x, y, leftKnobRect.getWidth(), leftKnobRect.getHeight() );
        return leftKnobRect;
    }

    /**
     * Get the paint used for filling in the track.  Override in subclasses if
     * a different look is desired.
     *
     * @param trackRect - rectangle that defines shape of the track.
     * @return
     */
    protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
        return new GradientPaint( (float) trackRect.getMinX(), (float) trackRect.getCenterY(), Color.white, (float) trackRect.getWidth(),
                                  (float) trackRect.getCenterY(), Color.gray, false );
    }

    protected double getViewX( double value ) {
        return new Function.LinearFunction( min, max, trackNode.getFullBounds().getMinX(), trackNode.getFullBounds().getMaxX() ).evaluate( value );
    }

    //Add a label to appear under the slider at the specified location
    public void addLabel( double value, PNode label ) {
        label.setOffset( getViewX( value ) - label.getFullBounds().getWidth() / 2, knobNode.getFullBounds().getMaxY() );

        //At discussion on 10/6/2011 we decided every label should have a tick mark that extends to the track but is also visible when the knob is over it
        float tickStroke = 1.5f;
        final PhetPPath tickMark = new PhetPPath( new Line2D.Double( getViewX( value ) - tickStroke / 2, 0, getViewX( value ) - tickStroke / 2, knobNode.getFullBounds().getHeight() / 2 + 3 ), new BasicStroke( tickStroke ), Color.darkGray );
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
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame( "test", false, new PCanvas() {{

                    Property<Double> sliderValue = new Property<Double>( 0.0 ) {{
                        addObserver( new VoidFunction1<Double>() {
                            public void apply( Double newValue ) {
                                System.out.println( "sliderValue = " + newValue );
                            }
                        } );
                    }};

                    final HSliderNode sliderNode = new HSliderNode( 0, 100, sliderValue ) {{
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