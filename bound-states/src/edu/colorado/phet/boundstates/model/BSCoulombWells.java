/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;


/**
 * BSCoulombWells is the model of a potential composed of one or more Coulomb wells.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>number of wells
 * <li>spacing
 * <li>offset
 * </ul>
 * Offset and spacing are identical for each well.
 * Spacing is irrelevant if the number of wells is 1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulombWells extends BSAbstractPotential {
   
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCoulombWells( int numberOfWells ) {
        this( numberOfWells, 
                BSConstants.DEFAULT_COULOMB_SPACING,
                BSConstants.DEFAULT_COULOMB_OFFSET, 
                BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSCoulombWells( int numberOfWells, double spacing, double offset, double center ) {
        super( numberOfWells, spacing, offset, center );
    }

    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    public BSWellType getWellType() {
        return BSWellType.COULOMB;
    }
    
    public int getStartingIndex() {
        return 1;
    }

    public double getEnergyAt( double position ) {

        double energy = 0;
        
        final int n = getNumberOfWells();
        final double kee = BSConstants.KEE;
        final double s = getSpacing();
        final double offset = getOffset();
        final double c = getCenter();
        
        for ( int i = 1; i <= n; i++ ) {
            final double xi = s * ( i - ( ( n + 1  ) / 2.0 ) );
            energy += -kee / Math.abs( (position-c) - xi );
        }
        
        return offset + energy;
    }
      
    //HACK dummy eigenstates, evenly spaced below the offset
    public BSEigenstate[] getEigenstates() {
        BSEigenstate[] eigenstates = new BSEigenstate[ 10 ];
        for ( int i = 0; i < eigenstates.length; i++ ) {
            eigenstates[i] = new BSEigenstate( getOffset() - ( ( eigenstates.length - i - 1 ) * 0.5 ) );
        }
        return eigenstates;
    }

}
