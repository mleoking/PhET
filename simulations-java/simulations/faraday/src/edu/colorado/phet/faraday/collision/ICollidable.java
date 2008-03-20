/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.collision;

import java.awt.Rectangle;



/**
 * ICollidable is the interface implemented by all objects that 
 * participate in collision detection.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
     * @return an array of Rectangle, possibly null
     */
    public Rectangle[] getCollisionBounds();
}
