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
     * Get the 2D position of the constituent.
     * 
     * @return The position.
     */
    public void setPosition(Point2D position);
    
    /**
     * This method tells the constituent to simulate quantum tunneling
     * behavior, meaning that it will randomly jump to a new position
     * within the specified range.
     * 
     * @param center - The center point around which tunneling should occur.
     * @param minDistance - Minimum distance from center where particle can end up.
     * @param nucleusRadius - Radius of the nucleus, which is where particles will mostly tunnel.
     * @param tunnelRadius - Radius where particles will occasionally tunnel out to.
     */
    public void tunnel(Point2D center, double minDistance, double nucleusRadius, double tunnelRadius);
}
