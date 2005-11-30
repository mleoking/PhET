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
 * EnergyManipulator
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergyManipulator extends ImageNode {

    private ConstrainedDragHandler _dragHandler;
    
    public EnergyManipulator() {
        super( QTConstants.IMAGE_DRAG_HANDLE_LR );
        
        // registration point @ center
        double rx = getWidth()/2;
        double ry = getHeight()/2;
        
        translate( -rx, -ry );
        
        addInputEventListener( new CursorHandler() );
        
        _dragHandler = new ConstrainedDragHandler();
        _dragHandler.setTreatAsPointEnabled( true );
        _dragHandler.setNodeCenter( rx, ry );
        _dragHandler.setVerticalLockEnabled( true );
        addInputEventListener( _dragHandler );
    }
    
    public void setDragBounds( Rectangle2D bounds ) {
        _dragHandler.setDragBounds( bounds );
    }
}
