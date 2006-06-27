/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;


/**
 * BSCoulomb3DWell is the model of a potential composed of one 3-D Coulomb well.
 * This implementation does not support multiple wells.
 * <p>
 * Most of the implementation is identical to 1-D Coulomb, so we simply
 * extend BSCoulomb1DWell and replace override the pieces that are different.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>offset
 * </ul>
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DWell extends BSCoulomb1DWells {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int NUMBER_OF_EIGENSTATES = 10;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param particle
     * @param offset
     */
    public BSCoulomb3DWell( BSParticle particle, double offset ) {
        super( particle, 1 /* numberOfWells */, offset, 0 /* spacing */ );
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /**
     * Gets the type of well used in the potential.
     * Potentials in this simulation are composed of homogeneous well types.
     * 
     * @return BSWellType.COULOMB_3D
     */
    public BSWellType getWellType() {
        return BSWellType.COULOMB_3D;
    }
    
    /**
     * Multiple wells are not supported.
     * @returns false
     */
    public boolean supportsMultipleWells() {
        return false;
    }
    
    /**
     * Gets the wave function approximation.
     * For the single-well, use an analytic solver.
     * For multiple-wells, use the Schmidt-Lee solver.
     */
    public Point2D[] getWaveFunctionPoints( BSEigenstate eigenstate, final double minX, final double maxX ) {
        if ( getNumberOfWells() != 1 ) {
            return super.getWaveFunctionPoints( eigenstate, minX, maxX );
        }
        else {
            final int numberOfPoints = BSConstants.COULOMB_ANALYTIC_SAMPLE_POINTS;
            BSCoulomb3DSolver solver = new BSCoulomb3DSolver( this, getParticle() );
            return solver.getWaveFunction( eigenstate.getSubscript(), minX, maxX, numberOfPoints );
        }
    }
    
    /**
     * Gets the coefficient required to normalize a wave function that 
     * was produced using the 1D Coulomb solver.
     * 
     * @param points
     * @param eigenstateIndex 0,...n
     * @return
     */
    public double getNormalizationCoefficient( Point2D[] points, int eigenstateIndex ) {
        final double mass = getParticle().getMass();
        final int eigenstateSubscript = eigenstateIndex + 1; // Coulomb subscripts start with 1
        return ( 1 / BSCoulomb3DSolver.getScalingCoefficient( eigenstateSubscript, mass ) );
    }
    
    /*
     * Calculates the eignestates for the potential using an analytic solver.
     */
    protected BSEigenstate[] calculateEigenstates() {
        if ( getNumberOfWells() != 1 ) {
            throw new UnsupportedOperationException( "multiple wells are not supported" );
        }
        
        BSCoulomb3DSolver solver = new BSCoulomb3DSolver( this, getParticle() );
        
        ArrayList eigenstates = new ArrayList();
        int subscript = getGroundStateSubscript();
        
        for ( int n = 1; n <= NUMBER_OF_EIGENSTATES; n++ ) {
            double E = solver.getEnergy( n );
            eigenstates.add( new BSEigenstate( subscript, E ) );
            subscript++;
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}