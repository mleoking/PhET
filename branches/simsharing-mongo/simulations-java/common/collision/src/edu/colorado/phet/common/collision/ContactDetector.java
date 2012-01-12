// Copyright 2002-2011, University of Colorado

/**
 * Class: ContactDetector
 * Package: edu.colorado.phet.lasers.model.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.common.collision;

public interface ContactDetector {
    boolean areInContact( Collidable bodyA, Collidable bodyB );

    boolean applies( Collidable bodyA, Collidable bodyB );
}
