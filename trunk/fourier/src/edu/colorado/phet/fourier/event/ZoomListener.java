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

import java.util.EventListener;


/**
 * ZoomListener is the listener interface for zoom events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface ZoomListener extends EventListener {

    /**
     * Invokes when a zoom occurs.
     * 
     * @param event
     */
    public void zoomPerformed( ZoomEvent event );
}
