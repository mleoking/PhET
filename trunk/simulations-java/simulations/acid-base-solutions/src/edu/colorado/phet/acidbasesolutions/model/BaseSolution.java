/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * Marker class for base solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BaseSolution extends AqueousSolution {

    public BaseSolution( Molecule solute, Molecule product, double strength, double concentration ) {
        super( solute, product, strength, concentration );
    }
}