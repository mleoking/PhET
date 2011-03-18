// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

/**
 * @author Sam Reid
 */
public interface DispersionFunction {
    double getIndexOfRefraction( double wavelength, double indexOfRefractionForBlue );//VisibleColor.MIN_WAVELENGTH / 1E9
}