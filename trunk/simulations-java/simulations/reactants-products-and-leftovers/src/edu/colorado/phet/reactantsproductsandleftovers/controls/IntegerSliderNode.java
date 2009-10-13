/* Copyright 2009, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A custom Piccolo slider for integer values.
 * Vertical orientation only, no support for tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntegerSliderNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Track
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Color TRACK_BACKGROUND_COLOR = Color.WHITE;
    private static final Color TRACK_FILL_COLOR = new Color( 165, 194, 228 ); // light blue
    
    // Knob
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_FILL_COLOR = new Color( 46, 107, 178 ); // dark blue
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final IntegerRange range;
    private final TrackNode trackNode;
    private final KnobNode knobNode;
    private final ArrayList<ChangeListener> listeners;
    private int value;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IntegerSliderNode( IntegerRange range, PDimension trackSize, PDimension knobSize ) {
        super();
        
        this.range = new IntegerRange( range );
        listeners = new ArrayList<ChangeListener>();
       
        trackNode = new TrackNode( trackSize );
        knobNode = new KnobNode( knobSize );
        
        // Rendering order
        addChild( trackNode );
        addChild( knobNode );
        
        // Positions:
        // origin at the upper-left corner of the track
        trackNode.setOffset( 0, 0 );
        // knob overlaps the track
        knobNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( knobNode.getFullBoundsReference().getWidth() / 2 ), 0 ); // y offset doesn't matter yet
        
        initInteractivity();
        
        // initialize
        setValue( range.getDefault() );
    }
    
    /*
     * Adds interactivity to the knob.
     */
    private void initInteractivity() {
        
        // hand cursor on knob
        knobNode.addInputEventListener( new CursorHandler() );
        
        // Constrain the knob to be dragged vertically within the track
        knobNode.addInputEventListener( new PDragEventHandler() {
            
            private double _globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates
            
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( IntegerSliderNode.this );
                Point2D pMouseGlobal = IntegerSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = IntegerSliderNode.this.localToGlobal( knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( IntegerSliderNode.this );
                Point2D pMouseGlobal = IntegerSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = IntegerSliderNode.this.globalToLocal( pKnobGlobal );
                
                // convert the offset to a value
                double yOffset = pKnobLocal.getY();
                double trackLength = trackNode.getFullBoundsReference().getHeight();
                double value = range.getMin() + ( range.getLength() * ( trackLength - yOffset ) / trackLength );
                
                if ( value < range.getMin() ) {
                    value = range.getMin();
                }
                else if ( value > range.getMax() ) {
                    value = range.getMax();
                }
                
                // set the current value (this will move the knob)
                setValue( value );
            }
            
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                moveKnobTo( value );
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the value.
     * 
     * @return int
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Sets the value and notifies all ChangeListeners.
     * 
     * @param value
     */
    public void setValue( int value ) {
        setValue( (double) value );
    }
    
    private void setValue( double value ) {
        if ( !range.contains( value ) ) {
            throw new IllegalArgumentException( "value is out of range: " + value );
        }
        if ( value != this.value ) {
            this.value = (int) value;
            moveKnobTo( value );
            trackNode.setFillHeight( trackNode.getFullBoundsReference().getHeight() - knobNode.getYOffset() );
            fireStateChanged();
        }
    }
    
    private void moveKnobTo( double value ) {
        double xOffset = knobNode.getXOffset();
        double yOffset = trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - value ) / range.getLength() );
        knobNode.setOffset( xOffset, yOffset );
    }
    
    /**
     * Enables and disables the slider.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        knobNode.setVisible( enabled );
    }

    /**
     * Is this slider enabled?
     * It's enabled if the knob is visible.
     * 
     * @return
     */
    public boolean isEnabled() {
        return knobNode.getVisible();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The slider track.
     */
    private static class TrackNode extends PNode {
        
        private final TrackFillNode fillNode;
        
        public TrackNode( PDimension size ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            // components
            PNode backgroundNode = new TrackBackgroundNode( size );
            fillNode = new TrackFillNode( size );
            PNode strokeNode = new TrackStrokeNode( size );
            
            // rendering order
            addChild( backgroundNode );
            addChild( fillNode );
            addChild( strokeNode );
        }
        
        public void setFillHeight( double height ) {
            fillNode.setFillHeight( height );
        }
    }
    
    /*
     * Stroke that goes around the track.
     * Origin is at the upper left corner.
     */
    private static class TrackStrokeNode extends PPath {
        
        public TrackStrokeNode( PDimension size ) {
            super( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            setStroke( TRACK_STROKE );
            setStrokePaint( TRACK_STROKE_COLOR );
            setPaint( null );
        }
    }
    
    /*
     * The background part of the slider track. 
     * Origin is at the upper left corner.
     */
    private static class TrackBackgroundNode extends PNode {
        
        public TrackBackgroundNode( PDimension size ) {
            super();
            PPath pathNode = new PPath();
            pathNode.setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            pathNode.setPaint( TRACK_BACKGROUND_COLOR );
            pathNode.setStroke( null );
            addChild( pathNode );
        }
    }
    
    /*
     * The portion of the track below the knob, filled with a color.
     */
    private static class TrackFillNode extends PPath {

        private final PDimension maxSize;
        private final GeneralPath path;

        public TrackFillNode( PDimension maxSize ) {
            super();
            this.maxSize = maxSize;
            path = new GeneralPath();
            setPaint( TRACK_FILL_COLOR );
            setStroke( null );
        }
        
        public void setFillHeight( double height ) {
            path.reset();
            path.moveTo( 0f, (float) maxSize.getHeight() );
            path.lineTo( 0f, (float) ( maxSize.getHeight() - height ) );
            path.lineTo( (float) maxSize.getWidth(), (float) ( maxSize.getHeight() - height ) );
            path.lineTo( (float) maxSize.getWidth(), (float) maxSize.getHeight() );
            path.closePath();
            setPathTo( path );
        }
    }
    
    /*
     * The slider knob, points to the left.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PNode {
        
        public KnobNode( PDimension size ) {

            float w = (float) size.getWidth();
            float h = (float) size.getHeight();
            GeneralPath knobPath = new GeneralPath();
            knobPath.moveTo( 0f, 0f );
            knobPath.lineTo( 0.35f * w, h / 2f );
            knobPath.lineTo( w, h / 2f );
            knobPath.lineTo( w, -h / 2f );
            knobPath.lineTo( 0.35f * w, -h / 2f );
            knobPath.closePath();

            PPath pathNode = new PPath();
            pathNode.setPathTo( knobPath );
            pathNode.setPaint( KNOB_FILL_COLOR );
            pathNode.setStroke( KNOB_STROKE );
            pathNode.setStrokePaint( KNOB_STROKE_COLOR );
            addChild( pathNode );
        }
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( event );
        }
    }
}
