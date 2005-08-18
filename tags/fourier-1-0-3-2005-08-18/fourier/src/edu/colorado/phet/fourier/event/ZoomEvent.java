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

import java.util.EventObject;


/**
 * ZoomEvent indicates that a zoom action has occurred.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ZoomEvent extends EventObject {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // The zoom types
    public static final int VERTICAL_ZOOM_IN = 0;
    public static final int VERTICAL_ZOOM_OUT = 1;
    public static final int HORIZONTAL_ZOOM_IN = 2;
    public static final int HORIZONTAL_ZOOM_OUT = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _zoomType;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param source
     * @param zoomType
     */
    public ZoomEvent( Object source, int zoomType ) {
        super( source );
        assert( isValidZoomType( zoomType ) );
        _zoomType = zoomType;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the zoom type associated with the event.
     * 
     * @return the zoom type
     */
    public int getZoomType() {
        return _zoomType;
    }
    
    /**
     * Validates a zoom type.
     * 
     * @param zoomType
     * @return true or false
     */
    public boolean isValidZoomType( int zoomType ) {
        return ( zoomType == VERTICAL_ZOOM_IN ||
                 zoomType == VERTICAL_ZOOM_OUT ||
                 zoomType == HORIZONTAL_ZOOM_IN ||
                 zoomType == HORIZONTAL_ZOOM_OUT );
    }
}
