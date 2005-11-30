/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;


/**
 * ConstrainedDragHandler
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConstrainedDragHandler extends PBasicInputEventHandler {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    /* bounds that constrain the dragging, in global coordinates */
    private Rectangle2D _dragBounds;
    /* center of the node, in the node's local coordinates */
    private Point2D _nodeCenter;
    /* where the mouse was pressed relative to the node's center */
    private Point2D _pressedOffset; 
    /* mouse position, adjusted for the pressed offset */
    private Point2D _adjustedMousePosition;
    /* is the horizontal (x) position locked? */
    private boolean _horizontalLockEnabled;
    /* is the vertical (y) position locked? */
    private boolean _verticalLockEnabled;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConstrainedDragHandler() {
        _dragBounds = new Rectangle2D.Double();
        _nodeCenter = new Point2D.Double();
        _pressedOffset = new Point2D.Double();
        _adjustedMousePosition = new Point2D.Double();
        _verticalLockEnabled = false;
        _horizontalLockEnabled = false;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setDragBounds( Rectangle2D bounds ) {
        setDragBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }
    
    public void setDragBounds( double x, double y, double w, double h ) {
        _dragBounds.setRect( x, y, w, h );
    }
    
    public void setNodeCenter( Point2D nodeCenter ) {
        setNodeCenter( nodeCenter.getX(), nodeCenter.getY() );
    }
    
    public void setNodeCenter( double x, double y ) {
        _nodeCenter.setLocation( x, y );
    }
    
    public void setVerticalLockEnabled( boolean enabled ) {
        _verticalLockEnabled = enabled;
    }
    
    public void setHorizontalLockEnabled( boolean enabled ) {
        _horizontalLockEnabled = enabled;
    }
    
    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------
    
    public void mousePressed( PInputEvent event ) {
        
        Point2D mousePosition = event.getPosition();
        PNode node = event.getPickedNode();
        Rectangle2D nodeBounds = node.getGlobalFullBounds();
        
        double x = mousePosition.getX() - nodeBounds.getX() - _nodeCenter.getX();
        double y = mousePosition.getY() - nodeBounds.getY() - _nodeCenter.getY();
        _pressedOffset.setLocation( x, y );
    }
    
    public void mouseDragged( PInputEvent event ) {
        
        Point2D mousePosition = event.getPosition();
        PNode node = event.getPickedNode();
        Rectangle2D nodeBounds = node.getGlobalFullBounds();

        /*
         * Adjust the mouse location to account for where we clicked 
         * relative to the node's center point. We want the center 
         * of the node to remain in the bounds.
         */
        double x = mousePosition.getX() + _pressedOffset.getX();
        double y = mousePosition.getY() + _pressedOffset.getY();
        _adjustedMousePosition.setLocation( x, y );
        
        // Calculate dx
        double dx = 0;
        if ( _horizontalLockEnabled ) {
            dx = 0;
        }
        else if ( _adjustedMousePosition.getX() < _dragBounds.getX() ) {
            // move to far left
            dx = _dragBounds.getX() - nodeBounds.getX() - _nodeCenter.getX();
        }
        else if ( _adjustedMousePosition.getX() > _dragBounds.getX() + _dragBounds.getWidth() ) {
            // move to far right
            dx = _dragBounds.getX() + _dragBounds.getWidth() - nodeBounds.getX() - _nodeCenter.getX();
        }
        else {
            // follow mouse
            dx = mousePosition.getX() - nodeBounds.getX() - _pressedOffset.getX() - _nodeCenter.getX();
        }

        // Calculate dy
        double dy = 0;
        if ( _verticalLockEnabled ) {
            dy = 0;
        }
        else if ( _adjustedMousePosition.getY() < _dragBounds.getY() ) {
            // move to top
            dy = _dragBounds.getY() - nodeBounds.getY() - _nodeCenter.getY();
        }
        else if ( _adjustedMousePosition.getY() > _dragBounds.getY() + _dragBounds.getHeight() ) {
            // move to bottom 
            dy = _dragBounds.getY() + _dragBounds.getHeight() - nodeBounds.getY() - _nodeCenter.getY();
        }
        else {
            // follow mouse
            dy = mousePosition.getY() - nodeBounds.getY() - _pressedOffset.getY() - _nodeCenter.getY();
        }

        // Perform the drag
        if ( dx != 0 || dy != 0 ) {
            node.translate( dx, dy );
        }
    }
}
