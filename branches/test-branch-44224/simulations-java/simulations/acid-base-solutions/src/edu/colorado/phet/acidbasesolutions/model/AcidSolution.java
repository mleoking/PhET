/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * Marker class for acid solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AcidSolution extends AqueousSolution {

    public AcidSolution( Molecule solute, Molecule product, double strength, double concentration ) {
        super( solute, product, strength, concentration );
    }
}
