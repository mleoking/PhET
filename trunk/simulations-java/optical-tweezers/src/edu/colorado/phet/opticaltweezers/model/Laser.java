/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.DoubleRange;

/**
 * Laser is the model of the laser.
 * Position indicates where the center of the waist is located.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Laser extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POWER = "power";
    public static final String PROPERTY_RUNNING = "running";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _running;
    private final double _diameterAtObjective; // nm
    private final double _diameterAtWaist; // nm
    private final double _distanceFromObjectiveToWaist;
    private final double _distanceFromObjectiveToControlPanel; // nm
    private final double _wavelength; // nm
    private final double _visibleWavelength; // nm
    private double _power; // mW
    private final DoubleRange _powerRange; // mW
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position of the laser's objective (nm)
     * @param orientation orientation of the beam (radians)
     * @param diameterAtObjective diameter of the beam at the objective (nm)
     * @param diameterAtWaist diameter at the waist of the beam (nm)
     * @param distanceFromObjectiveToWaist distance from the objective to the waist (nm)
     * @param distanceFromObjectiveToControlPanel distance from objective to control panel (nm)
     * @param wavelength wavelength (nm)
     * @param visibleWavelength wavelength (nm) to use in views, since actual wavelength is likely IR
     * @param power power (mW)
     */
    public Laser( Point2D position, double orientation, 
            double diameterAtObjective, double diameterAtWaist, double distanceFromObjectiveToWaist, double distanceFromObjectiveToControlPanel,
            double wavelength, double visibleWavelength, DoubleRange powerRange ) {
        super( position, orientation, 0 /* speed */ );
        
        _running = false;
        _diameterAtObjective = diameterAtObjective;
        _diameterAtWaist = diameterAtWaist;
        _distanceFromObjectiveToWaist = distanceFromObjectiveToWaist;
        _distanceFromObjectiveToControlPanel = distanceFromObjectiveToControlPanel;
        _wavelength = wavelength;
        _visibleWavelength = visibleWavelength;
        _power = powerRange.getDefault();
        _powerRange = new DoubleRange( powerRange );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setRunning( boolean running ) {
        if ( running != _running ) {
            _running = running;
            notifyObservers( PROPERTY_RUNNING );
        }
    }
    
    public boolean isRunning() {
        return _running;
    }
    
    public double getDiameterAtObjective() {
        return _diameterAtObjective;
    }
    
    public double getDiameterAtWaist() {
        return _diameterAtWaist;
    }

    public double getDistanceFromObjectiveToWaist() {
        return _distanceFromObjectiveToWaist;
    }
    
    public double getDistanceFromObjectiveToControlPanel() {
        return _distanceFromObjectiveToControlPanel;
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    public double getVisibleWavelength() {
        return _visibleWavelength;
    }
    
    public double getPower() {
        return _power;
    }
    
    public void setPower( double power ) {
        if ( power < _powerRange.getMin() || power > _powerRange.getMax() ) {
            throw new IllegalArgumentException( "power out of range: " + power );
        }
        if ( power != _power ) {
            _power = power;
            notifyObservers( PROPERTY_POWER );
        }
    }
    
    public DoubleRange getPowerRange() {
        return _powerRange;
    }
    
    //----------------------------------------------------------------------------
    // Beam shape & gradient
    //----------------------------------------------------------------------------
    
    public double getBeamIntensityAt( double x, double radius ) {
        return getBeamIntensityAt( x, radius, _power );
    }
    
    public static double getBeamIntensityAt( double x, double radius, double power ) {
        double t1 = power / ( Math.PI * ( ( radius * radius ) / 2 ) );
        double t2 = Math.exp( ( -2 * x * x ) / ( radius * radius ) );
        return t1 * t2;
    }
    
    /**
     * Gets the beam radius at a specified distance from the center of the waist.
     * The center of the waist is at (0,0).  If we draw a line through the center
     * of the waist, in the direction that the beam is pointing, then z is a 
     * distance on this line.
     * 
     * @param y vertical distance from the center of the waist (nm)
     * @return radius at y, in nm
     */
    public double getBeamRadiusAt( double y ) {
        final double yAbs = Math.abs( y );
        final double r0 = _diameterAtWaist / 2;
        final double rMax = _diameterAtObjective / 2;
        final double zr = ( Math.PI * r0 * r0 ) / _wavelength;
        final double A = ( _distanceFromObjectiveToWaist / zr ) / Math.sqrt( ( ( rMax / r0 ) * ( rMax / r0 ) ) - 1 );
        final double t1 = yAbs / ( A * zr );
        double rz = r0 * Math.sqrt(  1 + ( t1 * t1 ) );
        return rz;
    }
    
    /**
     * Is a specified point inside the out-going laser beam's shape?
     * 
     * @param x
     * @param y
     * @return true or false
     */
    public boolean contains( double x, double y ) {
        assert( getOrientation() == Math.toRadians( -90 ) ); // laser beam must point up
        boolean b = false;
        // Is y on the out-going side of the objective?
        if ( y <= getY() + _distanceFromObjectiveToWaist ) {
            double radius = getBeamRadiusAt( y - getY() );
            if ( radius <= Math.abs( getX() - x ) ) {
                b = true;
            }
        }
        return b;
    }
    
    /**
     * Is a specified point inside the out-going laser beam's shape?
     * 
     * @param p
     * @return true or false
     */
    public boolean contains( Point2D p ) {
        return contains( p.getX(), p.getY() );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
