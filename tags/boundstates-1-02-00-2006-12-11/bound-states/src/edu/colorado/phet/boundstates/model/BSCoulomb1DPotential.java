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
import java.util.Iterator;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.SchmidtLeeSolver.SchmidtLeeException;


/**
 * BSCoulomb1DPotential is the model of a potential composed of 
 * one or more 1-dimensional Coulomb wells.
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
public class BSCoulomb1DPotential extends BSAbstractPotential {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _spacing; // spacing, in nm
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param particle
     * @param numberOfWells
     * @param spacing
     */
    public BSCoulomb1DPotential( BSParticle particle, int numberOfWells, double offset, double spacing ) {
        super( particle, numberOfWells, offset );
        setSpacing( spacing );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the spacing.
     * The spacing is the distance between the center of
     * each pair of adjacent wells, and is the same for all wells.
     * 
     * @return the spacing, in nm
     */
    public double getSpacing() {
        return _spacing;
    }

    /**
     * Sets the spacing.
     * The spacing is the distance between the center of
     * each pair of adjacent wells, and is the same for all wells.
     * Spacing of zero is OK.
     *      
     * @param spacing the spacing, >= 0, in nm
     */
    public void setSpacing( double spacing ) {
        if ( spacing < 0 ) {
            throw new IllegalArgumentException( "invalid spacing: " + spacing );
        }
        if ( spacing != _spacing ) {
            _spacing = spacing;
            markEigenstatesDirty();
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /**
     * Gets the wave function approximation.
     * If we have a single well, use the analytic solver.
     * For multiple wells, use the default solver (Schmidt-Lee).
     */
    public Point2D[] getWaveFunctionPoints( BSEigenstate eigenstate, final double minX, final double maxX ) {
        if ( getNumberOfWells() == 1 ) {
            final int numberOfPoints = BSConstants.COULOMB_ANALYTIC_SAMPLE_POINTS;
            BSCoulomb1DSolver solver = new BSCoulomb1DSolver( this, getParticle() );
            return solver.getWaveFunction( eigenstate.getSubscript(), minX, maxX, numberOfPoints );
        }
        else {
            return super.getWaveFunctionPoints( eigenstate, minX, maxX );
        }
    }
    
    /**
     * Gets the coefficient required to normalize a wave function.
     * If we have a single well, use the analytic solver.
     * For multiple wells, use the default solver (Schmidt-Lee).
     * 
     * @param points
     * @param eigenstateIndex 0,...n
     * @return double
     */
    public double getNormalizationCoefficient( Point2D[] points, int eigenstateIndex ) {
        if ( getNumberOfWells() == 1 ) {
            final int eigenstateSubscript = eigenstateIndex + 1; // Coulomb subscripts start with 1
            return ( 1 / BSCoulomb1DSolver.getScalingCoefficient( eigenstateSubscript ) );
        }
        else {
            return super.getNormalizationCoefficient( points, eigenstateIndex );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    /**
     * Gets the type of well used in the potential.
     * Potentials in this simulation are composed of homogeneous well types.
     * 
     * @return BSWellType.COULOMB_1D
     */
    public BSWellType getWellType() {
        return BSWellType.COULOMB_1D;
    }
    
    /**
     * Multiple wells are supported.
     * @return true
     */
    public boolean supportsMultipleWells() {
        return true;
    }
    
    /**
     * The ground state is E1.
     * @return 1
     */
    public int getGroundStateSubscript() {
        return 1;
    }

    /**
     * Gets the energy at a specified position.
     * 
     * @param position the position, in nm
     */
    public double getEnergyAt( double position ) {

        double energy = 0;
        
        final int n = getNumberOfWells();
        final double kee = BSConstants.KE2;
        final double s = getSpacing();
        final double offset = getOffset();
        final double c = getCenter();
        
        for ( int i = 1; i <= n; i++ ) {
            final double xi = s * ( i - ( ( n + 1  ) / 2.0 ) );
            energy += -kee / Math.abs( (position-c) - xi );
        }
        
        // Apply field constant
        energy += ( getFieldConstant() * position );
        
        return offset + energy;
    }
 
    /*
     * Calculates the eigenstates.
     * For the single-well case, use an analytic solver.
     * For multiple-wells, use the Schmidt-Lee solver.
     */
    protected BSEigenstate[] calculateEigenstates() {
        if ( getNumberOfWells() == 1 ) {
            return calculateEigenstatesAnalytic();
        }
        else {
            return calculateEigenstatesSchmidtLee();
        }
    }
    
    /*
     * Calculates the eigenstates using Sam McKagan's analytic solution.
     */
    private BSEigenstate[] calculateEigenstatesAnalytic() {
        
        BSCoulomb1DSolver solver = new BSCoulomb1DSolver( this, getParticle() );
        
        ArrayList eigenstates = new ArrayList();
        final int numberOfEigenstates = solver.getMaxEigenstates();
        final double cutOffEnergy = getEnergyCutOff();
        int subscript = getGroundStateSubscript();
        
        for ( int n = 1; n <= numberOfEigenstates; n++ ) {
            double E = solver.getEnergy( n );
            if ( E < cutOffEnergy ) {
                eigenstates.add( new BSEigenstate( subscript, E ) );
                subscript++;
            }
            else {
                break;
            }
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
    
    /*
     * Calculates the eigenstates using the Schmidt-Lee solver.
     * 
     * We start from the ground state and work upward.
     * 
     * Skips ever-other "cluster" of nodes, where the cluster size 
     * is equal to the number of wells in the potential.
     * We do this because every-other cluster of eigenstates is unstable.
     * Label the eigenstates with consecutive subscripts, (E1, E2, E3,...)
     * regardless of the node number used to solve.
     * 
     * Examples:
     * 1 well:  skip node0, solve node1, skip node2, solve node3, etc.
     * 2 wells: skip node0-1, solve node2-3, skip node4-5, solve node6-7, etc.
     * 3 wells: skip node0-2, solve node3-5, skip node6-8, solve node9-11, etc.
     */
    private BSEigenstate[] calculateEigenstatesSchmidtLee() {

        SchmidtLeeSolver solver = getEigenstateSolver();
        ArrayList eigenstates = new ArrayList(); // array of BSEigentate
        final int numberOfWells = getNumberOfWells();
        final double cutOffEnergy = getEnergyCutOff();
        
        // Calculate the eigentates...
        int nodes = numberOfWells; // skip the first cluster
        int subscript = getGroundStateSubscript();
        boolean done = false;
        while ( !done ) {
            
            // Add the cluster's eigenstates, up to the cut off...
            for ( int i = 0; !done && i < numberOfWells; i++ ) {
                try {
                    double E = solver.getEnergy( nodes );
                    if ( E <= cutOffEnergy ) {
                        eigenstates.add( new BSEigenstate( subscript, E ) );
                    }
                    else {
                        done = true;
                    }
                }
                catch ( SchmidtLeeException sle ) {
                    System.err.println( sle.getClass() + ": " + sle.getMessage() );
                    done = true;
                }
                nodes++;
                subscript++;
            }
            
            // skip the next cluster
            nodes += numberOfWells;
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}
