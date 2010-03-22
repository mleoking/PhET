package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.AABSColors;
import edu.colorado.phet.acidbasesolutions.AABSImages;
import edu.colorado.phet.acidbasesolutions.AABSStrings;
import edu.colorado.phet.acidbasesolutions.AABSSymbols;

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
