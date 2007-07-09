package edu.colorado.phet.rotation.model;

/**
 * Using the linear Position, Velocity or Acceleration updates is unlikely insufficient to display the
 * centripetal acceleration for a rotational system.  The current implementation of PositionUpdate, which uses
 * a past window over which to estimate the velocity and acceleration creates a temporal lag, which
 * shows up in circular motion as a centripetal acceleration substantially offset from the center of rotation.
 * <p/>
 * Other schemes for numerical differentiation may produce better results, but none is guaranteed to be exact
 * unless it incorporates the knowledge that the motion is indeed circular.
 * <p/>
 * Author: Sam Reid
 * Jun 30, 2007, 8:44:12 AM
 */
public class RotationPlatformUpdate {
}
