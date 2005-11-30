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

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.quantumtunneling.QTConstants;


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
        double rx = getWidth()/2;
        double ry = getHeight()/2;
        
        translate( -rx, -ry );
        
        addInputEventListener( new CursorHandler() );
        _dragHandler = new ConstrainedDragHandler();
        addInputEventListener( _dragHandler );
        
        // Configure the drag handler...
        _dragHandler.setTreatAsPointEnabled( true );
        _dragHandler.setNodeCenter( rx, ry );
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
}
