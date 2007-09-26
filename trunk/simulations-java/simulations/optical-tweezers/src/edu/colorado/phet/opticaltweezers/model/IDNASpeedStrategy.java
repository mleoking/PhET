/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

/**
 * IDNASpeedStrategy is the interface for models that describe the speed with 
 * which an enzyme "pulls in" a bead attached to the end of a DNA strand.
 * <p>
 * Two concrete implementations are provided, named after the people who 
 * specified the models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
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
     * @param atp ATP concentration (arbitrary units)
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
    
    /**
     * KathyAbstractDNASpeedStrategy is the original model developed by Kathy Perkins,
     * and described in the design document.
     */
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
            
            private static final double[] CALIBRATION_CONSTANTS_B = { 4.79, 4.7, 0.09, 0.82, 2.1, 2, 0.1, 1.2 }; //XXX same as A, need different values!
            
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
         * @see IDNASpeedStrategy.getSpeed
         */
        public double getSpeed( final double atp, final double fDNA ) {
            final double maxSpeed = _maxSpeed * ( _c[0]  / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) ) );
            final double km = ( _c[0] / _c[4] ) * ( _c[5] + ( _c[6] * Math.exp( fDNA * _c[7] ) ) ) / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) );
            final double speed = maxSpeed * atp / ( atp + km );
            return speed;
        }
        
        /**
         * @see IDNASpeedStrategy.getMaxSpeed
         */
        public double getMaxSpeed() {
            return _maxSpeed;
        }
    }
    
    //----------------------------------------------------------------------------
    // Tom Perkins' model
    //----------------------------------------------------------------------------
    
    /**
     * TomAbstractDNASpeedStrategy is a later model developed by Tom Perkins.
     * It is not documented, and the semantics of the parameters is unknown.
     */
    public abstract class TomAbstractDNASpeedStrategy implements IDNASpeedStrategy {

        // speed when DNA force=0 and ATP concentration=infinite
        private static final double MAX_SPEED = 5000; // nm/s
        
        /**
         * TomDNASpeedStrategyA is the model for use with EnzymeA.
         */
        public static class TomDNASpeedStrategyA extends TomAbstractDNASpeedStrategy {
            
            // parameters, semantics unknown, modify at your peril
            private static final double KT = 4.1;
            private static final double KCAT0 = 700;
            private static final double KB0 = KCAT0 / 2;
            private static final double PC = 1;
            private static final double QC = 0.09;
            private static final double PB = 2;
            private static final double QB = 0.1;
            private static final double DELTA_CAT = 4 * 0.82;
            private static final double DELTA_B = 4 * 1.2;
            private static final double D = 8;
            
            public TomDNASpeedStrategyA() {
                super( MAX_SPEED, KT, KCAT0, KB0, PC, QC, PB, QB, DELTA_CAT, DELTA_B, D );
            }
        }
        
        /**
         * TomDNASpeedStrategyB is the model for use with EnzymeB.
         */
        public static class TomDNASpeedStrategyB extends TomAbstractDNASpeedStrategy {
            
            // parameters, semantics unknown, modify at your peril
            private static final double KT = 4.1;
            private static final double KCAT0 = 6500;
            private static final double KB0 = KCAT0 / 2;
            private static final double PC = 10;
            private static final double QC = 0.09;
            private static final double PB = 20;
            private static final double QB = 0.1;
            private static final double DELTA_CAT = 4 * 0.01;
            private static final double DELTA_B = 4 * 1.4;
            private static final double D = 8;
            
            public TomDNASpeedStrategyB() {
                super( MAX_SPEED, KT, KCAT0, KB0, PC, QC, PB, QB, DELTA_CAT, DELTA_B, D );
            }
        }
        
        // speed when DNA force=0 and ATP concentration=infinite
        private final double _maxSpeed;
        
        // parameters, semantics unknown
        private final double _kt;
        private final double _kcat0;
        private final double _kb0;
        private final double _pc;
        private final double _qc;
        private final double _pb;
        private final double _qb;
        private final double _deltaCat;
        private final double _deltaB;
        private final double _d;
        
        /**
         * TomAbstractDNASpeedStrategy is the base class for all of Tom's models.
         */
        public TomAbstractDNASpeedStrategy( double maxSpeed, 
                double kt, double kcat0, double kb0, double pc, double qc, 
                double pb, double qb, double deltaCat, double deltaB, double d ) {
            
            _maxSpeed = maxSpeed;
            
            _kt = kt;
            _kcat0 = kcat0;
            _kb0 = kb0;
            _pc = pc;
            _qc = qc;
            _pb = pb;
            _qb = qb;
            _deltaCat = deltaCat;
            _deltaB = deltaB;
            _d = d;
        }
        
        /**
         * @see IDNASpeedStrategy.getSpeed
         */
        public double getSpeed( final double atp, final double fDNA ) {
            final double boltCat = Math.exp( fDNA * _deltaCat / _kt );
            final double boltB = Math.exp( fDNA * _deltaB / _kt );
            final double kcat = _kcat0 / ( _pc + ( _qc * boltCat ) );
            final double kb = _kb0 / ( _pb + ( _qb * boltB ) );
            final double speed = _d * kcat * atp / ( atp + ( kcat / kb ) );
            return speed;
        }
        
        /**
         * @see IDNASpeedStrategy.getMaxSpeed
         */
        public double getMaxSpeed() {
            return _maxSpeed;
        }
    }
}
