/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

/**
 * Interface that defines the events that can be fired from a dynamic atom.
 *
 * @author John Blanco
 */
public interface AtomListener {
    /**
     * This signals that the configuration, meaning the number of protons,
     * neutrons, or electrons, has changed.
     */
    void configurationChanged();

    /**
     * This signals that the center position of the atom has changed.
     */
    void postitionChanged();

    /**
     * Adapter for easier implementation of listeners.
     */
    static class Adapter implements AtomListener {
        public void configurationChanged() {}
        public void postitionChanged() {}
    }
}