/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantum.model;



/**
 *
 */
public class GroundState extends AtomicState {

    public GroundState() {
        setEnergyLevel( 0 );
        setMeanLifetime( Double.POSITIVE_INFINITY );
    }

    public AtomicState getNextLowerEnergyState() {
        return AtomicState.MinEnergyState.instance();
    }
}
