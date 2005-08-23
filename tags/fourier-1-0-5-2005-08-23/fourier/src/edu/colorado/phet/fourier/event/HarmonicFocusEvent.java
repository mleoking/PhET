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
 * HarmonicFocusEvent indicates that a Harmonic has gained or lost focus.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicFocusEvent extends EventObject {

    private Harmonic _harmonic;
    private boolean _hasFocus;
    
    /**
     * Sole constructor
     * 
     * @param source
     * @param harmonic
     * @param hasFocus
     */
    public HarmonicFocusEvent( Object source, Harmonic harmonic, boolean hasFocus ) {
        super( source );
        assert( harmonic != null );
        _harmonic = harmonic;
        _hasFocus = hasFocus;
    }
    
    /**
     * Gets the Harmonic that has gained or lost focus.
     * 
     * @return Harmonic
     */
    public Harmonic getHarmonic() {
        return _harmonic;
    }
    
    /**
     * Indicates whether the Harmonic has focus.
     * 
     * @return true or false
     */
    public boolean hasFocus() {
        return _hasFocus;
    }
}
