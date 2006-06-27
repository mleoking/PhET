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

import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorWell;
import edu.colorado.phet.boundstates.model.BSParticle;

/**
 * BSHarmonicOscillatorConfig is a Java Bean used for XML encoding the state 
 * of a harmonic oscillator potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorConfig implements BSSerializable {
    
    private double _offset;
    private double _angularFrequency;
    
    public BSHarmonicOscillatorConfig() {}
    
    public BSHarmonicOscillatorConfig( BSHarmonicOscillatorWell potential ) {
        _offset = potential.getOffset();
        _angularFrequency = potential.getAngularFrequency();
    }
    
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
    
    public BSHarmonicOscillatorWell toPotential( BSParticle particle ) {
        return new BSHarmonicOscillatorWell( particle, _offset, _angularFrequency );
    }
}
