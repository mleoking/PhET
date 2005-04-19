/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * Electromagnet is the model of an electromagnet.
 * It is derived from the CoilMagnet model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electromagnet extends CoilMagnet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SourceCoil _sourceCoilModel;
    private boolean _isFlipped;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Electromagnet( SourceCoil sourceCoilModel ) {
        super();
        assert( sourceCoilModel != null );
        
        _sourceCoilModel = sourceCoilModel;
        _sourceCoilModel.addObserver( this );
        
        _isFlipped = false;
        
        update();
    }
    
    public void finalize() {
        _sourceCoilModel.removeObserver( this );
        _sourceCoilModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
     
        // The magnet size is a circle that has the same radius as the coil.
        double diameter = 2 * _sourceCoilModel.getRadius();
        super.setSize( diameter, diameter );
        
        // Get the voltage across the ends of the coil.
        double coilVoltage = _sourceCoilModel.getVoltage();
        
        // Flip the polarity
        if ( coilVoltage >= 0 && _isFlipped ) {
            flipPolarity();
            _isFlipped = false;
        }
        else if ( coilVoltage < 0 && !_isFlipped ) {
            flipPolarity();
            _isFlipped = true;
        }
        
        /* 
         * Set the strength.
         * This is a bit of a "fudge". 
         * We set the strength of the magnet to be proportional to the 
         * amplitude of the voltage in the coil.
         */
        double amplitude = Math.abs( _sourceCoilModel.getAmplitude() );
        double strength = amplitude * getMaxStrength();
        setStrength( strength );
    }
}