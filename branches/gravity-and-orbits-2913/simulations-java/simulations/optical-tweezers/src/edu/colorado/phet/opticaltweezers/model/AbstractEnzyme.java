// Copyright 2002-2011, University of Colorado

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
 * <p>
 * Relationships:
 * <ul>
 * <li>gets the ATP concentration from fluid to compute DNA speed
 * <li>gets the DNA force from the stand attached to the visible bead
 * <li>varies the contour length of the 2 DNA strands to pull in the bead
 * </ul>
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrandBead; // DNA strand that is attached to bead
    private DNAStrand _dnaStrandFree; // DNA strand that is free, not attached to bead
    private Fluid _fluid;
    private final double _maxDt;
    
    private IDNASpeedStrategy _dnaSpeedStrategy;
    private IStallForceStrategy _stallForceStrategy;
    
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
            IDNASpeedStrategy dnaSpeedStrategy,
            IStallForceStrategy stallForceStrategy ) {
        super( position, 0 /* orientation */ );
        
        _outerDiameter = outerDiameter;
        _innerDiameter = innerDiameter;
        _innerOrientation = 0;
        _enabled = false;
        
        _dnaStrandBead = dnaStrandBead;
        _dnaStrandFree = dnaStrandFree;
        _fluid = fluid;
        _maxDt = maxDt;
        
        _dnaSpeedStrategy = dnaSpeedStrategy;
        _stallForceStrategy = stallForceStrategy;
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
    
    /**
     * Gets the speed with which the DNA is being pulled in by the enzyme.
     * 
     * @param atp
     * @param dnaForceMagnitude
     * @return speed (nm/sec)
     */
    public double getDNASpeed( double atp, double dnaForceMagnitude ) {
        return _dnaSpeedStrategy.getSpeed( atp, dnaForceMagnitude );
    }
    
    /**
     * Gets the stall force magnitude.
     * This is the force when the DNA strand has been fully "pulled in" by the enzyme.
     * 
     * @param atpConcentration
     * @return
     */
    public double getStallForceMagnitude( double atpConcentration ) {
        return _stallForceStrategy.getStallForceMagnitude( atpConcentration );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the model each time the simulation clock ticks.
     * 
     * @param dt clock step, seconds
     */
    public void stepInTime( double dt ) {
        if ( _enabled ) {

            final double atp = _fluid.getATPConcentration();
            final double dnaForce = _dnaStrandBead.getForceAtBead().getMagnitude();
            final double dnaSpeed = getDNASpeed( atp, dnaForce ); // nm/sec
            
            // Shorten the DNA strand attached to the bead
            final double beadContourLengthDelta = dnaSpeed * dt; // ns/sec * sec = nm
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
                final double speedScale = dnaSpeed / _dnaSpeedStrategy.getMaxSpeed();
                final double dtScale = dt / _maxDt;
                final double deltaAngle = MAX_ROTATION_DELTA * speedScale * dtScale;
                _innerOrientation += deltaAngle;
                notifyObservers( PROPERTY_INNER_ORIENTATION );
            }
        }
    }
}
