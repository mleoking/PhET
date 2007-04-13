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
    
    /**
     * Gets the reflection probability.
     * A return value < 0 indicates that reflection probability
     * doesn't make sense for the state of the wave.
     * 
     * @return double
     */
    public abstract double getReflectionProbability();
    
    /**
     * Gets the transmission probability.
     * A return value < 0 indicates that transmission probability
     * doesn't make sense for the state of the wave.
     * 
     * @return double
     */
    public double getTransmissionProbability() {
        double T = -1;
        double R = getReflectionProbability();
        if ( R >= 0 ) {
           T = 1 - R;
        }
        return T;
    }
}
