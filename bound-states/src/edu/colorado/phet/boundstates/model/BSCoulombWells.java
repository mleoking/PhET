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

import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.SchmidtLeeSolver.SchmidtLeeException;


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
   
    private static final int NUMBER_OF_NODES = 10;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCoulombWells( BSParticle particle, int numberOfWells ) {
        this( particle, 
                numberOfWells, 
                BSConstants.DEFAULT_COULOMB_SPACING,
                BSConstants.DEFAULT_COULOMB_OFFSET, 
                BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSCoulombWells( BSParticle particle, int numberOfWells, double spacing, double offset, double center ) {
        super( particle, numberOfWells, spacing, offset, center );
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
      
    public BSEigenstate[] getEigenstates() {

        ArrayList eigenstates = new ArrayList();

        final double minX = BSConstants.POSITION_MODEL_RANGE.getLowerBound();
        final double maxX = BSConstants.POSITION_MODEL_RANGE.getUpperBound();
        final double hb = ( BSConstants.HBAR * BSConstants.HBAR ) / ( 2 * getParticle().getMass() );
        final int numberOfPoints = (int)( (maxX - minX) / getDx() ) + 1;
        
        SchmidtLeeSolver solver = new SchmidtLeeSolver( hb, minX, maxX, numberOfPoints, this );
        for ( int nodes = 0; nodes < NUMBER_OF_NODES; nodes++ ) {
            try {
                double E = solver.getEnergy( nodes );
                eigenstates.add( new BSEigenstate( E ) );
            }
            catch ( SchmidtLeeException sle ) {
                System.err.println( sle.getClass() + ": " + sle.getMessage() );//XXX
                break;
            }
        }
        
        // Ensure that they appear in ascending order...
        Collections.sort( eigenstates );
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
    
    public boolean supportsMultipleWells() {
        return true;
    }
}
