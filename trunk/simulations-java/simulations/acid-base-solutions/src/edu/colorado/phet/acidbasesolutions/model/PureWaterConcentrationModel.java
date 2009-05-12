package edu.colorado.phet.acidbasesolutions.model;


public class PureWaterConcentrationModel extends ConcentrationModel {
    
    private static final double H3O_CONCENTRATION = 1E-7;
    private static final double OH_CONCENTRATION = 1E-7;

    public PureWaterConcentrationModel( NullSolute solute ) {
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
