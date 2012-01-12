// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.quantum.model;


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
