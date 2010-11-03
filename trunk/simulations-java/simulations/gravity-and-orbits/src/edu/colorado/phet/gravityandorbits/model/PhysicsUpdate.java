package edu.colorado.phet.gravityandorbits.model;

/**
 * @author Sam Reid
 */
public interface PhysicsUpdate {
    ModelState getNextState( ModelState state, double dt );
}
