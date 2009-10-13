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
 * A custom Piccolo slider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class QuantitySliderNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Track
    private static final float TRACK_STROKE_WIDTH = 1f;
    private static final Stroke TRACK_STROKE = new BasicStroke( TRACK_STROKE_WIDTH );
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Color TRACK_FILL_COLOR = Color.WHITE;
    
    // Knob
    private static final int KNOB_STROKE_WIDTH = 1;
    private static final Stroke KNOB_STROKE = new BasicStroke( KNOB_STROKE_WIDTH );
    private static final Color KNOB_FILL_COLOR = new Color( 46, 107, 178 ); // blue
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    
    // Fill
    private static final Stroke FILL_STROKE = TRACK_STROKE;
    private static final Color FILL_STROKE_COLOR = TRACK_STROKE_COLOR;
    private static final Color FILL_COLOR = new Color( 165, 194, 228 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final IntegerRange range;
    private final TrackNode trackNode;
    private final KnobNode knobNode;
    private final FillNode fillNode;
    private final ArrayList<ChangeListener> listeners;
    private double value;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public QuantitySliderNode( IntegerRange range, PDimension trackSize, PDimension knobSize ) {
        super();
        
        this.range = new IntegerRange( range );
        listeners = new ArrayList<ChangeListener>();
            
        trackNode = new TrackNode( trackSize );
        knobNode = new KnobNode( knobSize );
        fillNode = new FillNode( trackSize );
        
        // Rendering order
        addChild( trackNode );
        addChild( fillNode );
        addChild( knobNode );
        
        // Positions:
        // origin at the upper-left corner of the track
        trackNode.setOffset( 0, 0 );
        // fill at same offset as track
        fillNode.setOffset( trackNode.getOffset() );
        // knob overlaps the track
        knobNode.setOffset( trackNode.getFullBoundsReference().getCenterX() - ( knobNode.getFullBoundsReference().getWidth() / 2 ), 0 ); // y offset doesn't matter yet
        // acid/base labels at top-left and bottom-left of track
        
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
                Point2D pMouseLocal = event.getPositionRelativeTo( QuantitySliderNode.this );
                Point2D pMouseGlobal = QuantitySliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = QuantitySliderNode.this.localToGlobal( knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( QuantitySliderNode.this );
                Point2D pMouseGlobal = QuantitySliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = QuantitySliderNode.this.globalToLocal( pKnobGlobal );
                
                // convert the offset to a pH value
                double yOffset = pKnobLocal.getY();
                double trackLength = trackNode.getFullBoundsReference().getHeight();
                double value = range.getMin() + range.getLength() * ( trackLength - yOffset ) / trackLength;
                
                if ( value < range.getMin() ) {
                    value = range.getMin();
                }
                else if ( value > range.getMax() ) {
                    value = range.getMax();
                }
                
                // set the current value (this will move the knob)
                setValue( value );
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the value.
     * 
     * @return double
     */
    public double getValue() {
        return value;
    }
    
    /**
     * Sets the value and notifies all ChangeListeners.
     * 
     * @param value
     */
    public void setValue( double value ) {
        if ( !range.contains( value ) ) {
            throw new IllegalArgumentException( "value is out of range: " + value );
        }
        if ( value != this.value ) {
            this.value = value;
            double xOffset = knobNode.getXOffset();
            double yOffset = trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - value ) / range.getLength() );
            knobNode.setOffset( xOffset, yOffset );
            fillNode.setFillHeight( trackNode.getFullBoundsReference().getHeight() - knobNode.getYOffset() );
            notifyChanged();
        }
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
     * The slider track, vertical orientation. 
     * Origin is at the upper left corner.
     */
    private static class TrackNode extends PNode {
        public TrackNode( PDimension size ) {
            super();
            setPickable( false );
            setChildrenPickable( false );
            
            PPath pathNode = new PPath();
            final double width = size.getWidth() - TRACK_STROKE_WIDTH;
            final double height = size.getHeight() - TRACK_STROKE_WIDTH;
            pathNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            pathNode.setPaint( TRACK_FILL_COLOR );
            pathNode.setStroke( TRACK_STROKE );
            pathNode.setStrokePaint( TRACK_STROKE_COLOR );
            addChild( pathNode );
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
    
    private static class FillNode extends PPath {

        private final PDimension maxSize;
        private final GeneralPath path;

        public FillNode( PDimension maxSize ) {
            super();
            this.maxSize = maxSize;
            path = new GeneralPath();
            setPaint( FILL_COLOR );
            setStroke( FILL_STROKE );
            setStrokePaint( FILL_STROKE_COLOR );
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
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    private void notifyChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( event );
        }
    }
}
