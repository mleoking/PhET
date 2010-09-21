/* Copyright 2005-2010, University of Colorado */

package edu.colorado.phet.faraday.collision;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.HashSet;

/**
 * CollisionDetector handles collision detection.
 * <b>
 * Each object can specify a set of Shapes that define its collision bounds.
 * Two objects collide if any of the Shapes in their collision bounds intersect.
 * <p>
 * This strategy is not scaleable. It works OK for this sim because we have
 * a very small number of objects that may collide.
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
    private HashSet<ICollidable> _collidables;
    
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
        _collidables = new HashSet<ICollidable>();
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
        for ( ICollidable collidable : _collidables ) {
            if ( wouldCollide( dx, dy, collidable ) ) {
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
        
        AffineTransform t = new AffineTransform();
        t.translate( dx, dy );
        
        // for each shape that defines the bounds of our object...
        Shape[] bounds = _object.getCollisionBounds();
        for ( int i = 0; i < bounds.length && collides == false; i++ ) {
            
            // translate the shape
            Shape movedBounds = t.createTransformedShape( bounds[i] );
            
            // determine if the shape intersects with the candidate object's shapes
            Shape[] candidateBounds = candidate.getCollisionBounds();
            if ( candidateBounds != null ) {
                for ( int j = 0; j < candidateBounds.length && collides == false; j++ ) {
                    Area area = new Area( movedBounds );
                    Area candidateArea = new Area( candidateBounds[j] );
                    area.intersect( candidateArea );
                    if ( !area.isEmpty() ) {
                        collides = true;
                    }
                }
            }
        }
        return collides;
    }
}
