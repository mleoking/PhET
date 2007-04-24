package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:36:26 PM
 *
 */
public interface UpdateStrategy {
    RotationModelState update( RotationModel rotationModel, double dt );
}
