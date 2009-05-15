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
    
    public abstract double getH3OConcentration();
    
    public abstract double getOHConcentration();
    
    public abstract double getH2OConcentration();
    
    public int getH3OMoleculeCount() {
        return getMoleculeCount( getH3OConcentration() ); 
    }
    
    public int getOHMoleculeCount() {
        return getMoleculeCount( getOHConcentration() ); 
    }
    
    public int getH2OMoleculeCount() {
        return getMoleculeCount( getH2OConcentration() ); 
    }
    
    protected int getMoleculeCount( double concentration ) {
        System.out.println( "ConcentrationModel.getMoleculeCount concentration=" + concentration );//XXX
        return (int)( concentration * ABSConstants.AVOGADROS_NUMBER * ABSConstants.SOLUTION_VOLUME );
    }
}
