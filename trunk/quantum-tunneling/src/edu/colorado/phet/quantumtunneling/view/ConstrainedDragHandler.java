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

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PDimension;


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
    
    private Rectangle2D _dragBounds;
    private Point2D _nodeCenter;
    private Point2D _hypotheticalLocation;
    private boolean _dragEnabled;
    private boolean _verticalLockEnabled;
    private boolean _horizontalLockEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConstrainedDragHandler() {
        _dragBounds = new Rectangle2D.Double();
        _nodeCenter = new Point2D.Double();
        _hypotheticalLocation = new Point2D.Double();
        _dragEnabled = true;
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
    
    public void mouseDragged( PInputEvent event ) {
        
        PNode node = event.getPickedNode();
        
        if ( ! _dragEnabled ) {
            // Don't resume dragging until the mouse cursor is inside the node.
            Point2D mousePosition = event.getPosition();
            Rectangle2D nodeBounds = node.getBounds();
            nodeBounds = node.localToGlobal( nodeBounds );
            if ( nodeBounds.contains( mousePosition ) ) {
                _dragEnabled = true;
            }
        }
        else {
            PDimension delta = event.getDeltaRelativeTo( node );
            double deltaX = ( _horizontalLockEnabled ) ? 0 : delta.width;
            double deltaY = ( _verticalLockEnabled ) ? 0 : delta.height;
            
            // Determine the hypothetical point to move to.
            node.getTransformReference( true ).transform( _nodeCenter, _hypotheticalLocation );
            _hypotheticalLocation.setLocation( _hypotheticalLocation.getX() + deltaX, _hypotheticalLocation.getY() + deltaY );

            // If the point is outside the bounds, drag to one of the extremes.
            if ( ! _dragBounds.contains( _hypotheticalLocation ) ) {

                _dragEnabled = false;
                
                Rectangle2D nodeBounds = node.getGlobalFullBounds();
                
                // Adjust deltaX
                if ( _hypotheticalLocation.getX() < _dragBounds.getX() ) {
                    // far left
                    deltaX = _dragBounds.getX() - nodeBounds.getX() - _nodeCenter.getX();
                }
                else if ( _hypotheticalLocation.getX() > _dragBounds.getX() + _dragBounds.getWidth() ) {
                    // far right
                    deltaX = _dragBounds.getX() + _dragBounds.getWidth()  - nodeBounds.getX() - _nodeCenter.getX();
                }
                
                // Adjust deltaY
                if ( _hypotheticalLocation.getY() < _dragBounds.getY() ) {
                    // top
                    deltaY = _dragBounds.getY() - nodeBounds.getY() - _nodeCenter.getY();
                }
                else if ( _hypotheticalLocation.getY() > _dragBounds.getY() + _dragBounds.getHeight() ) { 
                    // bottom 
                    deltaY = _dragBounds.getY() + _dragBounds.getHeight()  - nodeBounds.getY() - _nodeCenter.getY();
                }
            }
            
            // Perform the drag
            node.translate( deltaX, deltaY );
        }
    }
    
    public void mouseReleased( PInputEvent event ) {
        _dragEnabled = false;
    }
}
