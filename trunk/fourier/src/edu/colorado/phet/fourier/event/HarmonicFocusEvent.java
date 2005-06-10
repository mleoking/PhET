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

import edu.colorado.phet.fourier.model.Harmonic;


/**
 * HarmonicHighlightEvent
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicFocusEvent extends EventObject {

    private Harmonic _harmonic;
    private boolean _hasFocus;
    
    public HarmonicFocusEvent( Object source, Harmonic harmonic, boolean hasFocus ) {
        super( source );
        assert( harmonic != null );
        _harmonic = harmonic;
        _hasFocus = hasFocus;
    }
    
    public Harmonic getHarmonic() {
        return _harmonic;
    }
    
    public boolean hasFocus() {
        return _hasFocus;
    }
}
