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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.test.schmidt_lee.PotentialFunction;
import edu.colorado.phet.boundstates.test.schmidt_lee.Wavefunction;


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
      
    public BSEigenstate[] getEigenstates() {

        ArrayList eigenstates = new ArrayList();
        
        final double m = BSConstants.MAX_MASS;//XXX get mass from particle!
        final double h = BSConstants.HBAR;
        final double hb = 0.5; //( h * h ) / ( 2 * m );
        final double minX = BSConstants.POSITION_RANGE.getLowerBound();
        final double maxX = BSConstants.POSITION_RANGE.getUpperBound();
        final int numberOfPoints = 100;
        final double dx = Math.abs( ( maxX - minX ) / numberOfPoints );

//        Schrodinger1D schrodinger = new Schrodinger1D( hb, minX, maxX, dx, this );
//        for ( int nodes = 1; nodes <= NUMBER_OF_NODES; nodes++ ) {
//            try {
//                double E = schrodinger.getEnergy( nodes );
//                eigenstates.add( new BSEigenstate( E ) );
//            }
//            catch ( Exception e ) {
//                e.printStackTrace();
//            }
//        }

        for ( int nodes = 0; nodes < NUMBER_OF_NODES; nodes++ ) {
            try {
                PotentialFunction function = new PotentialFunctionAdapter( this );
                Wavefunction wavefunction = new Wavefunction( hb, minX, maxX, numberOfPoints, nodes, function );
                double E = wavefunction.getE();
                eigenstates.add( new BSEigenstate( E ) );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }

}
