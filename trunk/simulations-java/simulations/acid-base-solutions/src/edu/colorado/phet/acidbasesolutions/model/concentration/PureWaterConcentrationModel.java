package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Water;


public class PureWaterConcentrationModel extends ConcentrationModel {
    
    private static final double H3O_CONCENTRATION = 1E-7;
    private static final double OH_CONCENTRATION = 1E-7;

    public PureWaterConcentrationModel( Solute solute ) {
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
