package edu.colorado.phet.jamaphet.collision;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Interface for objects whose collsisions need to be handled
 *
 * Spelled like this, since it looks better to me. See also http://english.stackexchange.com/questions/11646/adjective-form-of-collidecollideable-or-collidable
 */
public interface Collidable2D {

    public ImmutableVector2D getPosition();

    public ImmutableVector2D getVelocity();

}
