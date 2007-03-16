/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.model;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:32:09 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public interface UpdateMode {
    void stepInTime( Body body, double dt );

    void init( Body body );

    UpdateMode copy();

    void finish( Body body );
}
