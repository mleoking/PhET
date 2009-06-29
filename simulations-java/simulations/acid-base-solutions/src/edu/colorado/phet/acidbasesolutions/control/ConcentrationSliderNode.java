
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.control.IScalarTransform.LogLinearTransform;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Log slider for concentration.
 * Drag the thumb or click in the track.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationSliderNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Thumb
    private static final PDimension THUMB_SIZE = new PDimension( 13, 18 );
    private static final Color THUMB_ENABLED_COLOR = ABSColors.THUMB_ENABLED;
    private static final Color THUMB_HILITE_COLOR = ABSColors.THUMB_HIGHLIGHTED;
    private static final Color THUMB_DISABLED_COLOR = ABSColors.THUMB_DISABLED;
    private static final Color THUMB_STROKE_COLOR = Color.BLACK;
    private static final Stroke THUMB_STROKE = new BasicStroke( 1f );

    // Track
    private static final PDimension TRACK_SIZE = new PDimension( 180, 5 );
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final float TRACK_STROKE_WIDTH = 1f;
    private static final Stroke TRACK_STROKE = new BasicStroke( TRACK_STROKE_WIDTH );

    // Major ticks
    private static final double MAJOR_TICK_LENGTH = 10;
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final double MAJOR_TICK_LABEL_Y_SPACING = 2;

    // Major tick labels
    private static final Font MAJOR_TICK_LABEL_FONT = new PhetFont( 12 );
    private static final Color MAJOR_TICK_LABEL_COLOR = Color.BLACK;
    
    // Minor ticks
    private static final double MINOR_TICK_LENGTH = 0.6 * MAJOR_TICK_LENGTH;
    private static final Color MINOR_TICK_COLOR = Color.GRAY;
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final double MINOR_TICKS_CLOSEST_X_SPACING = 2;
    private static final double MINOR_TICK_X_SPACING_MULTIPLIER = 1.5;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList<ChangeListener> changeListeners;
    private final SliderThumbArrowNode thumbNode;
    private final TrackNode trackNode;
    private final double min, max;
    private double value;
    private final IScalarTransform transform;
    private boolean enabled;
    private final CursorHandler thumbCursorHandler;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConcentrationSliderNode( DoubleRange range ) {
        this( range.getMin(), range.getMax() );
    }
    
    public ConcentrationSliderNode( double min, double max ) {
        assert ( min < max );
        
        this.enabled = true;
        
        this.min = min;
        this.max = max;
        this.value = min;
        transform = new LogLinearTransform( min, max, 0, TRACK_SIZE.getWidth() );
        changeListeners = new ArrayList<ChangeListener>();
        
        // track
        trackNode = new TrackNode();
        addChild( trackNode );
        trackNode.addInputEventListener( new TrackClickHandler( this ) );
        
        // ticks
        createTicks( min, max );
        
        // thumb
        thumbNode = new SliderThumbArrowNode( THUMB_SIZE, THUMB_ENABLED_COLOR, THUMB_STROKE_COLOR, THUMB_STROKE );
        addChild( thumbNode );
        thumbCursorHandler = new CursorHandler();
        thumbNode.addInputEventListener( thumbCursorHandler );
        thumbNode.addInputEventListener( new ThumbDragHandler( this ) );
        thumbNode.addInputEventListener( new HilightHandler( this ) );
        
        // initial state
        updateThumb();
    }

    /*
     * Create major and minor ticks.
     * Major ticks are labeled with 10^N.
     */
    private void createTicks( double min, double max ) {
        
        int minExponent = (int) Math.floor( MathUtil.log10( min ) );
        int maxExponent = (int) Math.ceil( MathUtil.log10( max ) );
        
        // this algorithm assumes that we're dealing powers of 10
        assert( Math.pow( 10, minExponent ) == min );
        assert( Math.pow( 10, maxExponent ) == max );
        
        final double dx = TRACK_SIZE.getWidth() / ( maxExponent - minExponent );
        double xOffset = 0;
        final double yOffset = trackNode.getFullBoundsReference().getMaxY();
        for ( int i = minExponent; i <= maxExponent; i++ ) {
            String label = "10<sup>" + i + "</sup>";
            if ( i == 0 ) {
                label = "1";
            }
            MajorTickNode majorTickNode = new MajorTickNode( label );
            addChild( majorTickNode );
            majorTickNode.setOffset( xOffset, yOffset );
            if ( xOffset != 0 ) {
                createMinorTicks( xOffset - dx, xOffset );
            }
            xOffset += dx;
        }
    }
    
    /*
     * Creates minor ticks between 2 major ticks, moving right-to-left, log spacing.
     */
    private void createMinorTicks( double majorTickLeft, double majorTickRight ) {
        double dx = MINOR_TICKS_CLOSEST_X_SPACING;
        double xOffset = majorTickRight - dx;
        final double yOffset = trackNode.getFullBoundsReference().getMaxY();
        while ( xOffset >= majorTickLeft ) {
            MinorTickNode minorTickNode = new MinorTickNode();
            addChild( minorTickNode );
            minorTickNode.setOffset( xOffset, yOffset );
            dx *= MINOR_TICK_X_SPACING_MULTIPLIER;
            xOffset = xOffset - dx;
        }
    }

    public void setValue( double value ) {
        if ( !( value >= min && value <= max ) ) {
            throw new IllegalArgumentException( "value out of range: " + value );
        }
        if ( value != getValue() ) {
            this.value = value;
            updateThumb();
            fireStateChanged();
        }
    }

    public double getValue() {
        return value;
    }
    
    public double getMin() {
        return min;
    }
    
    public double getMax() {
        return max;
    }
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != this.enabled ) {
            this.enabled = enabled;
            thumbNode.setThumbPaint( enabled ? THUMB_ENABLED_COLOR : THUMB_DISABLED_COLOR );
            if ( enabled ) {
                thumbNode.addInputEventListener( thumbCursorHandler );
            }
            else {
                thumbNode.removeInputEventListener( thumbCursorHandler );
            }
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    protected TrackNode getTrackNode() {
       return trackNode; 
    }
    
    protected SliderThumbArrowNode getThumbNode() {
        return thumbNode;
    }
    
    protected IScalarTransform getScalarTransform() {
        return transform;
    }
    
    private void updateThumb() {
        double xOffset = transform.modelToView( value );
        double yOffset = trackNode.getFullBoundsReference().getCenterY();
        thumbNode.setOffset( xOffset, yOffset );
    }

    public void addChangeListener( ChangeListener listener ) {
        changeListeners.add( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeListeners.remove( listener );
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        Iterator<ChangeListener> i = changeListeners.iterator();
        while ( i.hasNext() ) {
            i.next().stateChanged( event );
        }
    }

    /*
     * The track is a rectangular region, oriented horizontally.
     * Origin is at the upper-left corner.
     * The track is interactive.
     */
    private static class TrackNode extends PPath {

        public TrackNode() {
            super();
            setPathTo( new Rectangle2D.Double( 0, 0, TRACK_SIZE.getWidth(), TRACK_SIZE.getHeight() ) );
            setPaint( TRACK_FILL_COLOR );
            setStroke( TRACK_STROKE );
            setStrokePaint( TRACK_STROKE_COLOR );
        }
    }

    /*
     * Minor ticks are vertical lines that appear below the track.
     * Origin is at the top center.
     * They have no label, and are not interactive.
     */
    private static class MinorTickNode extends PPath {

        public MinorTickNode() {
            super();
            // not interactive
            setPickable( false );
            setChildrenPickable( false );
            // properties
            setPathTo( new Line2D.Double( 0, 0, 0, MINOR_TICK_LENGTH ) );
            setStroke( MINOR_TICK_STROKE );
            setStrokePaint( MINOR_TICK_COLOR );
        }
    }

    /*
     * Major ticks are vertical lines that appear below the track.
     * Origin is at the top center.
     * They are labeled, and the label is centered below the tick line.
     * They are not interactive.
     */
    private static class MajorTickNode extends PComposite {

        public MajorTickNode( String label ) {
            super();
            // not interactive
            setPickable( false );
            setChildrenPickable( false );
            // tick
            PPath tickNode = new PPath();
            tickNode.setPathTo( new Line2D.Double( 0, 0, 0, MAJOR_TICK_LENGTH ) );
            tickNode.setStroke( MAJOR_TICK_STROKE );
            tickNode.setStrokePaint( MAJOR_TICK_COLOR );
            addChild( tickNode );
            // label
            PNode labelNode = new MajorTickLabelNode( label );
            addChild( labelNode );
            // layout, label centered below tick
            tickNode.setOffset( 0, 0 );
            double xOffset = -labelNode.getFullBoundsReference().getWidth() / 2;
            double yOffset = tickNode.getFullBoundsReference().getMaxY() + MAJOR_TICK_LABEL_Y_SPACING;
            labelNode.setOffset( xOffset, yOffset );
        }
    }

    /*
     * Major tick labels are in HTML or plain text format.
     * They are not interactive.
     * Origin is at upper-left corner.
     */
    private static class MajorTickLabelNode extends HTMLNode {

        public MajorTickLabelNode( String label ) {
            super();
            // not interactive
            setPickable( false );
            setChildrenPickable( false );
            // properties
            setHTML( HTMLUtils.toHTMLString( label ) );
            setFont( MAJOR_TICK_LABEL_FONT );
            setHTMLColor( MAJOR_TICK_LABEL_COLOR );
        }
    }

    /*
     * Dragging the thumb changes the slider value.
     */
    private static class ThumbDragHandler extends PDragEventHandler {

        private final ConcentrationSliderNode sliderNode;
        private double _globalClickXOffset; // X offset of mouse click from knob's origin, in global coordinates

        public ThumbDragHandler( ConcentrationSliderNode sliderNode ) {
            super();
            this.sliderNode = sliderNode;
        }

        protected void startDrag( PInputEvent event ) {
            if ( sliderNode.isEnabled() ) {
                super.startDrag( event );
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( sliderNode );
                Point2D pMouseGlobal = sliderNode.localToGlobal( pMouseLocal );
                Point2D pThumbGlobal = sliderNode.localToGlobal( sliderNode.getThumbNode().getOffset() );
                _globalClickXOffset = pMouseGlobal.getX() - pThumbGlobal.getX();
            }
        }

        protected void drag( PInputEvent event ) {
            if ( sliderNode.isEnabled() ) {

                // determine the thumb's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( sliderNode );
                Point2D pMouseGlobal = sliderNode.localToGlobal( pMouseLocal );
                Point2D pThumbGlobal = new Point2D.Double( pMouseGlobal.getX() - _globalClickXOffset, pMouseGlobal.getY() );
                Point2D pThumbLocal = sliderNode.globalToLocal( pThumbGlobal );

                // transform offset to a slider value
                double value = sliderNode.getScalarTransform().viewToModel( pThumbLocal.getX() );
                if ( value < sliderNode.getMin() ) {
                    value = sliderNode.getMin();
                }
                else if ( value > sliderNode.getMax() ) {
                    value = sliderNode.getMax();
                }

                sliderNode.setValue( value );
            }
        }
    }
    
    /*
     * Clicking in the slider track changes the slider value.
     */
    private static class TrackClickHandler extends PBasicInputEventHandler {
        
        private final ConcentrationSliderNode sliderNode;
        
        public TrackClickHandler( ConcentrationSliderNode sliderNode ) {
            super();
            this.sliderNode = sliderNode;
        }
        
        public void mousePressed( PInputEvent event ) {
            
            // determine the offset of the mouse click
            Point2D pMouseLocal = event.getPositionRelativeTo( sliderNode );
            Point2D pMouseGlobal = sliderNode.localToGlobal( pMouseLocal );
            Point2D pTrackLocal = sliderNode.globalToLocal( pMouseGlobal );

            // transform offset to a slider value
            double value = sliderNode.getScalarTransform().viewToModel( pTrackLocal.getX() );
            if ( value < sliderNode.getMin() ) {
                value = sliderNode.getMin();
            }
            else if ( value > sliderNode.getMax() ) {
                value = sliderNode.getMax();
            }
            
            sliderNode.setValue( value );
        }
    }
    
    public static class HilightHandler extends PBasicInputEventHandler {

        private final ConcentrationSliderNode sliderNode;

        public HilightHandler( ConcentrationSliderNode sliderNode ) {
            super();
            this.sliderNode = sliderNode;
        }

        public void mouseEntered( PInputEvent event ) {
            if ( sliderNode.isEnabled() ) {
                sliderNode.getThumbNode().setThumbPaint( THUMB_HILITE_COLOR );
            }
        }

        public void mouseExited( PInputEvent event ) {
            if ( sliderNode.isEnabled() ) {
                sliderNode.getThumbNode().setThumbPaint( THUMB_ENABLED_COLOR );
            }
        }
    }
}
