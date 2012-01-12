// Copyright 2002-2011, University of Colorado

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
 * HarmonicHighlightListener is the listener interface for receiving
 * focus events related to Harmonics.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface HarmonicFocusListener extends EventListener {

    /**
     * Invoked when a Harmonic gains focus.
     * 
     * @param event
     */
    public void focusGained( HarmonicFocusEvent event );
    
    /**
     * Invoked when a Harmonic loses focus.
     * 
     * @param event
     */
    public void focusLost( HarmonicFocusEvent event );
}
