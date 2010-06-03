/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Base class for all solutes that are bases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Base extends Solute {

    public Base( Molecule molecule, Molecule conjugate, double strength ) {
        super( molecule, conjugate, strength );
    }

    public String getStrengthSymbol() {
        return ABSSymbols.Kb;
    }
}
