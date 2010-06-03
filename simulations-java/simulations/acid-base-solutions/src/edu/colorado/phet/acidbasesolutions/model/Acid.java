/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;

/**
 * Base class for all solutes that are acids.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Acid extends Solute {

    public Acid( Molecule molecule, Molecule conjugate, double strength ) {
        super( molecule, conjugate, strength );
    }

    public String getStrengthSymbol() {
        return ABSSymbols.Ka;
    }

}
