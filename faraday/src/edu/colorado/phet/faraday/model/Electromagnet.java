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
 * It is derived from the DipoleMagnet model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electromagnet extends DipoleMagnet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractCoil _coilModel;
    private boolean _isFlipped;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Electromagnet( AbstractCoil coilModel ) {
        super();
        assert( coilModel != null );
        
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        _isFlipped = false;
        
        update();
    }
    
    public void finalize() {
        _coilModel.removeObserver( this );
        _coilModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
     
        /* 
         * Set the magnet's size, based on the coil's physical appearance.
         * Our magnetic field model requires the magnet's size to be cylindrical
         * (width significantly > height), so we fudge the height.
         */
        double width = _coilModel.getNumberOfLoops() * _coilModel.getWireWidth();
        double height = width / FaradayConfig.ELECTROMAGNET_ASPECT_RATIO;
        super.setSize( width, height );
        
        // Get the voltage across the ends of the coil.
        double coilVoltage = _coilModel.getVoltage();
        
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
         * We set the strength of the magnet to be proportional to the voltage in the coil.
         */
        double percent = Math.abs( coilVoltage / _coilModel.getMaxVoltage() );
        double strength = percent * getMaxStrength();
        setStrength( strength );
    }
}
