/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;

import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.model.BSParticle;

/**
 * BSHarmonicOscillatorConfig is a Java Bean used for XML encoding the state 
 * of a harmonic oscillator potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorConfig implements BSSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _offset;
    private double _angularFrequency;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSHarmonicOscillatorConfig() {}
    
    public BSHarmonicOscillatorConfig( BSHarmonicOscillatorPotential potential ) {
        _offset = potential.getOffset();
        _angularFrequency = potential.getAngularFrequency();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public double getAngularFrequency() {
        return _angularFrequency;
    }
    
    public void setAngularFrequency( double angularFrequency ) {
        _angularFrequency = angularFrequency;
    }
    
    public double getOffset() {
        return _offset;
    }

    public void setOffset( double offset ) {
        _offset = offset;
    }
    
    //----------------------------------------------------------------------------
    // Conversions
    //----------------------------------------------------------------------------
    
    public BSHarmonicOscillatorPotential toPotential( BSParticle particle ) {
        return new BSHarmonicOscillatorPotential( particle, _offset, _angularFrequency );
    }
}
