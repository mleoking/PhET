/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.model;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:32:09 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public interface UpdateMode {
    void stepInTime( EnergyConservationModel model, Body body, double dt );
}
