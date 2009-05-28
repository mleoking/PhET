package edu.colorado.phet.acidbasesolutions.model.equilibrium;

import edu.colorado.phet.acidbasesolutions.model.NoSolute;
import edu.colorado.phet.acidbasesolutions.model.Water;


public class PureWaterEquilibriumModel extends EquilibriumModel {
    
    private static final double H3O_CONCENTRATION = 1E-7;
    private static final double OH_CONCENTRATION = 1E-7;

    public PureWaterEquilibriumModel( NoSolute solute ) {
        super( solute );
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
