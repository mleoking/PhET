/* Copyright 2009, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A custom Piccolo slider for integer values.
 * Vertical orientation only, no support for tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntegerSliderNode extends PNode {
    
    //XXX decide on 2D or 3D knob, then delete the other
    private static final boolean USE_3D_KNOB = true;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Knob
    private static final Stroke KNOB_STROKE = new BasicStroke( 1f );
    private static final Color KNOB_FILL_COLOR = new Color( 46, 107, 178 ); // dark blue
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final IntegerRange range;
    private final IntegerHistogramBarNode trackNode;
    private final PNode knobNode;
    private final ArrayList<ChangeListener> listeners;
    private int value;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IntegerSliderNode( IntegerRange range, PDimension trackSize, PDimension knobSize ) {
        super();
        
        this.range = new IntegerRange( range );
        listeners = new ArrayList<ChangeListener>();
       
        trackNode = new IntegerHistogramBarNode( range, trackSize );
        if ( USE_3D_KNOB ) {
            knobNode = new KnobNode3D( knobSize );
        }
        else {
            knobNode = new KnobNode2D( knobSize );
        }
        
        // Rendering order
        addChild( trackNode );
        addChild( knobNode );
        
        // Positions:
        // origin at the upper-left corner of the track
        trackNode.setOffset( 0, 0 );
        // knob to right of track
        knobNode.setOffset( trackNode.getFullBoundsReference().getMaxX(), 0 ); // y offset doesn't matter yet
        
        initInteractivity();
        
        // initialize
        value = range.getMax() + 1; // force an update
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
    public void setValue( double value ) {
        if ( !range.contains( value ) ) {
            throw new IllegalArgumentException( "value is out of range: " + value );
        }
        if ( value != this.value ) {
            this.value = (int) value;
            moveKnobTo( value );
            trackNode.setValue( value );
            fireStateChanged();
        }
    }
    
    private void moveKnobTo( double value ) {
        double xOffset = knobNode.getXOffset();
        double yOffset = trackNode.getFullBoundsReference().getHeight() * ( ( range.getMax() - value ) / range.getLength() );
        knobNode.setOffset( xOffset, yOffset );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The slider knob, points to the left.
     * Origin is at the knob's tip.
     */
    private static class KnobNode2D extends PNode {
        
        public KnobNode2D( PDimension size ) {

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

    private static class KnobNode3D extends PhetPNode {

        public KnobNode3D( PDimension knobSize ) {
            super();
            
            PImage image = new PImage( RPALImages.SLIDER_KNOB );
            addChild( image );
            
            double xScale = image.getFullBoundsReference().getWidth() / knobSize.getWidth();
            double yScale = image.getFullBoundsReference().getHeight() / knobSize.getHeight();
            image.getTransform().scale( xScale, yScale );
            
            image.setOffset( 0, -image.getFullBoundsReference().getHeight() / 2 );
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
