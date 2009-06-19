
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.control.IScalarTransform.LogLinearTransform;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Strength slider with 3 log zones: weak, intermediate, strong.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StrengthSliderNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Thumb
    private static final PDimension THUMB_SIZE = new PDimension( 13, 18 );
    private static final Color THUMB_ENABLED_COLOR = ABSColors.THUMB_ENABLED;
    private static final Color THUMB_HILITE_COLOR = ABSColors.THUMB_HIGHLIGHTED;
    private static final Color THUMB_DISABLED_COLOR = new Color( 245, 245, 245 );
    private static final Color THUMB_STROKE_COLOR = Color.BLACK;
    private static final Stroke THUMB_STROKE = new BasicStroke( 1f );

    // Track
    private static final Color TRACK_WEAK_COLOR = Color.WHITE;
    private static final Color TRACK_INTERMEDIATE_COLOR = Color.LIGHT_GRAY;
    private static final Color TRACK_STRONG_COLOR = Color.GRAY;
    private static final double TRACK_WEAK_WIDTH = 200;
    private static final double TRACK_INTERMEDIATE_WIDTH = 30;
    private static final double TRACK_STRONG_WIDTH = 32;
    private static final double TRACK_WIDTH = TRACK_WEAK_WIDTH + TRACK_INTERMEDIATE_WIDTH + TRACK_STRONG_WIDTH;
    private static final double TRACK_HEIGHT = 10;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;

    // Minor ticks
    private static final double MINOR_TICK_LENGTH = TRACK_HEIGHT;
    private static final Color MINOR_TICK_COLOR = Color.GRAY;
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final double MINOR_TICKS_CLOSEST_X_SPACING = 2;
    private static final double MINOR_TICK_X_SPACING_MULTIPLIER = 1.5;
    
    // Range indicators
    private static final Font RANGE_LABEL_FONT = new PhetFont( 12 );
    private static final Color RANGE_LABEL_TEXT_COLOR = Color.BLACK;
    private static final Color RANGE_LABEL_STROKE_COLOR = Color.BLACK;
    private static final Stroke RANGE_LABEL_STROKE = new BasicStroke( 1f );
    private static final double RANGE_LABEL_HEIGHT = 10;
    private static final Font RANGE_MIN_MAX_FONT = new PhetFont( 10 );
    private static final Color RANGE_MIN_MAX_COLOR = Color.BLACK;
    private static final double RANGE_MIN_MAX_X_SPACING = 4;
    private static final double RANGE_MIN_MAX_Y_SPACING = 2;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final DoubleRange weakRange, strongRange;
    private final ArrayList<ChangeListener> changeListeners;
    private double value;
    private final TrackNode trackNode;
    private final SliderThumbArrowNode thumbNode;
    private final IScalarTransform weakTransform, intermediateTransform, strongTransform;
    private boolean enabled;
    private final CursorHandler thumbCursorHandler;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public StrengthSliderNode( DoubleRange weakRange, DoubleRange strongRange ) {
        super();
        assert( weakRange.getMax() < strongRange.getMin() ); // intermediate range is between weak and strong

        this.weakRange = weakRange;
        this.strongRange = strongRange;
        value = weakRange.getMin();
        changeListeners = new ArrayList<ChangeListener>();
        enabled = true;
        
        // scalar transforms
        weakTransform = new LogLinearTransform( weakRange.getMin(), weakRange.getMax(), 0, TRACK_WEAK_WIDTH );
        intermediateTransform = new LogLinearTransform( weakRange.getMax(), strongRange.getMin(), TRACK_WEAK_WIDTH, TRACK_WEAK_WIDTH + TRACK_INTERMEDIATE_WIDTH );
        strongTransform = new LogLinearTransform( strongRange.getMin(), strongRange.getMax(), TRACK_WEAK_WIDTH + TRACK_INTERMEDIATE_WIDTH, TRACK_WIDTH );

        // track
        trackNode = new TrackNode();
        addChild( trackNode );
        trackNode.setOffset( 0, 0 );
        trackNode.addInputEventListener( new TrackClickHandler( this ) );
        
        // weak range label
        PNode weakRangeLabelNode = new RangeLabelNode( TRACK_WEAK_WIDTH, ABSStrings.LABEL_WEAK, ABSStrings.LABEL_WEAKER, ABSStrings.LABEL_STRONGER );
        addChild( weakRangeLabelNode );
        double xOffset = 0;
        double yOffset = ( weakRangeLabelNode.getYOffset() - weakRangeLabelNode.getY() ) - 20;
        weakRangeLabelNode.setOffset( xOffset, yOffset );
        
        // strong range label
        PNode strongRangeLabelNode = new RangeLabelNode( TRACK_STRONG_WIDTH, ABSStrings.LABEL_STRONG );
        addChild( strongRangeLabelNode );
        xOffset = TRACK_WEAK_WIDTH + TRACK_INTERMEDIATE_WIDTH;
        yOffset = ( strongRangeLabelNode.getYOffset() - strongRangeLabelNode.getY() ) - 20;
        strongRangeLabelNode.setOffset( xOffset, yOffset );
        
        //  minor ticks, intermediate track
        double dx = MINOR_TICKS_CLOSEST_X_SPACING;
        xOffset = TRACK_WEAK_WIDTH + TRACK_INTERMEDIATE_WIDTH - dx;
        yOffset = 0;
        while ( xOffset - dx > TRACK_WEAK_WIDTH ) {
            MinorTickNode minorTickNode = new MinorTickNode();
            addChild( minorTickNode );
            minorTickNode.setOffset( xOffset, yOffset );
            dx *= MINOR_TICK_X_SPACING_MULTIPLIER;
            xOffset = xOffset - dx;
        }
        
        //  minor ticks, weak track
        dx = MINOR_TICKS_CLOSEST_X_SPACING;
        xOffset = TRACK_WEAK_WIDTH - dx;
        yOffset = 0;
        while ( xOffset > 0 ) {
            MinorTickNode minorTickNode = new MinorTickNode();
            addChild( minorTickNode );
            minorTickNode.setOffset( xOffset, yOffset );
            dx *= MINOR_TICK_X_SPACING_MULTIPLIER;
            xOffset = xOffset - dx;
        }

        // thumb
        thumbCursorHandler = new CursorHandler();
        thumbNode = new SliderThumbArrowNode( THUMB_SIZE, THUMB_ENABLED_COLOR, THUMB_STROKE_COLOR, THUMB_STROKE );
        addChild( thumbNode );
        thumbNode.setOffset( 0, trackNode.getFullBoundsReference().getCenterY() );
        thumbNode.addInputEventListener( thumbCursorHandler );
        thumbNode.addInputEventListener( new ThumbDragHandler( this ) );
        thumbNode.addInputEventListener( new HilightHandler( this ) );
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

    public void setValue( double value ) {
        if ( !( value >= getMin() && value <= getMax() ) ) {
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
    
    private void updateThumb() {
        double xOffset = modelToView( value );
        double yOffset = trackNode.getFullBoundsReference().getCenterY();
        thumbNode.setOffset( xOffset, yOffset );
    }
    
    protected double getMin() {
        return weakRange.getMin();
    }
    
    protected double getMax() {
        return strongRange.getMax();
    }
    
    protected SliderThumbArrowNode getThumbNode() {
        return thumbNode;
    }
    
    protected IScalarTransform getWeakTransform() {
        return weakTransform;
    }
    
    protected IScalarTransform getIntermediateTransform() {
        return intermediateTransform;
    }
    
    protected IScalarTransform getStrongTransform() {
        return strongTransform;
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

    protected double viewToModel( double viewValue ) {
        double modelValue = 0;
        if ( viewValue <= TRACK_WEAK_WIDTH ) {
            // weak
            modelValue = getWeakTransform().viewToModel( viewValue );
        }
        else if ( viewValue <= TRACK_WEAK_WIDTH + TRACK_INTERMEDIATE_WIDTH ) {
            // intermediate
            modelValue = getIntermediateTransform().viewToModel( viewValue );
        }
        else { 
            // strong
            modelValue = getStrongTransform().viewToModel( viewValue );
        }
        
        // adjust
        if ( modelValue < getMin() ) {
            modelValue = getMin();
        }
        else if ( modelValue > getMax() ) {
            modelValue = getMax();
        }
        
        return modelValue;
    }
    
    private double modelToView( double modelValue ) {
        double viewValue = 0;
        if ( modelValue < weakRange.getMax() ) {
            // weak
            viewValue = getWeakTransform().modelToView( modelValue );
        }
        else if ( modelValue < strongRange.getMin() ) {
            // intermediate
            viewValue = getIntermediateTransform().modelToView( modelValue );
        }
        else { 
            // strong 
            viewValue = getStrongTransform().modelToView( modelValue );
        }
        return viewValue;
    }
    
    /*
     * Minor ticks are vertical lines that appear in the track.
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
     * The track is a rectangular region, oriented horizontally.
     * Origin is at the upper-left corner.
     * The track is interactive.
     */
    private static class TrackNode extends PPath {

        public TrackNode() {
            super();

            // weak segment
            PPath weakTrackNode = new PPath();
            weakTrackNode.setPathTo( new Rectangle2D.Double( 0, 0, TRACK_WEAK_WIDTH, TRACK_HEIGHT ) );
            weakTrackNode.setPaint( TRACK_WEAK_COLOR );
            weakTrackNode.setStroke( TRACK_STROKE );
            weakTrackNode.setStrokePaint( TRACK_STROKE_COLOR );
            addChild( weakTrackNode );

            // intermediate segment
            PPath intermediateTrackNode = new PPath();
            intermediateTrackNode.setPathTo( new Rectangle2D.Double( 0, 0, TRACK_INTERMEDIATE_WIDTH, TRACK_HEIGHT ) );
            intermediateTrackNode.setPaint( TRACK_INTERMEDIATE_COLOR );
            intermediateTrackNode.setStroke( TRACK_STROKE );
            intermediateTrackNode.setStrokePaint( TRACK_STROKE_COLOR );
            addChild( intermediateTrackNode );

            // strong segment
            PPath strongTrackNode = new PPath();
            strongTrackNode.setPathTo( new Rectangle2D.Double( 0, 0, TRACK_STRONG_WIDTH, TRACK_HEIGHT ) );
            strongTrackNode.setPaint( TRACK_STRONG_COLOR );
            strongTrackNode.setStroke( TRACK_STROKE );
            strongTrackNode.setStrokePaint( TRACK_STROKE_COLOR );
            addChild( strongTrackNode );
            
            // layout
            weakTrackNode.setOffset( 0, 0 );
            double xOffset = TRACK_WEAK_WIDTH;
            double yOffset = 0;
            intermediateTrackNode.setOffset( xOffset, yOffset );
            xOffset = TRACK_WEAK_WIDTH + TRACK_INTERMEDIATE_WIDTH;
            strongTrackNode.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * Label a range of the track, with optional min/max labels.
     * Origin at upper-left of the line.
     */
    private static class RangeLabelNode extends PComposite {
        
        public RangeLabelNode( double length, String label ) {
            this( length, label, "", "" );
        }
        
        public RangeLabelNode( double length, String label, String minLabel, String maxLabel ) {
            super();
            // not interactive
            setPickable( false );
            setChildrenPickable( false );
            
            // label
            PText labelNode = new PText( label );
            labelNode.setFont( RANGE_LABEL_FONT );
            labelNode.setTextPaint( RANGE_LABEL_TEXT_COLOR );
            addChild( labelNode );
            
            // indicator shape
            GeneralPath path = new GeneralPath();
            float w = (float) length;
            float h = (float) RANGE_LABEL_HEIGHT;
            path.moveTo( 0, h );
            path.lineTo( 0, 0 );
            path.lineTo( w, 0 );
            path.lineTo( w, h );
            PPath shapeNode = new PPath( path );
            addChild( shapeNode );
            shapeNode.setStroke( RANGE_LABEL_STROKE );
            shapeNode.setStrokePaint( RANGE_LABEL_STROKE_COLOR );
            
            // min label
            PText minLabelNode = new PText( minLabel );
            minLabelNode.setFont( RANGE_MIN_MAX_FONT );
            minLabelNode.setTextPaint( RANGE_MIN_MAX_COLOR );
            addChild( minLabelNode );
            
            // max label
            PText maxLabelNode = new PText( maxLabel );
            maxLabelNode.setFont( RANGE_MIN_MAX_FONT );
            maxLabelNode.setTextPaint( RANGE_MIN_MAX_COLOR );
            addChild( maxLabelNode );
            
            // layout
            shapeNode.setOffset( 0, 0 );
            double xOffset = ( shapeNode.getFullBoundsReference().getWidth() - labelNode.getFullBoundsReference().getWidth() ) / 2;
            double yOffset = -( labelNode.getFullBoundsReference().getHeight() + 2 );
            labelNode.setOffset( xOffset, yOffset );
            xOffset = RANGE_MIN_MAX_X_SPACING;
            yOffset = RANGE_MIN_MAX_Y_SPACING;
            minLabelNode.setOffset( xOffset, yOffset );
            xOffset = length - maxLabelNode.getFullBoundsReference().getWidth() - RANGE_MIN_MAX_X_SPACING;
            yOffset = RANGE_MIN_MAX_Y_SPACING;
            maxLabelNode.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * Dragging the thumb changes the slider value.
     */
    private static class ThumbDragHandler extends PDragEventHandler {

        private final StrengthSliderNode sliderNode;
        private double _globalClickXOffset; // X offset of mouse click from knob's origin, in global coordinates

        public ThumbDragHandler( StrengthSliderNode sliderNode ) {
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
                final double xOffset = pThumbLocal.getX();

                // transform offset to a slider value
                double value = sliderNode.viewToModel( xOffset );
                sliderNode.setValue( value );
            }
        }
    }
    
    /*
     * Clicking in the slider track changes the slider value.
     */
    private static class TrackClickHandler extends PBasicInputEventHandler {
        
        private final StrengthSliderNode sliderNode;
        
        public TrackClickHandler( StrengthSliderNode sliderNode ) {
            super();
            this.sliderNode = sliderNode;
        }
        
        public void mousePressed( PInputEvent event ) {
            if ( sliderNode.isEnabled() ) {
                // determine the offset of the mouse click
                Point2D pMouseLocal = event.getPositionRelativeTo( sliderNode );
                Point2D pMouseGlobal = sliderNode.localToGlobal( pMouseLocal );
                Point2D pTrackLocal = sliderNode.globalToLocal( pMouseGlobal );
                final double xOffset = pTrackLocal.getX();

                // transform offset to a slider value
                double value = sliderNode.viewToModel( xOffset );
                sliderNode.setValue( value );
            }
        }
    }
    
    public static class HilightHandler extends PBasicInputEventHandler {

        private final StrengthSliderNode sliderNode;

        public HilightHandler( StrengthSliderNode sliderNode ) {
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
