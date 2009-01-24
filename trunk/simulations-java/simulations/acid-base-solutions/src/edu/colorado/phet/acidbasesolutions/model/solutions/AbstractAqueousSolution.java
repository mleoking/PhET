
package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.common.phetcommon.math.MathUtil;


public abstract class AbstractAqueousSolution implements IAqueousSolution {

    private static final double AVOGADROS_NUMBER = 6.022E23;
    
    protected static double getAvogadrosNumber() {
        return AVOGADROS_NUMBER;
    }
    
    // pH = -log10([H3O+])
    public double getPH() {
        return -MathUtil.log10( getHydroniumConcentration() );
    }
    
    // count of H3O+ molecules
    public int getHydroniumMoleculeCount() {
        return (int) ( AVOGADROS_NUMBER * getHydroniumConcentration() );
    }

    // count of OH- molecules
    public int getHydroxideMoleculeCount() {
        return (int) ( AVOGADROS_NUMBER * getHydroxideConcentration() );
    }

    // count of H2O molecules
    public int getWaterMoleculeCount() {
        return (int) ( AVOGADROS_NUMBER * getWaterConcentration() );
    }
}
