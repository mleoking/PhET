/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.collision;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;

/**
 * CollisionDetector handles collision detection.
 * <b>
 * Each object can specify a set of Rectangles that define its collision bounds.
 * Two objects collide if any of the Rectangles in their collision bounds intersect.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CollisionDetector {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The object that we're handling collision detection for.
    private ICollidable _object;
    
    // The object that we might collide with.
    private HashSet _collidables;
    
    // Proposed bounds for moving the object.
    private Rectangle _proposedBounds;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param object the object that we're handling collision detection for
     */
    public CollisionDetector( ICollidable object ) {
        _object = object;
        _collidables = new HashSet();
        _proposedBounds = new Rectangle();
    }
    
    //----------------------------------------------------------------------------
    // HashSet management
    //----------------------------------------------------------------------------

    /**
     * Adds an object to the collision detection set.
     * 
     * @param object the object
     */
    public void add( ICollidable object ) {
        _collidables.add( object );
    }

    /**
     * Removes an object from the collision detection set.
     * 
     * @param object the object
     */
    public void remove( ICollidable object ) {
        _collidables.remove( object );
    }

    /**
     * Clears the collision detection set.
     */
    public void clear() {
        _collidables.clear();
    }
    
    //----------------------------------------------------------------------------
    // Collision detection
    //----------------------------------------------------------------------------

    /**
     * Determines if the object currently collides with anything.
     * 
     * @return true or false
     */
    public boolean collidesNow() {
        return wouldCollide( 0, 0 );
    }
    
    /**
     * Determines if the object would collide with anything if we moved it 
     * a hypothetical amount.
     * 
     * @param dx horizontal delta
     * @param dy vertical delta
     * @return true or false
     */
    public boolean wouldCollide( int dx, int dy ) {
        boolean collides = false;
        Iterator i = _collidables.iterator();
        while ( i.hasNext() ) {
            ICollidable object = (ICollidable) i.next();
            if ( wouldCollide( dx, dy, object ) ) {
                collides = true;
                break;
            }
        }
        return collides;
    }

    /**
     * Evaluates one candidate at a time to see if there would be a collision.
     * 
     * @param dx horizontal delta
     * @param dy vertical delta
     * @param candidate the collision candidate
     * @return true or false
     */
    private boolean wouldCollide( int dx, int dy, ICollidable candidate ) {
        boolean collides = false;
        Rectangle[] bounds = _object.getCollisionBounds();
        for ( int j = 0; j < bounds.length && collides == false; j++ ) {
            _proposedBounds.setBounds( bounds[j].x + dx, bounds[j].y + dy, bounds[j].width, bounds[j].height );
            Rectangle[] collisionBounds = candidate.getCollisionBounds();
            if ( collisionBounds != null ) {
                for ( int i = 0; i < collisionBounds.length && collides == false; i++ ) {
                    if ( _proposedBounds.intersects( collisionBounds[i] ) ) {
                        collides = true;
                    }
                }
            }
        }
        return collides;
    }
}
