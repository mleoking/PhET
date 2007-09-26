/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;


public interface IDNASpeedStrategy {

    //----------------------------------------------------------------------------
    // Interface required for all models
    //----------------------------------------------------------------------------
    
    /**
     * Gets the speed at which the DNA strand is moving through the enzyme
     * for specific ATP and DNA force values.
     * <p>
     * This is erroneously refered to as velocity in the design document.
     * It is a function of the DNA force magnitude, so it has no orientation
     * and should be referred to as speed (the magnitude component of velocity).
     * 
     * @param atp ATP concentration
     * @param fDNA DNA force magnitude (pN)
     * @return speed (nm/sec)
     */
    public double getSpeed( final double atp, final double fDNA );
    
    /**
     * Gets the maximum speed, when DNA force=0 and ATP concentration=infinite.
     */
    public double getMaxSpeed();
    
    //----------------------------------------------------------------------------
    // Kathy Perkins' model
    //----------------------------------------------------------------------------
    
    public abstract class KathyAbstractDNASpeedStrategy implements IDNASpeedStrategy {

        // speed when DNA force=0 and ATP concentration=infinite
        private static final double MAX_SPEED = 5000; // nm/s
        
        /**
         * KathyDNASpeedStrategyA is the model for use with EnzymeA.
         */
        public static class KathyDNASpeedStrategyA extends KathyAbstractDNASpeedStrategy {
            
            private static final double[] CALIBRATION_CONSTANTS_A = { 4.79, 4.7, 0.09, 0.82, 2.1, 2, 0.1, 1.2 };
            
            public KathyDNASpeedStrategyA() {
                super( MAX_SPEED, CALIBRATION_CONSTANTS_A );
            }
        }
        
        /**
         * KathyDNASpeedStrategyB is the model for use with EnzymeB.
         */
        public static class KathyDNASpeedStrategyB extends KathyAbstractDNASpeedStrategy {
            
            private static final double[] CALIBRATION_CONSTANTS_B = { 4.79, 4.7, 0.09, 0.82, 2.1, 2, 0.1, 1.2 }; //XXX need different values!
            
            public KathyDNASpeedStrategyB() {
                super( MAX_SPEED, CALIBRATION_CONSTANTS_B );
            }
        }
        
        // speed when DNA force=0 and ATP concentration=infinite
        private final double _maxSpeed;
        // calibration constants for DNA speed model (c1 thru c8 in design doc)
        private final double[] _c;
        
        /**
         * KathyAbstractDNASpeedStrategy is the base class for all of Kathy's models.
         */
        public KathyAbstractDNASpeedStrategy( double maxSpeed, double[] calibrationConstants ) {
            _maxSpeed = maxSpeed;
            _c = calibrationConstants;
            assert( _c.length == 8 ); // we should have 8 calibration constants
        }
        
        /**
         * Gets the speed at which the DNA strand is moving through the enzyme
         * for specific ATP and DNA force values.
         * 
         * @param atp ATP concentration
         * @param fDNA DNA force magnitude (pN)
         * @return speed (nm/sec)
         */
        public double getSpeed( final double atp, final double fDNA ) {
            final double maxSpeed = _maxSpeed * ( _c[0]  / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) ) );
            final double km = ( _c[0] / _c[4] ) * ( _c[5] + ( _c[6] * Math.exp( fDNA * _c[7] ) ) ) / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) );
            final double speed = maxSpeed * atp / ( atp + km );
            return speed;
        }
        
        /**
         * Gets the maximum speed, when DNA force=0 and ATP concentration=infinite.
         */
        public double getMaxSpeed() {
            return _maxSpeed;
        }
    }
    
    //----------------------------------------------------------------------------
    // Tom Perkins' model
    //----------------------------------------------------------------------------
    
    public abstract class TomAbstractDNASpeedStrategy implements IDNASpeedStrategy {

        // speed when DNA force=0 and ATP concentration=infinite
        private static final double MAX_SPEED = 5000; // nm/s
        
        /**
         * TomDNASpeedStrategyA is the model for use with EnzymeA.
         */
        public static class TomDNASpeedStrategyA extends TomAbstractDNASpeedStrategy {
            public TomDNASpeedStrategyA() {
                super( MAX_SPEED );
            }
        }
        
        /**
         * TomDNASpeedStrategyB is the model for use with EnzymeB.
         */
        public static class TomDNASpeedStrategyB extends TomAbstractDNASpeedStrategy {
            public TomDNASpeedStrategyB() {
                super( MAX_SPEED );
            }
        }
        
        // speed when DNA force=0 and ATP concentration=infinite
        private final double _maxSpeed;
        
        /**
         * TomAbstractDNASpeedStrategy is the base class for all of Tom's models.
         */
        public TomAbstractDNASpeedStrategy( double maxSpeed ) {
            _maxSpeed = maxSpeed;
        }
        
        /**
         * Gets the speed at which the DNA strand is moving through the enzyme
         * for specific ATP and DNA force values.
         * 
         * @param atp ATP concentration
         * @param fDNA DNA force magnitude (pN)
         * @return speed (nm/sec)
         */
        public double getSpeed( final double atp, final double fDNA ) {
            return 0; //XXX not implemented
        }
        
        /**
         * Gets the maximum speed, when DNA force=0 and ATP concentration=infinite.
         */
        public double getMaxSpeed() {
            return _maxSpeed;
        }
    }
}
