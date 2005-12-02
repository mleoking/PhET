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

import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.piccolo.ConstrainedDragHandler;
import edu.colorado.phet.quantumtunneling.piccolo.ImageNode;


/**
 * DragHandle
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DragHandle extends ImageNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _orientation;
    private Point2D _registrationPoint;
    private ConstrainedDragHandler _dragHandler;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param orientation HORIZONTAL or VERTICAL
     * @throws IllegalArgumentException
     */
    public DragHandle( int orientation ) {
        super();
        
        if ( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        _orientation = orientation;
        
        // Set the image...
        if ( orientation == HORIZONTAL ) {
            setImageByResourceName( QTConstants.IMAGE_DRAG_HANDLE_HORIZONTAL );
        }
        else {
            setImageByResourceName( QTConstants.IMAGE_DRAG_HANDLE_VERTICAL );
        }
        
        // registration point @ center
        _registrationPoint = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
        
        translate( -_registrationPoint.getX(), -_registrationPoint.getY() );
        
        addInputEventListener( new CursorHandler() );
        _dragHandler = new ConstrainedDragHandler();
        addInputEventListener( _dragHandler );
        
        // Configure the drag handler...
        _dragHandler.setTreatAsPointEnabled( true );
        _dragHandler.setNodeCenter( _registrationPoint.getX(), _registrationPoint.getY() );
        if ( orientation == HORIZONTAL ) {
            _dragHandler.setVerticalLockEnabled( true );
        }
        else {
            _dragHandler.setHorizontalLockEnabled( true );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the orientation.
     * 
     * @return HORIZONTAL or VERTICAL
     */
    public int getOrientation() {
        return _orientation;
    }
    
    /**
     * Sets the drag bounds.
     * 
     * @param bounds
     */
    public void setDragBounds( Rectangle2D bounds ) {
        _dragHandler.setDragBounds( bounds );
    }
    
    /**
     * Sets the position of the node, in the global coordinate system.
     * 
     * @param globalPoint
     */
    public void setGlobalPosition( Point2D globalPoint ) {
        Point2D currentGlobalPosition = getGlobalPosition();
        double deltaX = globalPoint.getX() - currentGlobalPosition.getX();
        double deltaY = globalPoint.getY() - currentGlobalPosition.getY();
        translate( deltaX, deltaY );
    }
    
    /**
     * Gets the position of the node, in the global coordinate system.
     * 
     * @return
     */
    public Point2D getGlobalPosition() {
        Rectangle2D bounds = getGlobalBounds();
        double x = bounds.getX() + _registrationPoint.getX();
        double y = bounds.getY() + _registrationPoint.getY();
        return new Point2D.Double( x, y );          
    }
}
