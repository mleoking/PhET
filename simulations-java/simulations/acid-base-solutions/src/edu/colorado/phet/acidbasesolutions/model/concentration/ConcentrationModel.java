package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.PHValue;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.common.phetcommon.math.MathUtil;


public abstract class ConcentrationModel {
    
    private final Solute solute;
    
    protected ConcentrationModel( Solute solute ) {
        this.solute = solute;
    }
    
    public Solute getSolute() {
        return solute;
    }

    public PHValue getPH() {
        double c = getH3OConcentration();
        return new PHValue( -MathUtil.log10( c ) );
    }
    
    // c
    public double getInitialConcentration() {
        return solute.getInitialConcentration();
    }
    
    public abstract double getH3OConcentration();
    
    public abstract double getOHConcentration();
    
    public abstract double getH2OConcentration();
}
