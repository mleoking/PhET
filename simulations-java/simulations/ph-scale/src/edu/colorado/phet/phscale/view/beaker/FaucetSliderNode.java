/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * SliderNode is a Piccolo subset of JSlider.
 * Listeners are notified when the value changes.
 * The slider can be "on" only while the user is dragging it.
 * When the user releases the slider knob, it snaps to the "off" position.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaucetSliderNode extends PNode {
    
  //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // track
    private static final float TRACK_STROKE_WIDTH = 2f;
    private static final Stroke TRACK_STROKE = new BasicStroke( TRACK_STROKE_WIDTH );
    private static final Color TRACK_STROKE_COLOR = Color.BLACK;
    private static final Color TRACK_FILL_COLOR = Color.LIGHT_GRAY;
    
    // knob
    private static final int KNOB_STROKE_WIDTH = 1;
    private static final Stroke KNOB_STROKE = new BasicStroke( KNOB_STROKE_WIDTH );
    private static final Color KNOB_FILL_COLOR = new Color( 255, 255, 255, 210 );
    private static final Color KNOB_STROKE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    private final double _maxRate;
    private final PDimension _trackSize;
    private final KnobNode _knobNode;
    private double _value;
    private boolean _dragging;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FaucetSliderNode( double maxRate, PDimension trackSize, PDimension knobSize ) {
        
        _listeners = new ArrayList();
        _dragging = false;

        _maxRate = maxRate;
        _trackSize = new PDimension( trackSize );
        TrackNode trackNode = new TrackNode( trackSize );
        _knobNode = new KnobNode( knobSize );
        
        addChild( trackNode );
        addChild( _knobNode );
        
        _value = 0;
        trackNode.setOffset( 0, 0 );
        _knobNode.setOffset( 0, trackNode.getFullBoundsReference().getCenterY() + _knobNode.getFullBoundsReference().getHeight() / 2 );
        
        _knobNode.addInputEventListener( new CursorHandler() );
        
        initInteractivity();
    }
    
    /*
     * Adds interactivity to the knob.
     */
    private void initInteractivity() {
        
        // hand cursor on knob
        _knobNode.addInputEventListener( new CursorHandler() );
        
        // Constrain the knob to be dragged vertically within the track
        _knobNode.addInputEventListener( new PDragEventHandler() {
            
            private double _globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates
            
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                _dragging = true;
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( FaucetSliderNode.this );
                Point2D pMouseGlobal = FaucetSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = FaucetSliderNode.this.localToGlobal( _knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( FaucetSliderNode.this );
                Point2D pMouseGlobal = FaucetSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = FaucetSliderNode.this.globalToLocal( pKnobGlobal );
                
                // constrain the drag to the track
                double xOffset = pKnobLocal.getX();
                if ( xOffset < 0 ) {
                    xOffset = 0;
                }
                else if ( xOffset > _trackSize.getWidth() ) {
                    xOffset = _trackSize.getWidth();
                }
                
                // move the knob
                _knobNode.setOffset( xOffset, _knobNode.getYOffset() );
                
                // map the offset to a value
                double modelValue = viewToModel( xOffset );
                _value = modelValue;
                notifyValueChanged();
            }
            
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                _dragging = false;
                // snap to off position
                _knobNode.setOffset( 0, _knobNode.getYOffset() );
                // if value changed, notify
                if ( _value != 0 ) {
                    _value = 0;
                    notifyValueChanged();
                }
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getMaxRate() {
        return _maxRate;
    }
    
    public void setRate( double value ) {
        if ( value < 0 || value > _maxRate ) {
            throw new IllegalArgumentException( "value is out of range" );
        }
        if ( !_dragging && value != _value ) {
            _value = value;
            notifyValueChanged();
        }
    }
    
    public double getRate() {
        return _value;
    }
    
    public void setOff() {
        setRate( 0 );
    }
    
    public void setFullOn() {
        setRate( _maxRate );
    }
    
    public boolean isOn() {
        return ( _value == 0 );
    }
    
    public double getPercentOn() {
        return _value / _maxRate;
    }
    
    //----------------------------------------------------------------------------
    // Model-view transforms
    //----------------------------------------------------------------------------
    
    private double modelToView( double modelValue ) {
        assert( modelValue >= 0 && modelValue <= _maxRate );
        return _trackSize.getWidth() * modelValue / _maxRate;
    }
    
    private double viewToModel( double viewValue ) {
        return _maxRate * viewValue / _trackSize.getWidth();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The slider track, horizontal orientation.
     * Origin is at the upper left corner.
     */
    private static class TrackNode extends PNode {
        public TrackNode( PDimension size ) {
            super();
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
     * 
     * The slider knob, tip points down.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PNode {
        public KnobNode( PDimension size ) {

            float w = (float) size.getWidth();
            float h = (float) size.getHeight();
            GeneralPath knobPath = new GeneralPath();
            knobPath.moveTo( 0f, 0f );
            knobPath.lineTo( w / 2f, -0.35f * h );
            knobPath.lineTo( w / 2f, -h );
            knobPath.lineTo( -w / 2f, -h );
            knobPath.lineTo( -w / 2f, -0.35f * h );
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
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface FaucetSliderListener {
        public void valueChanged();
    }
    
    public void addFaucetSliderListener( FaucetSliderListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeFaucetSliderListener( FaucetSliderListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyValueChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (FaucetSliderListener) i.next() ).valueChanged();
        }
    }
    
    //----------------------------------------------------------------------------
    // Example
    //----------------------------------------------------------------------------
    
    public static void main( String args[] ) {
        
        final DecimalFormat valueFormat = new DecimalFormat( "0.00" );
        
        final double maxFlowRate = 1;
        PDimension trackSize = new PDimension( 200, 5 );
        PDimension knobSize = new PDimension( 15, 20 );
        final FaucetSliderNode sliderNode = new FaucetSliderNode( maxFlowRate, trackSize, knobSize );
        
        final PText textNode = new PText( valueFormat.format( 0 ) );
        textNode.setFont( new PhetFont( 16 ) );
        
        sliderNode.addFaucetSliderListener( new FaucetSliderListener() {
            public void valueChanged() {
                textNode.setText( valueFormat.format( sliderNode.getRate() ) );
            }
        });
        
        PCanvas canvas = new PCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        canvas.getLayer().addChild( sliderNode );
        canvas.getLayer().addChild( textNode );
        
        sliderNode.setOffset( 50, 50 );
        textNode.setOffset( sliderNode.getFullBoundsReference().getMaxX() + 15, sliderNode.getFullBoundsReference().getY() );
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( canvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
