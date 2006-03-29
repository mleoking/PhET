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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import sun.security.krb5.internal.ccache.an;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.test.schmidt_lee.PotentialFunction;
import edu.colorado.phet.boundstates.test.schmidt_lee.Wavefunction;


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
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != 1 ) {
            throw new UnsupportedOperationException( "mutiple harmonic oscillator wells are not supported" );
        }
        else {
            super.setNumberOfWells( numberOfWells );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    public BSWellType getWellType() {
        return BSWellType.HARMONIC_OSCILLATOR;
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
     * En = h * w * ( n + 0.5 )
     * where:
     * n = 0, 1, 2,....
     * h = hbar
     * w = angular frequency
     */
    public BSEigenstate[] getEigenstates() {
        assert( getNumberOfWells() == 1 ); // this solution works only for single wells
        System.out.println( "BSHarmonicOscillatorWell.getEigenestates, numberOfWells=" + getNumberOfWells() );//XXX
        return getEigenstatesSimple();
//        return getEigenstatesSchmidtLee();
    }
    
    /*
     * Gets the eigenstates using the Schmidt-Lee algorithm.
     */
    private BSEigenstate[] getEigenstatesSchmidtLee() {
        
        ArrayList eigenstates = new ArrayList();
        
        final double maxE = getOffset() + BSConstants.ENERGY_RANGE.getLength(); //XXX
        int nodes = 0;
        boolean done = false;
        while ( !done ) {
            try {
                PotentialFunction function = new PotentialFunctionAdapter( this );
                Wavefunction wavefunction = new Wavefunction( 0.5, -4, +4, 1000, nodes, function );
                double E = wavefunction.getE();
                if ( E <= maxE ) {
                    eigenstates.add( new BSEigenstate( E ) );
                }
                else {
                    done = true;
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
            nodes++;
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
    
    /*
     * Gets the eigenstates by calculating En directly, as follows:
     * 
     * En = h * w * ( n + 0.5 )
     * 
     * where:
     *     n = 0, 1, 2,....
     *     h = hbar
     *     w = angular frequency
     */
    private BSEigenstate[] getEigenstatesSimple() {
        ArrayList eigenstates = new ArrayList();
        
        boolean done = false;
        int n = 0;
        final double maxE = getOffset() + BSConstants.ENERGY_RANGE.getLength(); //XXX
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
