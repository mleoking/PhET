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
import java.util.Iterator;

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
    
    public boolean supportsMultipleWells() {
        return true;
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
  
    /*
     * Calculates the eigenstates.
     * Skips ever-other "cluster" of nodes, where the cluster size 
     * is equal to the number of wells in the potential.
     * We do this because every-other cluster of eigenstates is unstable.
     * Stop solving when we reach the top of the well (the top is the same as the offset).
     * Label the eigenstates with consecutive subscripts, (E1, E2, E3,...)
     * regardless of the node number used to solve.
     * 
     * Examples:
     * 1 well:  skip node0, solve node1, skip node2, solve node3, etc.
     * 2 wells: skip node0-1, solve node2-3, skip node4-5, solve node6-7, etc.
     * 3 wells: skip node0-2, solve node3-5, skip node6-8, solve node9-11, etc.
     */
    protected BSEigenstate[] calculateEigenstates() {

        SchmidtLeeSolver solver = getEigenstateSolver();
        ArrayList eigenstates = new ArrayList(); // array of BSEigentate
        ArrayList cluster = new ArrayList(); // array of BSEigenstate
        final double maxE = getOffset();
        final int numberOfWells = getNumberOfWells();
        
        // Calculate the eigentates...
        int nodes = numberOfWells; // skip the first cluster
        int subscript = getStartingIndex();
        boolean done = false;
        while ( !done ) {
            
            // Get the cluster's eigenstates...
            cluster.clear();
            for ( int i = 0; !done && i < numberOfWells; i++ ) {
                try {
                    double E = solver.getEnergy( nodes );
                    if ( E <= maxE ) {
                        cluster.add( new BSEigenstate( subscript, E ) );
                    }
                    else {
                        done = true;
                    }
                }
                catch ( SchmidtLeeException sle ) {
                    System.err.println( sle.getClass() + ": " + sle.getMessage() );//XXX
                    done = true;
                }
                nodes++;
                subscript++;
            }
            
            // Add the entire cluster or nothing...
            if ( cluster.size() == numberOfWells ) {
                Iterator i = cluster.iterator();
                while ( i.hasNext() ) {
                    eigenstates.add( i.next() );
                }
            }
            
            // skip the next cluster
            nodes += numberOfWells;
        }
        
        // Ensure that they appear in ascending order...
        Collections.sort( eigenstates );
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}
