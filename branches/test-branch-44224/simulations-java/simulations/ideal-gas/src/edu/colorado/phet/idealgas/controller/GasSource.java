/**
 * Class: GasSource
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 27, 2004
 */
package edu.colorado.phet.idealgas.controller;

public interface GasSource {
    void setCurrentGasSpecies( Class currentGasSpecies );

    Class getCurrentGasSpecies();
}
