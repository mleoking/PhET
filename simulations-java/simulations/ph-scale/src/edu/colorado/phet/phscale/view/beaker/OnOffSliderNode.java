/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * OnOffSliderNode is a slider with two states: on & off.
 * Listeners are notified when the state changes.
 * The slider can be "on" only while the user is dragging it.
 * When the user releases the slider knob, it snaps to the "off" position.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OnOffSliderNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    private boolean _on;
    private final PDimension _trackSize;
    private final TrackNode _trackNode;
    private final KnobNode _knobNode;
    private boolean _dragging;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OnOffSliderNode( PDimension trackSize, Paint trackFillPaint, Paint trackStrokePaint, float trackStrokeWidth, 
            PDimension knobSize, Paint knobFillPaint, Paint knobStrokePaint, float knobStrokeWidth ) {
        
        _listeners = new ArrayList();
        _on = false;
        _trackSize = new PDimension( trackSize );
        _trackNode = new TrackNode( trackSize, trackFillPaint, trackStrokePaint, trackStrokeWidth );
        _knobNode = new KnobNode( knobSize, knobFillPaint, knobStrokePaint, knobStrokeWidth );
        _dragging = false;
        
        addChild( _trackNode );
        addChild( _knobNode );
        
        _trackNode.setOffset( 0, 0 );
        _knobNode.setOffset( 0, _trackNode.getFullBoundsReference().getCenterY() + _knobNode.getFullBoundsReference().getHeight() / 2 );
        
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
                Point2D pMouseLocal = event.getPositionRelativeTo( OnOffSliderNode.this );
                Point2D pMouseGlobal = OnOffSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = OnOffSliderNode.this.localToGlobal( _knobNode.getOffset() );
                _globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( OnOffSliderNode.this );
                Point2D pMouseGlobal = OnOffSliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - _globalClickYOffset );
                Point2D pKnobLocal = OnOffSliderNode.this.globalToLocal( pKnobGlobal );
                
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
                
                // if state changed, notify listeners
                if ( ( xOffset == 0 && _on == true ) || ( xOffset != 0 && _on == false ) ) {
                    _on = !_on;
                    notifyOnOffChanged();
                }
            }
            
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                _dragging = false;
                // snap to off position
                _knobNode.setOffset( 0, _knobNode.getYOffset() );
                // if state changed, notify listeners
                if ( _on  ) {
                    _on = false;
                    notifyOnOffChanged();
                }
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Set the slider to on or off state.
     * If the user is dragging the slider, or if the slider is already in
     * the specified state, this method does nothing.
     * 
     * @param on true=on, false=off
     */
    public void setOn( boolean on ) {
        if ( on != _on && !_dragging ) {
            _on = on;
            if ( on ) {
                _knobNode.setOffset( _trackSize.getWidth(), _knobNode.getYOffset() );
            }
            else {
                _knobNode.setOffset( 0, _knobNode.getYOffset() );
            }
            notifyOnOffChanged();
        }
    }
    
    /**
     * Is the slider in the "on" state?
     * 
     * @return true or false
     */
    public boolean isOn() {
        return _on;
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The slider track, horizontal orientation.
     * Origin is at the upper left corner.
     */
    private static class TrackNode extends PNode {
        public TrackNode( PDimension size, Paint fillPaint, Paint strokePaint, float strokeWidth ) {
            super();
            PPath pathNode = new PPath();
            final double width = size.getWidth() - strokeWidth;
            final double height = size.getHeight() - strokeWidth;
            pathNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            pathNode.setPaint( fillPaint );
            if ( strokeWidth > 0 ) {
                pathNode.setStrokePaint( strokePaint );
                pathNode.setStroke( new BasicStroke( strokeWidth ) );
            }
            else {
                pathNode.setStrokePaint( null );
                pathNode.setStroke( null );
            }
            addChild( pathNode );
        }
    }
    /*
     * 
     * The slider knob, tip points down.
     * Origin is at the knob's tip.
     */
    private static class KnobNode extends PNode {
        public KnobNode( PDimension size, Paint fillPaint, Paint strokePaint, float strokeWidth ) {

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
            pathNode.setPaint( fillPaint );
            if ( strokeWidth > 0 ) {
                pathNode.setStrokePaint( strokePaint );
                pathNode.setStroke( new BasicStroke( strokeWidth ) );
            }
            else {
                pathNode.setStrokePaint( null );
                pathNode.setStroke( null );
            }
            addChild( pathNode );
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface MomentarySliderListener {
        public void onOffChanged( boolean on );
    }
    
    public void addMomentarySliderListener( MomentarySliderListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeMomentarySliderListener( MomentarySliderListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyOnOffChanged() {
        final boolean on = isOn();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MomentarySliderListener) i.next() ).onOffChanged( on );
        }
    }
    
    //----------------------------------------------------------------------------
    // Example
    //----------------------------------------------------------------------------
    
    public static void main( String args[] ) {
        
        // track
        Color trackFillColor = Color.LIGHT_GRAY;
        Color trackStrokeColor = Color.BLACK;
        float trackStrokeWidth = 2f;
        
        // knob
        Color knobFillColor = new Color( 255, 255, 255, 210 );
        Color knobStrokeColor = Color.BLACK;
        int knobStrokeWidth = 1;
        
        PDimension trackSize = new PDimension( 200, 8 );
        PDimension knobSize = new PDimension( 15, 20 );
        final OnOffSliderNode sliderNode = new OnOffSliderNode( 
                trackSize, trackFillColor, trackStrokeColor, trackStrokeWidth,
                knobSize, knobFillColor, knobStrokeColor, knobStrokeWidth );
        
        final PPath onOffNode = new PPath( new Rectangle2D.Double( 0, 0, 50, 50 ) );
        onOffNode.setPaint( sliderNode.isOn() ? Color.GREEN : Color.RED );
        
        sliderNode.addMomentarySliderListener( new MomentarySliderListener() {
            public void onOffChanged( boolean on ) {
                onOffNode.setPaint( sliderNode.isOn() ? Color.GREEN : Color.RED );
            }
        });
        
        PCanvas canvas = new PCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        canvas.getLayer().addChild( sliderNode );
        canvas.getLayer().addChild( onOffNode );
        
        sliderNode.setOffset( 50, 50 );
        onOffNode.setOffset( 50, 150 );
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( canvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
