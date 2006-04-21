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
 * BSHarmonicOscillatorWell is the model of a potential composed of one Harmonic Oscillator well.
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
public class BSHarmonicOscillatorWell extends BSAbstractPotential{

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _angularFrequency;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSHarmonicOscillatorWell( BSParticle particle ) {
        this( particle, BSConstants.DEFAULT_HARMONIC_OSCILLATOR_OFFSET, BSConstants.DEFAULT_HARMONIC_OSCILLATOR_ANGULAR_FREQUENCY );
    }
    
    public BSHarmonicOscillatorWell( BSParticle particle, double offset, double angularFrequency ) {
        super( particle, 1, 0, offset, BSConstants.DEFAULT_WELL_CENTER );
        setAngularFrequency( angularFrequency );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public double getAngularFrequency() {
        return _angularFrequency;
    }

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
    
    public BSWellType getWellType() {
        return BSWellType.HARMONIC_OSCILLATOR;
    }

    public boolean supportsMultipleWells() {
        return false;
    }
    
    public int getStartingIndex() {
        return 0;
    }

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
        assert( getNumberOfWells() == 1 ); // this solution works only for single wells
//        return calculateEigenstatesAnalytic();
        return calculateEigenstatesSchmidtLee();
    }
    
    /*
     * Calculates the eigenstates using the Schmidt-Lee algorithm.
     */
    private BSEigenstate[] calculateEigenstatesSchmidtLee() {
        
        SchmidtLeeSolver solver = getEigenstateSolver();
        ArrayList eigenstates = new ArrayList();
        final double maxE = getOffset() + BSConstants.ENERGY_RANGE.getLength();
        int nodes = 0;

        boolean done = false;
        while ( !done ) {
            try {
                double E = solver.getEnergy( nodes );
                if ( E <= maxE ) {
                    eigenstates.add( new BSEigenstate( E ) );
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
        
        // Ensure that they appear in ascending order...
        Collections.sort( eigenstates );
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
    
    /*
     * Calculates the eigenstates by calculating En directly, as follows:
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
        final double maxE = getOffset() + BSConstants.ENERGY_RANGE.getLength();
        while ( !done ) {
            double E = ( BSConstants.HBAR * _angularFrequency * ( n + 0.5 ) ) + getOffset();
            if ( E <= maxE ) {
                eigenstates.add( new BSEigenstate( E ) );
            }
            else {
                done = true;
            }
            n++;
        }

        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
    

}
