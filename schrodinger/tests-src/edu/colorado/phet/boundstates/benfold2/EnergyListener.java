/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

/**
 * Used primarily by the {@link Shoot Shoot} applet, a class implementing
 * this interface may receive information about a search for eigenvalues.
 */
public interface EnergyListener {
    /**
     * Called to signal that the best estimate for the energy has changed.
     *
     * @param d The new energy
     */
    public void energyChanged( double d );


    /**
     * Called to signal that the search has finished.s
     */
    public void searchFinished();
}
