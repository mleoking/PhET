/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;

/**
 * This interface is for controlling the particles that make up the atomic
 * nucleus.  It would be called "Nucleon", except that we allow alpha
 * particles to also be a constituent of the nucleus, and alpha particles
 * aren't really nucleons.
 *
 * @author John Blanco
 */
public interface AtomicNucleusConstituent {
    
    /**
     * Get the 2D position of the constituent.
     * 
     * @return The position.
     */
    public Point2D getPosition();
    
    /**
     * This method tells the constituent to simulate quantum tunneling
     * behavior, meaning that it will randomly jump to a new position
     * within the specified range.
     * 
     * @param minDistance - Minimum distance from origin where particle can end up.
     * @param maxDistance - Maximum distance from origin where particle can end up.
     */
    public void tunnel(double minDistance, double nucleusRadius, double tunnelRadius);
}
