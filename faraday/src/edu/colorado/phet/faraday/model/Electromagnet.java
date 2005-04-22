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

import edu.colorado.phet.common.math.MathUtil;
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
    private AbstractVoltageSource _voltageSource;
    private boolean _isFlipped;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param sourceCoilModel the electromagnet's coil
     * @param voltageSource the electromagnet's voltage source
     */
    public Electromagnet( SourceCoil sourceCoilModel, AbstractVoltageSource voltageSource ) {
        super();
        assert( sourceCoilModel != null );
        assert( voltageSource != null );
        
        _sourceCoilModel = sourceCoilModel;
        _sourceCoilModel.addObserver( this );
        
        _voltageSource = voltageSource;
        _voltageSource.addObserver( this );
        
        _isFlipped = false;
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _sourceCoilModel.removeObserver( this );
        _sourceCoilModel = null;
        
        if ( _voltageSource != null ) {
            _voltageSource.removeObserver( this );
            _voltageSource = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the electromagnet's voltage source.
     * 
     * @param voltageSource
     */
    public void setVoltageSource( AbstractVoltageSource voltageSource ) {
        assert( voltageSource != null );
        if ( voltageSource != _voltageSource ) {
            if ( _voltageSource != null ) {
                _voltageSource.removeObserver( this );
            }
            _voltageSource = voltageSource;
            _voltageSource.addObserver( this );
            update();
        }
    }
    
    /**
     * Gets the eletromagnet's voltage source.
     * 
     * @return the voltage source
     */
    public AbstractVoltageSource getVoltageSource() {
        return _voltageSource;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates current in the coil and strength of the magnet.
     */
    public void update() {
     
        /* 
         * The magnet size is a circle that has the same radius as the coil.
         * Adding half the wire width makes it look a little better.
         */
        double diameter = ( 2 * _sourceCoilModel.getRadius() ) +  ( _sourceCoilModel.getWireWidth() / 2 );
        super.setSize( diameter, diameter );
        
        // Current amplitude is proportional to voltage amplitude.
        _sourceCoilModel.setCurrentAmplitude( _voltageSource.getAmplitude() );
        
        // Compute the electromagnet's emf amplitude.
        double amplitude = ( _sourceCoilModel.getNumberOfLoops() / (double) FaradayConfig.ELECTROMAGNET_LOOPS_MAX ) * _voltageSource.getAmplitude();
        amplitude = MathUtil.clamp( -1, amplitude, 1 );
        
        // Flip the polarity
        if ( amplitude >= 0 && _isFlipped ) {
            flipPolarity();
            _isFlipped = false;
        }
        else if ( amplitude < 0 && !_isFlipped ) {
            flipPolarity();
            _isFlipped = true;
        }
        
        /* 
         * Set the strength.
         * This is a bit of a "fudge". 
         * We set the strength of the magnet to be proportional to its emf.
         */
        double strength = Math.abs( amplitude ) * getMaxStrength();
        setStrength( strength );
    }
}