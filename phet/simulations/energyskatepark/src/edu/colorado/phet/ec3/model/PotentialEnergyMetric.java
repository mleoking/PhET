package edu.colorado.phet.ec3.model;

/**
 * User: Sam Reid
 * Date: Oct 16, 2006
 * Time: 11:34:08 PM
 * Copyright (c) Oct 16, 2006 by Sam Reid
 */
public interface PotentialEnergyMetric {
    double getPotentialEnergy( Body body );

    double getGravity();

    PotentialEnergyMetric copy();
}
