/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;


/**
 * AbstractWave was originally intended to contain all of the stuff that 
 * was common to plane waves and wave packets.  As it turns out, there's
 * not much in common, since the algorithms for solving their associated
 * wave functions are fundamentally very different.  This class survives
 * for the few cases where we need to set a wave and don't care which
 * type it is.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractWave extends QTObservable {

    public AbstractWave() {}
    
    public abstract boolean isInitialized();
}
