package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:36:26 PM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */
public interface UpdateStrategy {
    RotationModelState update( RotationModel rotationModel, double dt );
}
