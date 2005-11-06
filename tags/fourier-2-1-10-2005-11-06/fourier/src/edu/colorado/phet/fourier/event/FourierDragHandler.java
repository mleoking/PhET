/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.event;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;


/**
 * FourierDragHandler supports unconstrained dragging of a PhetGraphic
 * within the apparatus panel boundaries.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierDragHandler extends MouseInputAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetGraphic _graphic;
    private boolean _dragEnabled;
    private Point _previousPoint;
    private Rectangle _dragBounds;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FourierDragHandler( PhetGraphic graphic ) {
        super();
        assert( graphic != null );
        _graphic = graphic; 
        _dragEnabled = true;
        _previousPoint = new Point();
        _dragBounds = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Set the drag bounds. 
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setDragBounds( int x, int y, int width, int height ) {
        if ( _dragBounds == null ) {
            _dragBounds = new Rectangle();
        }
        _dragBounds.setBounds( x, y, width, height );
    }
    
    /**
     * Sets the drag bounds.
     * 
     * @param r
     */
    public void setDragBounds( Rectangle r ) {
        setDragBounds( r.x, r.y, r.width, r.height );
    }

    //----------------------------------------------------------------------------
    // MouseInputAdapter overrides
    //----------------------------------------------------------------------------
    
    /**
     * Handles mouse pressed events.
     * 
     * @param event
     */
    public void mousePressed( MouseEvent event ) {
        _dragEnabled = true;
        _previousPoint.setLocation( event.getPoint() );
    }
    
    /**
     * Handles mouse dragged events.
     * Drag boundary checking is enabled if drag boundaries have been provided.
     * If the object is outside the drag boundaries, then the drag is vetoed.
     * Otherwise the graphic's location is updated.
     */
    public void mouseDragged( MouseEvent event ) {

        if ( !_dragEnabled && _graphic.contains( event.getX(), event.getY() ) ) {
            // Re-enable dragging when the mouse cursor is inside the object.
            _dragEnabled = true;
            _previousPoint.setLocation( event.getPoint() );
        }
        
        if ( _dragEnabled ) {

            int dx = event.getX() - _previousPoint.x;
            int dy = event.getY() - _previousPoint.y;
            
            boolean outOfBounds = false;
            if ( _dragBounds != null ) {
                // Is the mouse cursor outside the drag bounds?
                outOfBounds = !_dragBounds.contains( event.getPoint() );
            }

            if ( outOfBounds ) {
                // Ignore the translate if the mouse is outside the apparatus panel or 
                // if the translate would result in a collision.
                _dragEnabled = false;
            }
            else {
                // Translate the view.
                int x = _graphic.getX() + dx;
                int y = _graphic.getY() + dy;
                _graphic.setLocation( x, y );
                _previousPoint.setLocation( event.getPoint() );
            }
        }
    }
}
