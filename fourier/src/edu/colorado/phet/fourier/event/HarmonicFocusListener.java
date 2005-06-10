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
 * HarmonicHighlightListener
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface HarmonicFocusListener extends EventListener {

    public void focusGained( HarmonicFocusEvent event );
    public void focusLost( HarmonicFocusEvent event );
}
