package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
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
        return new PHValue( -MathUtil.log10( getH3OConcentration() ) );
    }
    
    // c
    public double getInitialConcentration() {
        return solute.getConcentration();
    }
    
    public abstract double getH3OConcentration();
    
    public abstract double getOHConcentration();
    
    public abstract double getH2OConcentration();
    
    public double getH3OMoleculeCount() {
        return getMoleculeCount( getH3OConcentration() ); 
    }
    
    public double getOHMoleculeCount() {
        return getMoleculeCount( getOHConcentration() ); 
    }
    
    public double getH2OMoleculeCount() {
        return getMoleculeCount( getH2OConcentration() ); 
    }
    
    protected double getMoleculeCount( double concentration ) {
        return concentration * ABSConstants.AVOGADROS_NUMBER * ABSConstants.SOLUTION_VOLUME;
    }
}
