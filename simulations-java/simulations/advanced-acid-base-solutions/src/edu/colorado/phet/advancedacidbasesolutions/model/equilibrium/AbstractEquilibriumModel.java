// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.AABSConstants;
import edu.colorado.phet.advancedacidbasesolutions.model.PHValue;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute;
import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Base class for models of a solution in equilibrium.
 * The reactant is the solute that is added to the solution.
 * The product is what is produced as a result of the reaction.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractEquilibriumModel {
    
    private final Solute solute;
    
    protected AbstractEquilibriumModel( Solute solute ) {
        this.solute = solute;
    }
    
    protected Solute getSolute() {
        return solute;
    }

    public PHValue getPH() {
        return new PHValue( -MathUtil.log10( getH3OConcentration() ) );
    }
    
    public abstract double getReactantConcentration();
    
    public abstract double getProductConcentration();
    
    public abstract double getH3OConcentration();
    
    public abstract double getOHConcentration();
    
    public abstract double getH2OConcentration();
    
    public double getH3OConcentration( double pH ) {
        return Math.pow( 10, -pH );
    }
    
    public double getOHConcentration( double pH ) {
        return Math.pow( 10, -( 14 - pH ) );
    }
    
    public double getReactantMoleculeCount() {
        return getMoleculeCount( getReactantConcentration() ); 
    }
    
    public double getProductMoleculeCount() {
        return getMoleculeCount( getProductConcentration() ); 
    }
    
    public double getH3OMoleculeCount() {
        return getMoleculeCount( getH3OConcentration() ); 
    }
    
    public double getOHMoleculeCount() {
        return getMoleculeCount( getOHConcentration() ); 
    }
    
    public double getH2OMoleculeCount() {
        return getMoleculeCount( getH2OConcentration() ); 
    }
    
    protected static double getMoleculeCount( double concentration ) {
        return concentration * AABSConstants.AVOGADROS_NUMBER;
    }
}
