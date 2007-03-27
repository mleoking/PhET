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
    private final double _zrScale;  // scaling factor for zr term, constrains the width of the beam shape
    
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
        
        _zrScale = getBeamRadiusAt( _distanceFromObjectiveToWaist, _diameterAtWaist / 2, _wavelength, 1 ) / ( _diameterAtObjective / 2 );
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
    // Beam shape
    //----------------------------------------------------------------------------
    
    /*
     * Gets the beam radius at a specified distance from the center of the waist.
     * The center of the waist is at (0,0).  If we draw a line through the center
     * of the waist, in the direction that the beam is pointing, then z is a 
     * distance on this line.
     * 
     * @param z vertical distance from the center of the waist (nm)
     */
    public double getBeamRadiusAt( double z ) {
        return getBeamRadiusAt( z, _diameterAtWaist/2, _wavelength, _zrScale );
    }
    
    /*
     * Gets the beam radius at a specified distance from the waist.
     * Calculation assumes that (0,0) is at the center of the waist.
     * 
     * @param z distance from waist
     * @param r0 radius at waist
     * @param wavelength
     * @param zrScale
     */
    private static double getBeamRadiusAt( double z, double r0, double wavelength, double zrScale ) {
        double zAbs = Math.abs( z );
        double zr = zrScale * Math.PI * r0 * r0 / wavelength;
        double rz = r0 * Math.sqrt(  1 + ( ( zAbs / zr ) * ( zAbs / zr )  ) );
        return rz;
    }
    
    /**
     * Is a specified point inside the out-going laser beam's shape?
     * 
     * @param p
     * @return
     */
    public boolean contains( Point2D p ) {
        assert( getOrientation() == Math.toRadians( -90 ) ); // laser beam must point up
        boolean b = false;
        // Is p on the out-going side of the objective?
        if ( p.getY() <= getY() + _distanceFromObjectiveToWaist ) {
            double radius = getBeamRadiusAt( p.getY() - getY() );
            if ( radius <= Math.abs( getX() - p.getX() ) ) {
                b= true;
            }
        }
        return b;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
