/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

/**
 * This interface is for controlling subatomic particles.
 *
 * @author John Blanco
 */
public interface SubatomicParticle {
    
    /**
     * Get the 2D position of the constituent.
     * 
     * @return The position.
     */
    public Point2D.Double getPosition();
    
    /**
     * Get a reference to the 2D position of the constituent.
     * 
     * @return The position.
     */
    public Point2D.Double getPositionReference();
    
    /**
     * Set the 2D position of the constituent.
     * 
     * @return The position.
     */
    public void setPosition(Point2D position);
    
    public void setPosition(double xPos, double yPos);
    
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
