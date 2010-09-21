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

import java.util.ArrayList;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.SchmidtLeeSolver.SchmidtLeeException;


/**
 * BSHarmonicOscillatorPotential is the model of a potential composed
 * of one Harmonic Oscillator well.
 * This implementation does not support multiple wells.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>offset
 * <li>angular frequency
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorPotential extends BSAbstractPotential {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Determines whether to use analytic or Schmit-Lee solver for eigenstates
    private static final boolean USE_ANALYTIC_EIGENSTATE_SOLVER = true;
    
    // How far above the offset we'll look for eigenstates
    private static final double EIGENSTATE_ENERGY_RANGE = 20; // eV
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _angularFrequency; // angular frequency in fs^-1
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param particle
     * @param offset
     * @param angularFrequency
     */
    public BSHarmonicOscillatorPotential( BSParticle particle, double offset, double angularFrequency ) {
        super( particle, 1 /* numberOfWells */, offset );
        setAngularFrequency( angularFrequency );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the angular frequency.
     * 
     * @return angular frequency, in fs^-1
     */
    public double getAngularFrequency() {
        return _angularFrequency;
    }

    /**
     * Sets the angular frequency.
     * 
     * @param angularFrequency angular frequency, in fs^1
     */
    public void setAngularFrequency( double angularFrequency ) {
        if ( angularFrequency != _angularFrequency ) {
            _angularFrequency = angularFrequency;
            markEigenstatesDirty();
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    /**
     * Gets the type of well used in the potential.
     * Potentials in this simulation are composed of homogeneous well types.
     * 
     * @return BSWellType.HARMONIC_OSCILLATOR
     */
    public BSWellType getWellType() {
        return BSWellType.HARMONIC_OSCILLATOR;
    }

    /**
     * Multiple wells are not supported.
     * @return false
     */
    public boolean supportsMultipleWells() {
        return false;
    }
    
    /**
     * The ground state is E0.
     * @return 0
     */
    public int getGroundStateSubscript() {
        return 0;
    }

    /**
     * Gets the energy at a specified position.
     * 
     * @param position the position, in nm
     */
    public double getEnergyAt( double position ) {
        assert( getNumberOfWells() == 1 );  // this solution works only for single wells
        
        final double offset = getOffset();
        final double c = getCenter();
        final double m = getParticle().getMass();
        final double w = getAngularFrequency();
        
        return offset + ( 0.5 * m * w * w * ( position - c ) * ( position - c ) );
    }

    /**
     * Calculates the eigenstates.
     * 
     * En = h * w * ( n + 0.5 )
     * where:
     * n = 0, 1, 2,....
     * h = hbar
     * w = angular frequency
     */
    protected BSEigenstate[] calculateEigenstates() {
        assert ( getNumberOfWells() == 1 ); // this solution works only for single wells
        if ( USE_ANALYTIC_EIGENSTATE_SOLVER ) {
            return calculateEigenstatesAnalytic();
        }
        else {
            return calculateEigenstatesSchmidtLee();
        }
    }
    
    /*
     * Calculates the eigenstates using the Schmidt-Lee algorithm.
     * Start at the ground state and continue based on the energy range we're interested in.
     */
    private BSEigenstate[] calculateEigenstatesSchmidtLee() {
        
        SchmidtLeeSolver solver = getEigenstateSolver();
        ArrayList eigenstates = new ArrayList();
        final double maxE = getOffset() + EIGENSTATE_ENERGY_RANGE;
        int nodes = 0;

        boolean done = false;
        while ( !done ) {
            try {
                double E = solver.getEnergy( nodes );
                if ( E <= maxE ) {
                    final int subscript = nodes; // subscripts start at 0 for harmonics oscillator
                    eigenstates.add( new BSEigenstate( subscript, E ) );
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
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
    
    /*
     * Calculates the eigenstates by calculating En directly.
     * Start at the ground state and continue based on the energy range we're interested in.
     * The analytic solution is as follows:
     * 
     * En = ( h * w * ( n + 0.5 ) ) + offset
     * 
     * where:
     *     n = 0, 1, 2,....
     *     h = hbar
     *     w = angular frequency
     */
    private BSEigenstate[] calculateEigenstatesAnalytic() {
        ArrayList eigenstates = new ArrayList();
        
        boolean done = false;
        int n = 0;
        final double maxE = getOffset() + EIGENSTATE_ENERGY_RANGE;
        while ( !done ) {
            double E = ( BSConstants.HBAR * _angularFrequency * ( n + 0.5 ) ) + getOffset();
            if ( E <= maxE ) {
                final int subscript = n; // subscripts start at 0 for harmonics oscillator
                eigenstates.add( new BSEigenstate( subscript, E ) );
            }
            else {
                done = true;
            }
            n++;
        }

        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
    

}
