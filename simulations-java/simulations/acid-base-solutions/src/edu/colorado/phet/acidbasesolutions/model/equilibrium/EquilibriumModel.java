package edu.colorado.phet.acidbasesolutions.model.equilibrium;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.PHValue;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.common.phetcommon.math.MathUtil;


public abstract class EquilibriumModel {
    
    private final Solute solute;
    
    protected EquilibriumModel( Solute solute ) {
        this.solute = solute;
    }
    
    protected Solute getSolute() {
        return solute;
    }

    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getPH()
     */
    public PHValue getPH() {
        return new PHValue( -MathUtil.log10( getH3OConcentration() ) );
    }
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getReactantConcentration()
     */
    public abstract double getReactantConcentration();
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getProductConcentration()
     */
    public abstract double getProductConcentration();
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getH3OConcentration()
     */
    public abstract double getH3OConcentration();
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getOHConcentration()
     */
    public abstract double getOHConcentration();
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getH2OConcentration()
     */
    public abstract double getH2OConcentration();
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getReactantMoleculeCount()
     */
    public double getReactantMoleculeCount() {
        return getMoleculeCount( getReactantConcentration() ); 
    }
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getProductMoleculeCount()
     */
    public double getProductMoleculeCount() {
        return getMoleculeCount( getProductConcentration() ); 
    }
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getH3OMoleculeCount()
     */
    public double getH3OMoleculeCount() {
        return getMoleculeCount( getH3OConcentration() ); 
    }
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getOHMoleculeCount()
     */
    public double getOHMoleculeCount() {
        return getMoleculeCount( getOHConcentration() ); 
    }
    
    /*
     * @see edu.colorado.phet.acidbasesolutions.model.equilibrium.IEquilibriumModel#getH2OMoleculeCount()
     */
    public double getH2OMoleculeCount() {
        return getMoleculeCount( getH2OConcentration() ); 
    }
    
    protected static double getMoleculeCount( double concentration ) {
        return concentration * ABSConstants.AVOGADROS_NUMBER;
    }
}
