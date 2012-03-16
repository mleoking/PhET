// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.model.NoSolute;
import edu.colorado.phet.advancedacidbasesolutions.model.Water;

/**
 * Equilibrium model for pure water (no solute).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PureWaterEquilibriumModel extends AbstractEquilibriumModel {
    
    private static final double H3O_CONCENTRATION = 1E-7;
    private static final double OH_CONCENTRATION = 1E-7;

    public PureWaterEquilibriumModel( NoSolute solute ) {
        super( solute );
    }
    
    public double getReactantConcentration() {
        return 0;
    }
    
    public double getProductConcentration() {
        return 0;
    }
    
    public double getH3OConcentration() {
        return H3O_CONCENTRATION;
    }

    public double getOHConcentration() {
        return OH_CONCENTRATION;
    }
    
    public double getH2OConcentration() {
        return Water.getConcentration();
    }
}
