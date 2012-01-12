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
 * HarmonicColorChangeListener is the listener interface for receiving
 * events related to the changing of harmonic colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface HarmonicColorChangeListener extends EventListener {

    /**
     * Invoked when a harmonic's color is changed.
     * 
     * @param event
     */
    public void harmonicColorChanged( HarmonicColorChangeEvent event );
}
