/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * AbstractEnzyme is base class for all enzymes.
 * An enzyme feeds on ATP in the fluid and pulls on the DNA strand,
 * converting chemical energy into work.
 * <p>
 * The model herein is based on:
 * "Force production by single kinesin motors", Schnitzer et. al.,
 * Nature Cell Biology, Vol 2, October 2000, p. 718-723.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractEnzyme extends FixedObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_INNER_ORIENTATION = "innerOrientation";
    
    public static final double MAX_ROTATION_PER_CLOCK_STEP = Math.toRadians( 35 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrand;
    private Fluid _fluid;
    private final double _maxDt;
    
    // constants used in the velocity model
    private final double _c1, _c2, _c3, _c4, _c5, _c6, _c7, _c8, _d1, _d2;
    private final double _maxDNAStrandSpeed;
    
    private final double _outerDiameter, _innerDiameter;
    private double _innerOrientation;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractEnzyme( Point2D position, 
            double outerDiameter, double innerDiameter,
            DNAStrand dnaStrand, Fluid fluid,
            double maxDt,
            double c1, double c2, double c3, double c4, double c5, double c6, double c7, double c8, double d1, double d2 ) {
        super( position, 0 /* orientation */ );
        
        _outerDiameter = outerDiameter;
        _innerDiameter = innerDiameter;
        _innerOrientation = 0;
        _enabled = false;
        
        _dnaStrand = dnaStrand;
        _fluid = fluid;
        _maxDt = maxDt;
        
        _c1 = c1;
        _c2 = c2;
        _c3 = c3;
        _c4 = c4;
        _c5 = c5;
        _c6 = c6;
        _c7 = c7;
        _c8 = c8;
        _d1 = d1;
        _d2 = d2;
        
        _maxDNAStrandSpeed = getMaxDNAStrandSpeed();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getOuterDiameter() {
        return _outerDiameter;
    }
    
    public double getInnerDiameter() {
        return _innerDiameter;
    }
    
    public double getInnerOrientation() {
        return _innerOrientation;
    }
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifyObservers( PROPERTY_ENABLED );
        }
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
    
    //----------------------------------------------------------------------------
    // Model of DNA strand moving through enzyme
    //----------------------------------------------------------------------------
    
    /**
     * Gets the speed with which the DNA strand is moving through the enzyme.
     * <p>
     * This is erroneously refered to as velocity in the design document.
     * It is a function of the DNA force magnitude, so it has no orientation
     * and should be referred to as speed (the magnitude component of velocity).
     * 
     * @return speed (nm/s)
     */
    public double getDNAStrandSpeed() {
        final double atp = _fluid.getATPConcentration();
        final double fDNA = _dnaStrand.getForce().getMagnitude();
        return getDNAStrandSpeed( atp, fDNA );
    }
    
    /*
     * Gets the maximum speed with which the DNA strand is moving through the enzyme.
     * The speed will be at a maximum when the ATP concentration is at its maximum
     * and the DNA force is zero.
     */
    private double getMaxDNAStrandSpeed() {
        final double maxATP = _fluid.getATPConcentrationRange().getMax();
        return getDNAStrandSpeed( maxATP, 0 );
    }
    
    /*
     * Gets the speed with which the DNA strand is moving through the enzyme
     * for specific ATP and DNA force values.
     * 
     * @param atp ATP concentration
     * @param fDNA DNA force magnitude (pN)
     * @return speed (nm/s)
     */
    public double getDNAStrandSpeed( final double atp, final double fDNA ) {
        final double sMax = calcMaxSpeed( fDNA ); // V_Max in design doc
        final double km = calcRateConstant( fDNA ); // KM in design doc
        double speed = sMax * atp / ( atp + km ); // Velocity in design doc
        return speed;
    }
    
    /*
     * Max speed, referred to erroneously as V_Max in the design document.
     * 
     * @param fDNA DNA force magnitude
     * @return max speed (nm/s)
     */
    private double calcMaxSpeed( final double fDNA ) {
        return ( _d2 * _c1 ) / ( _c2 + ( _c3 * Math.exp( fDNA * _c4 ) ) );
    }
    
    /*
     * Rate constant, referred to as KM in the design document.
     * 
     * @param fDNA DNA force magnitude
     * @return KM
     */
    private double calcRateConstant( final double fDNA ) {
        return _d1 * ( _c6 + ( _c7 * Math.exp( fDNA * _c8 ) ) ) / ( _c2 + ( _c3 * Math.exp( fDNA * _c4 ) ) );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        if ( _enabled ) {
            final double dnaStrandSpeed = getDNAStrandSpeed();
//          System.out.println( "AbstractEnzyme.stepInTime dnaStrandSpeed=" + dnaStrandSpeed );//XXX
            final double speedScale = dnaStrandSpeed / _maxDNAStrandSpeed;
            final double dtScale = dt / _maxDt;
            final double deltaAngle = MAX_ROTATION_PER_CLOCK_STEP * speedScale * dtScale;
            _innerOrientation += deltaAngle;
            notifyObservers( PROPERTY_INNER_ORIENTATION );
        }
    }
}
