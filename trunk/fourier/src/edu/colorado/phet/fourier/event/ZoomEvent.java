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
 * ZoomEvent
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ZoomEvent extends EventObject {

    public static final int VERTICAL_ZOOM_IN = 0;
    public static final int VERTICAL_ZOOM_OUT = 1;
    public static final int HORIZONTAL_ZOOM_IN = 2;
    public static final int HORIZONTAL_ZOOM_OUT = 3;
    
    private int _zoomType;
    
    public ZoomEvent( Object source, int zoomType ) {
        super( source );
        assert( isValidZoomType( zoomType ) );
        _zoomType = zoomType;
    }
    
    public int getZoomType() {
        return _zoomType;
    }
    
    private boolean isValidZoomType( int zoomType ) {
        return ( zoomType == VERTICAL_ZOOM_IN ||
                 zoomType == VERTICAL_ZOOM_OUT ||
                 zoomType == HORIZONTAL_ZOOM_IN ||
                 zoomType == HORIZONTAL_ZOOM_OUT );
    }
}
