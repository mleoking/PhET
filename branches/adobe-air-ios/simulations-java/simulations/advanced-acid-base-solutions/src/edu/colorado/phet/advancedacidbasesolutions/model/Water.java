// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSImages;
import edu.colorado.phet.advancedacidbasesolutions.AABSStrings;
import edu.colorado.phet.advancedacidbasesolutions.AABSSymbols;

/**
 * Water molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Water extends Molecule {
    
    private static final double W = 55.6; // concentration, mol/L
    private static final double Kw = 1E-14; // equilibrium constant

    public Water() {
        super( AABSStrings.WATER, AABSSymbols.H2O, AABSImages.H2O_MOLECULE, AABSImages.H2O_STRUCTURE, AABSColors.H2O );
    }
    
    public static double getConcentration() {
        return W;
    }
    
    public static double getEquilibriumConstant() {
        return Kw;
    }
}
