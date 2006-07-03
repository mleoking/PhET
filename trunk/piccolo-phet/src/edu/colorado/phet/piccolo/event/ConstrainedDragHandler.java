/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.event;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;


/**
 * ConstrainedDragHandler constrains drags to a specified bounding box.
 * The node being dragged can be treated as either a point or a rectangle.
 * When treated as a point, the center point of the node can be specified.
 * <p>
 * This node does not mark events as handled unless you 
 * request that behavior via setMarkAsHandledEnabled.
 * <p>
 * NOTE: This handler does not work for nodes that have been rotated!
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
    /* where the mouse was pressed relative to the node's upper left corner */
    private Point2D _pressedOffset;
    /* mouse position, adjusted for _nodeCenter and _pressedOffset */
    private Point2D _adjustedMousePosition;
    /* is the horizontal (x) position locked? */
    private boolean _horizontalLockEnabled;
    /* is the vertical (y) position locked? */
    private boolean _verticalLockEnabled;
    /* should we treat the node as a point? */
    private boolean _treatAsPointEnabled;
    /* should we mark events as handled? */
    private boolean _markAsHandled;
    /* should we constrain dragging using full bounds? */
    private boolean _useFullBounds;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a handler with empty drag bounds.
     */
    public ConstrainedDragHandler() {
        this( null );
    }
    
    /**
     * Creates a handler with specified drag bounds.
     * 
     * @param dragBounds
     */
    public ConstrainedDragHandler( Rectangle2D dragBounds ) {
        _dragBounds = new Rectangle2D.Double();
        if ( dragBounds != null ) {
            _dragBounds.setRect( dragBounds );
        }
        _nodeCenter = new Point2D.Double();
        _pressedOffset = new Point2D.Double();
        _adjustedMousePosition = new Point2D.Double();
        _verticalLockEnabled = false;
        _horizontalLockEnabled = false;
        _treatAsPointEnabled = false;
        _markAsHandled = false;
        _useFullBounds = true;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the drag bounds.
     * 
     * @param bounds
     */
    public void setDragBounds( Rectangle2D bounds ) {
        setDragBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }
    
    /**
     * Sets the drag bounds.
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void setDragBounds( double x, double y, double w, double h ) {
        _dragBounds.setRect( x, y, w, h );
    }
    
    /**
     * Returns a copy of the drag bounds.
     * 
     * @return copy of the drag bounds
     */
    public Rectangle2D getDragBounds() {
        return new Rectangle2D.Double( _dragBounds.getX(), _dragBounds.getY(), _dragBounds.getWidth(), _dragBounds.getHeight() );
    }
    
    /**
     * Returns a direct reference to the drag bounds.
     * The value returned should not be modified.
     * 
     * @return reference to the drag bounds
     */
    public Rectangle2D getDragBoundsReference() {
        return _dragBounds;
    }
    
    /**
     * Specified the location of the node's center point,
     * relative to the upper left corner of the node's bounds.
     * 
     * @param nodeCenter
     */
    public void setNodeCenter( Point2D nodeCenter ) {
        setNodeCenter( nodeCenter.getX(), nodeCenter.getY() );
    }
    
    /**
     * Specifies the node's center.
     * 
     * @param x
     * @param y
     */
    public void setNodeCenter( double x, double y ) {
        _nodeCenter.setLocation( x, y );
    }
    
    /**
     * Gets a copy of the node's center.
     * 
     * @return copy of the node's center
     */
    public Point2D getNodeCenter() {
        return new Point2D.Double( _nodeCenter.getX(), _nodeCenter.getY() );
    }
    
    /**
     * Returns a direct reference to the node's center.
     * The value returned should not be modified.
     * 
     * @return reference to node's center
     */
    public Point2D getNodeCenterReference() {
        return _nodeCenter;
    }
    
    /**
     * Determines whether horizontal dragging is locked.
     * When locked, dragging does not change the horizontal position.
     * 
     * @param enabled true or false
     */
    public void setHorizontalLockEnabled( boolean enabled ) {
        _horizontalLockEnabled = enabled;
    }
    
    /**
     * Is horizontal dragging locked?
     * 
     * @return true or false
     */
    public boolean isHorizontalLockEnabled() {
        return _horizontalLockEnabled;
    }
    
    /**
     * Determines whether vertical dragging is locked.
     * When locked, dragging does not change the vertical position.
     * 
     * @param enabled true or false
     */
    public void setVerticalLockEnabled( boolean enabled ) {
        _verticalLockEnabled = enabled;
    }
    
    /**
     * Is vertical dragging locked?
     * 
     * @return true or false
     */
    public boolean isVerticalLockEnabled() {
        return _verticalLockEnabled;
    }
    
    /**
     * By default, the entire node must be inside the drag bounds.
     * If you prefer to constrain only the node's center point,
     * then call this method with true.
     * 
     * @param treatAsPoint true or false
     */
    public void setTreatAsPointEnabled( boolean treatAsPoint ) {
        _treatAsPointEnabled = treatAsPoint;
    }
    
    /**
     * Is the node being treated as a point?
     * 
     * @return true or false
     */
    public boolean isTreatAsPointEnabled() {
        return _treatAsPointEnabled;
    }
    
    /**
     * Determines whether processed events will be marked as handled.
     * 
     * @param flag true or false
     */
    public void setMarkAsHandled( boolean flag ) {
        _markAsHandled = flag;
    }
    
    /**
     * Will processed events be marked as handled?
     * 
     * @return true or false
     */
    public boolean getMarkAsHandled() {
        return _markAsHandled;
    }
    
    /**
     * Determines whether we contrain dragging based on the "bounds"
     * or "full bounds" of the node. Full bounds includes the bounds
     * of all children, and is the default behavior.
     * 
     * @param useFullBounds true or false
     */
    public void setUseFullBounds( boolean useFullBounds ) {
        _useFullBounds = useFullBounds;
    }
    
    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------
    
    /*
     * When the mouse is pressed, remember where we pressed
     * relative to the node's upper left corner.
     */
    public void mousePressed( PInputEvent event ) {
        
        Point2D mousePosition = event.getPosition();
        PNode node = event.getPickedNode();
        Rectangle2D nodeBounds = getNodeBounds( node );
        
        // Determine where we pressed relative to the node's upper left corner.
        double x = mousePosition.getX() - nodeBounds.getX();
        double y = mousePosition.getY() - nodeBounds.getY();
        _pressedOffset.setLocation( x, y );
        
        event.setHandled( _markAsHandled );
    }
    
    /*
     * When the mouse is dragged, constrain the drag.
     */
    public void mouseDragged( PInputEvent event ) {
        if ( ! _dragBounds.isEmpty() ) {
            if ( _treatAsPointEnabled ) {
                // treating the node as a point...
                dragConstrainPoint( event );
            }
            else {
                // treating the node as a rectangle...
                dragConstrainBounds( event );
            }
        }
        event.setHandled( _markAsHandled );
    }
        
    /*
     * Constrains the drag.
     * Center point of the node must be contained within the drag bounds.
     */
    private void dragConstrainPoint( PInputEvent event ) {
            
        Point2D mousePosition = event.getPosition();
        PNode node = event.getPickedNode();
        Rectangle2D nodeBounds = getNodeBounds( node );

        /*
         * Adjust the mouse location to account for where we clicked 
         * relative to the node's center point. We want the center 
         * of the node to remain in the bounds.
         */
        double x = mousePosition.getX() - _pressedOffset.getX() + _nodeCenter.getX();
        double y = mousePosition.getY() - _pressedOffset.getY() + _nodeCenter.getY();
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
            dx = ( _dragBounds.getX() + _dragBounds.getWidth() ) - nodeBounds.getX() - _nodeCenter.getX();
        }
        else {
            // follow mouse
            dx = mousePosition.getX() - nodeBounds.getX() - _pressedOffset.getX();
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
            dy = ( _dragBounds.getY() + _dragBounds.getHeight() ) - nodeBounds.getY() - _nodeCenter.getY();
        }
        else {
            // follow mouse
            dy = mousePosition.getY() - nodeBounds.getY() - _pressedOffset.getY();
        }

        // Perform the drag
        if ( dx != 0 || dy != 0 ) {
            node.translate( dx, dy );
        }
    }
    
    /*
     * Constrains the drag.
     * Bounds of the node must be fully contained within the drag bounds.
     */
    private void dragConstrainBounds( PInputEvent event ) {
        
        Point2D mousePosition = event.getPosition();
        PNode node = event.getPickedNode();
        Rectangle2D nodeBounds = getNodeBounds( node );
        
        /*
         * Adjust the mouse location to account for where we clicked 
         * relative to the node's upper left corner.
         */
        double x = mousePosition.getX() - _pressedOffset.getX();
        double y = mousePosition.getY() - _pressedOffset.getY();
        _adjustedMousePosition.setLocation( x, y );
        
        // Calculate dx
        double dx = 0;
        if ( _horizontalLockEnabled ) {
            dx = 0;
        }
        else if ( _adjustedMousePosition.getX() < _dragBounds.getX() ) {
            // move to far left
            dx = _dragBounds.getX() - nodeBounds.getX();
        }
        else if ( _adjustedMousePosition.getX() + nodeBounds.getWidth() > _dragBounds.getX() + _dragBounds.getWidth() ) {
            // move to far right
            dx = ( _dragBounds.getX() + _dragBounds.getWidth() - nodeBounds.getWidth() ) - nodeBounds.getX() ;
        }
        else {
            // follow mouse
            dx = mousePosition.getX() - nodeBounds.getX() - _pressedOffset.getX();
        }

        // Calculate dy
        double dy = 0;
        if ( _verticalLockEnabled ) {
            dy = 0;
        }
        else if ( _adjustedMousePosition.getY() < _dragBounds.getY() ) {
            // move to top
            dy = _dragBounds.getY() - nodeBounds.getY();
        }
        else if ( _adjustedMousePosition.getY() + nodeBounds.getHeight() > _dragBounds.getY() + _dragBounds.getHeight() ) {
            // move to bottom 
            dy = ( _dragBounds.getY() + _dragBounds.getHeight() - nodeBounds.getHeight() ) - nodeBounds.getY();
        }
        else {
            // follow mouse
            dy = mousePosition.getY() - nodeBounds.getY() - _pressedOffset.getY();
        }

        // Perform the drag
        if ( dx != 0 || dy != 0 ) {
            node.translate( dx, dy );
        }
    }
    
    /*
     * Use the same bounds everywhere.
     */
    private Rectangle2D getNodeBounds( PNode node ) {
        if ( _useFullBounds ) {
            return node.getGlobalFullBounds();
        }
        else {
            return node.getGlobalBounds();
        }
    }
}
