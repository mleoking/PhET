/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * ICurrentSource
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface ICurrentSource {

    /**
     * Gets the current value (in ampheres) of the current source.
     * 
     * @return the current value, in amps
     */
    public double getCurrent();
}
