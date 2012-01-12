// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

/**
 * KathyDNASpeedStrategy uses an algorithm developed by Kathy Perkins,
 * as described in the design document (doc/optical-tweezers.pdf).
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class KathyDNASpeedStrategy implements IDNASpeedStrategy {

    // speed when DNA force=0 and ATP concentration=infinite
    private static final double MAX_SPEED = 5000; // nm/s
    
    // speed when DNA force=0 and ATP concentration=infinite
    private final double _maxSpeed;
    // calibration constants for DNA speed model (c1 thru c8 in design doc)
    private final double[] _c;
    
    /*
     * Base class constructor.
     */
    protected KathyDNASpeedStrategy( double maxSpeed, double[] calibrationConstants ) {
        _maxSpeed = maxSpeed;
        _c = calibrationConstants;
        assert( _c.length == 8 ); // we should have 8 calibration constants
    }
    
    /**
     * @see IDNASpeedStrategy.getSpeed
     */
    public double getSpeed( final double atp, final double force ) {
        final double maxSpeed = _maxSpeed * ( _c[0]  / ( _c[1] + ( _c[2] * Math.exp( force * _c[3] ) ) ) );
        final double km = ( _c[0] / _c[4] ) * ( _c[5] + ( _c[6] * Math.exp( force * _c[7] ) ) ) / ( _c[1] + ( _c[2] * Math.exp( force * _c[3] ) ) );
        final double speed = maxSpeed * atp / ( atp + km );
        return speed;
    }
    
    /**
     * @see IDNASpeedStrategy.getMaxSpeed
     */
    public double getMaxSpeed() {
        return _maxSpeed;
    }
    
    //----------------------------------------------------------------------------
    // Concrete subclasses
    //----------------------------------------------------------------------------
    
    /**
     * KathyDNASpeedStrategyA is the strategy for use with EnzymeA.
     */
    public static class KathyDNASpeedStrategyA extends KathyDNASpeedStrategy {
        
        private static final double[] CALIBRATION_CONSTANTS_A = { 4.79, 4.7, 0.09, 0.82, 2.1, 2, 0.1, 1.2 };
        
        public KathyDNASpeedStrategyA() {
            super( MAX_SPEED, CALIBRATION_CONSTANTS_A );
        }
    }
    
    /**
     * KathyDNASpeedStrategyB is the strategy for use with EnzymeB.
     */
    public static class KathyDNASpeedStrategyB extends KathyDNASpeedStrategy {
        
        private static final double[] CALIBRATION_CONSTANTS_B = { 4.79, 4.7, 0.09, 0.82, 2.1, 2, 0.1, 1.2 }; //XXX same as A, need different values!
        
        public KathyDNASpeedStrategyB() {
            super( MAX_SPEED, CALIBRATION_CONSTANTS_B );
        }
    }
}