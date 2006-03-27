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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;


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
public class BSHarmonicOscillatorWell extends BSAbstractPotential implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _angularFrequency;
    private BSParticle _particle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSHarmonicOscillatorWell( BSParticle particle ) {
        this( particle, BSConstants.DEFAULT_OSCILLATOR_OFFSET, BSConstants.DEFAULT_OSCILLATOR_ANGULAR_FREQUENCY );
    }
    
    public BSHarmonicOscillatorWell( BSParticle particle, double offset, double angularFrequency ) {
        super( 1, 0, offset, BSConstants.DEFAULT_WELL_CENTER );
        setAngularFrequency( angularFrequency );
        setParticle( particle );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public double getAngularFrequency() {
        return _angularFrequency;
    }

    public void setAngularFrequency( double angularFrequency ) {
        _angularFrequency = angularFrequency;
        notifyObservers();
    }

    public BSParticle getParticle() {
        return _particle;
    }
    
    public void setParticle( BSParticle particle ) {
        if ( _particle != null ) {
            _particle.deleteObserver( this );
        }
        _particle = particle;
        _particle.addObserver( this );
        notifyObservers();
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != 1 ) {
            throw new UnsupportedOperationException( "mutiple wells not supported for harmonic oscillator well" );
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
        assert( getNumberOfWells() == 1 );
        
        final double offset = getOffset();
        final double c = getCenter();
        final double m = _particle.getMass();
        final double w = getAngularFrequency();
        
        return offset + ( 0.5 * m * w * w * ( position - c ) * ( position - c ) );
    }

    //HACK dummy eigenstates, evenly spaced above the offset
    public BSEigenstate[] getEigenstates() {
        BSEigenstate[] eigenstates = new BSEigenstate[5];
        for ( int i = 0; i < eigenstates.length; i++ ) {
            eigenstates[i] = new BSEigenstate( getOffset() + ( i * 0.5 ) );
        }
        return eigenstates;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _particle ) {
            notifyObservers();
        }   
    }
}
