// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;


/**
 * TomDNASpeedStrategy uses an algorithm developed by Tom Perkins.
 * The semantics of the parameters is unknown.
 * See doc/dna-speed.pdf for the original Igor code for this model.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TomDNASpeedStrategy implements IDNASpeedStrategy {

    // speed when DNA force=0 and ATP concentration=infinite
    private static final double MAX_SPEED = 5000; // nm/s
    
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
    
    /*
     * Base class constructor.
     */
    protected TomDNASpeedStrategy( double maxSpeed, 
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
    public double getSpeed( final double atp, final double force ) {
        final double boltCat = Math.exp( force * _deltaCat / _kt );
        final double boltB = Math.exp( force * _deltaB / _kt );
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
    
    //----------------------------------------------------------------------------
    // Concrete subclasses
    //----------------------------------------------------------------------------
    
    /**
     * TomDNASpeedStrategyA is the strategy for use with EnzymeA.
     */
    public static class TomDNASpeedStrategyA extends TomDNASpeedStrategy {
        
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
     * TomDNASpeedStrategyB is the strategy for use with EnzymeB.
     */
    public static class TomDNASpeedStrategyB extends TomDNASpeedStrategy {
        
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
}