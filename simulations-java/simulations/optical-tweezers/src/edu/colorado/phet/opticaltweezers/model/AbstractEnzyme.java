/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * AbstractEnzyme is the base class for all enzymes.
 * An enzyme feeds on ATP in the fluid and pulls on the DNA strand,
 * converting chemical energy into work.
 * <p>
 * The model herein is based on:
 * "Force production by single kinesin motors", Schnitzer et. al.,
 * Nature Cell Biology, Vol 2, October 2000, p. 718-723.
 * <p>
 * Also see the simulation design document (optical-tweezers.pdf) and
 * Excel file (velocityVsForce_Calibration.xls) in the doc directory.
 * Note that these docs erroneously refer to DNA speed as velocity.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractEnzyme extends FixedObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_INNER_ORIENTATION = "innerOrientation";
    
    public static final double MAX_ROTATION_PER_CLOCK_STEP = Math.toRadians( 45 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrand;
    private Fluid _fluid;
    private final double _maxDt;
    
    // speed when DNA force=0 and ATP concentration=infinite (d2 in design doc)
    private final double _maxDNASpeed;
    // calibration constants for DNA speed model (c1 thru c8 in design doc)
    private final double[] _c;
    
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
            double[] calibrationConstants ) {
        super( position, 0 /* orientation */ );
        
        _outerDiameter = outerDiameter;
        _innerDiameter = innerDiameter;
        _innerOrientation = 0;
        _enabled = false;
        
        _dnaStrand = dnaStrand;
        _fluid = fluid;
        _maxDt = maxDt;
        
        _maxDNASpeed = maxDNASpeed;
        
        _c = calibrationConstants;
        assert( _c.length == 8 ); // we should have 8 calibration constants
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
     * 
     * @return speed (nm/s)
     */
    public double getDNASpeed() {
        final double atp = _fluid.getATPConcentration();
        final double fDNA = _dnaStrand.getHeadForce().getMagnitude();
        return getDNASpeed( atp, fDNA );
    }
    
    /*
     * Gets the speed with which the DNA strand is moving through the enzyme
     * for specific ATP and DNA force values.
     * <p>
     * This is erroneously refered to as velocity in the design document.
     * It is a function of the DNA force magnitude, so it has no orientation
     * and should be referred to as speed (the magnitude component of velocity).
     * 
     * @param atp ATP concentration
     * @param fDNA DNA force magnitude (pN)
     * @return speed (nm/s)
     */
    public double getDNASpeed( final double atp, final double fDNA ) {
        final double maxSpeed = _maxDNASpeed * ( _c[0]  / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) ) );
        final double km = ( _c[0] / _c[4] ) * ( _c[5] + ( _c[6] * Math.exp( fDNA * _c[7] ) ) ) / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) );
        final double speed = maxSpeed * atp / ( atp + km );
        return speed;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        if ( _enabled ) {
            final double dnaSpeed = getDNASpeed();
//          System.out.println( "AbstractEnzyme.stepInTime dnaSpeed=" + dnaSpeed );//XXX
            final double speedScale = dnaSpeed / _maxDNASpeed;
            final double dtScale = dt / _maxDt;
            final double deltaAngle = MAX_ROTATION_PER_CLOCK_STEP * speedScale * dtScale;
            _innerOrientation += deltaAngle;
            notifyObservers( PROPERTY_INNER_ORIENTATION );
        }
    }
}
