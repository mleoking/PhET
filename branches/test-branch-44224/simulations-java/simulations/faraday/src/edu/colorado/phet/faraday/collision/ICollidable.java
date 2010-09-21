/* Copyright 2005-2010, University of Colorado */

package edu.colorado.phet.faraday.collision;

import java.awt.Shape;

/**
 * ICollidable is the interface implemented by all objects that 
 * participate in collision detection.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ICollidable {

    /**
     * Gets the CollisionDetector associated with the object.
     * 
     * @return the CollisionDetector
     */
    public CollisionDetector getCollisionDetector();
    
    /**
     * Gets the bounds that define the collidable area of the object.
     * If an object is invisible, the bounds returned should be null.
     * 
     * @return an array of Shapes, possibly null
     */
    public Shape[] getCollisionBounds();
}
