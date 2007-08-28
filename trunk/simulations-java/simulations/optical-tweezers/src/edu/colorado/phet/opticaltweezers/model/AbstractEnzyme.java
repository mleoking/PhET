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
    
    // speed when DNA force=0 and ATP concentration=infinite
    private final double _maxDNASpeed;
    // calibration constants for DNA speed model
    private final double _c1, _c2, _c3, _c4, _c5, _c6, _c7, _c8;
    
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
            double maxDNASpeed,
            double c1, double c2, double c3, double c4, double c5, double c6, double c7, double c8 ) {
        super( position, 0 /* orientation */ );
        
        _outerDiameter = outerDiameter;
        _innerDiameter = innerDiameter;
        _innerOrientation = 0;
        _enabled = false;
        
        _dnaStrand = dnaStrand;
        _fluid = fluid;
        _maxDt = maxDt;
        
        _maxDNASpeed = maxDNASpeed;
        
        _c1 = c1;
        _c2 = c2;
        _c3 = c3;
        _c4 = c4;
        _c5 = c5;
        _c6 = c6;
        _c7 = c7;
        _c8 = c8;
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
     * Gets the speed with which the DNA strand is moving through the enzyme
     * for specific ATP and DNA force values.
     * 
     * @param atp ATP concentration
     * @param fDNA DNA force magnitude (pN)
     * @return speed (nm/s)
     */
    public double getDNAStrandSpeed( final double atp, final double fDNA ) {
        final double maxSpeed = _maxDNASpeed * ( _c1  / ( _c2 + ( _c3 * Math.exp( fDNA * _c4 ) ) ) );
        final double km = ( _c1 / _c5 ) * ( _c6 + ( _c7 * Math.exp( fDNA * _c8 ) ) ) / ( _c2 + ( _c3 * Math.exp( fDNA * _c4 ) ) );
        double speed = maxSpeed * atp / ( atp + km );
        return speed;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        if ( _enabled ) {
            final double dnaStrandSpeed = getDNAStrandSpeed();
//          System.out.println( "AbstractEnzyme.stepInTime dnaStrandSpeed=" + dnaStrandSpeed );//XXX
            final double speedScale = dnaStrandSpeed / _maxDNASpeed;
            final double dtScale = dt / _maxDt;
            final double deltaAngle = MAX_ROTATION_PER_CLOCK_STEP * speedScale * dtScale;
            _innerOrientation += deltaAngle;
            notifyObservers( PROPERTY_INNER_ORIENTATION );
        }
    }
}
