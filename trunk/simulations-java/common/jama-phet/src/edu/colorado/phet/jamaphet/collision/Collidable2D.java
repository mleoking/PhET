// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.jamaphet.collision;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Interface for objects whose collsisions need to be handled
 * <p/>
 * Spelled like this, since it looks better to me. See also http://english.stackexchange.com/questions/11646/adjective-form-of-collidecollideable-or-collidable
 */
public interface Collidable2D {

    public Vector2D getPosition();

    public Vector2D getVelocity();

}
