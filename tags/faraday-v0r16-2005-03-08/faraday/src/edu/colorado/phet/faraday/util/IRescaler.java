/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.util;


/**
 * IRescaler is the interface implemented by all classes that perform rescaling.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IRescaler {
    
    /**
     * Rescales a "scale" value. A scale value is a percetange.
     * 
     * @param scale a value in the range 0...1
     * @return the rescaled value, int the range 0...1
     */
    public double rescale( double scale );
}
