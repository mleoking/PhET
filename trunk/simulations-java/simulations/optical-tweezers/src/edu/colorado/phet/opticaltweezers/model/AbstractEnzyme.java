/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import com.sun.tools.javac.v8.tree.Tree.NewClass;

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
    // Debug
    //----------------------------------------------------------------------------
    
    private static final boolean ENABLE_DEBUG_OUTPUT = false;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_INNER_ORIENTATION = "innerOrientation";
    
    private static final double MAX_ROTATION_DELTA = Math.toRadians( 45 );
    
    private static final double MAX_CONTOUR_LENGTH_DELTA = 30;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrandBead; // DNA strand that is attached to bead
    private DNAStrand _dnaStrandFree; // DNA strand that is free, not attached to bead
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
            DNAStrand dnaStrandBead, 
            DNAStrand dnaStrandFree,
            Fluid fluid,
            double maxDt,
            double maxDNASpeed,
            double[] calibrationConstants ) {
        super( position, 0 /* orientation */ );
        
        _outerDiameter = outerDiameter;
        _innerDiameter = innerDiameter;
        _innerOrientation = 0;
        _enabled = false;
        
        _dnaStrandBead = dnaStrandBead;
        _dnaStrandFree = dnaStrandFree;
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
    
    /*
     * Gets the speed at which the DNA strand is moving through the enzyme.
     * 
     * @return speed (nm/s)
     */
    private double getDNASpeed() {
        final double atp = _fluid.getATPConcentration();
        final double fDNA = _dnaStrandBead.getForceAtBead().getMagnitude();
        return getDNASpeed( atp, fDNA );
    }
    
    /*
     * Gets the speed at which the DNA strand is moving through the enzyme
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
    private double getDNASpeed( final double atp, final double fDNA ) {
        final double maxSpeed = _maxDNASpeed * ( _c[0]  / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) ) );
        final double km = ( _c[0] / _c[4] ) * ( _c[5] + ( _c[6] * Math.exp( fDNA * _c[7] ) ) ) / ( _c[1] + ( _c[2] * Math.exp( fDNA * _c[3] ) ) );
        final double speed = maxSpeed * atp / ( atp + km );
        return speed;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the model each time the simulation clock ticks.
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        if ( _enabled ) {

            final double dnaSpeed = getDNASpeed();
            final double speedScale = dnaSpeed / _maxDNASpeed;
            final double dtScale = dt / _maxDt;
            
            // Shorten the DNA strand attached to the bead
            final double beadContourLengthDelta = MAX_CONTOUR_LENGTH_DELTA * speedScale * dtScale;
            final double oldBeadContourLength = _dnaStrandBead.getContourLength();
            final double newBeadContourLength = oldBeadContourLength - beadContourLengthDelta;
            final double actualBeadContourLength = _dnaStrandBead.setContourLength( newBeadContourLength );
            
            // Lengthen the DNA strand attached to the free end
            final double actualContourDelta = oldBeadContourLength - actualBeadContourLength;
            if ( actualContourDelta > 0 ) {
                _dnaStrandFree.setContourLength( _dnaStrandFree.getContourLength() + actualContourDelta );
            }
            
            if ( ENABLE_DEBUG_OUTPUT ) {
                System.out.println( "speed=" + dnaSpeed + " deltaContour=" + beadContourLengthDelta + 
                    " beadContour=" + _dnaStrandBead.getContourLength() + " beadPivots=" + _dnaStrandBead.getPivots().size() +
                    " freeContour=" + _dnaStrandFree.getContourLength() + " freePivots=" + _dnaStrandFree.getPivots().size() +
                    " totalContour=" + ( _dnaStrandBead.getContourLength() + _dnaStrandFree.getContourLength() ) );
            }
            
            // If the strand's contour length was changed, rotate the enzyme's inner sphere.
            if ( actualContourDelta > 0 ) {
                final double deltaAngle = MAX_ROTATION_DELTA * speedScale * dtScale;
                _innerOrientation += deltaAngle;
                notifyObservers( PROPERTY_INNER_ORIENTATION );
            }
        }
    }
}
