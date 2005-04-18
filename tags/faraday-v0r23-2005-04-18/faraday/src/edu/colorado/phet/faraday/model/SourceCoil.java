/* Copyright 2005, University of Colorado */

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
 * SourceCoil
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SourceCoil extends AbstractCoil implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractVoltageSource _voltageSource;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public SourceCoil() {
        super();
        
        // pack the loops close together
        setLoopSpacing( getWireWidth() );
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        if ( _voltageSource != null ) {
            _voltageSource.removeObserver( this );
            _voltageSource = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the voltage source attached to the coil.
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
            super.setMaxVoltage( _voltageSource.getMaxVoltage() );
            update();
        }
    }
    
    /**
     * Gets the voltage source attached to the coil.
     * 
     * @return the voltage source
     */
    public AbstractVoltageSource getVoltageSource() {
        return _voltageSource;
    }

    //----------------------------------------------------------------------------
    // SpacialObserver overrides
    //----------------------------------------------------------------------------
    
    /*
     * Ensure that the coil is updated when inherited setters are called.
     */
    protected void updateSelf() {
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        if ( isEnabled() && _voltageSource != null ) {

            /*
             * The amplitude of the voltage in the source coil is a based on
             * the number of loops in the coil and the relative magnitude of
             * the voltage supplied by the voltage source.
             */
            double amplitude = ( getNumberOfLoops() / (double) FaradayConfig.ELECTROMAGNET_LOOPS_MAX ) * _voltageSource.getAmplitude();
            super.setAmplitude( amplitude );
            
            notifyObservers();
        }
    }
}
