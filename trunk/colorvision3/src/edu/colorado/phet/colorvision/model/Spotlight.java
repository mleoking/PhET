/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision.model;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * Spotlight is the model for a 2D spotlight.
 * Any changes to the models properties (via its setters or getters)
 * results in the notification of all registered observers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Spotlight extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /** Minumum intensity, in percent */
    public static final double INTENSITY_MIN = 0.0;
    /** Maximum intensity, in percent */
    public static final double INTENSITY_MAX = 100.0;
    /** Minimum drop off rate (constant intensity) */
    public static final double DROP_OFF_RATE_MIN = 0.0;
    /** Maximum drop off rate (sharpest) */
    public static final double DROP_OFF_RATE_MAX = 100.0;
    /** Minimum cut off angle */
    public static final double CUT_OFF_ANGLE_MIN = 0.0;
    /** Maximum cut off angle */
    public static final double CUT_OFF_ANGLE_MAX = 180.00;
    /** Default cut off angle */
    public static final double CUT_OFF_ANGLE_DEFAULT = 15.0;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Light color
    private VisibleColor _color;
    // Cut off angle, the angle outside of which the light intensity is 0.0
    private double _cutOffAngle;
    // Direct that the light points, in degrees
    private double _direction;
    // Drop off rate, the rate at which light intensity drops off from the primary direction
    private double _dropOffRate;
    // Intensity, in percent
    private double _intensity;
    // Location in 2D space
    private double _x, _y;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * Creates a Spotlight with: location at the origin (0,0), a white beam,
     * 15 degree cut off angle, 0 degree direction, 0 drop off rate (contant),
     * 0 intensity.
     */
    public Spotlight() {
        // Initialize instance data.
        _color = VisibleColor.WHITE;
        _cutOffAngle = CUT_OFF_ANGLE_DEFAULT;
        _direction = 0.0;
        _dropOffRate = DROP_OFF_RATE_MIN; // constant intensity
        _intensity = INTENSITY_MAX;
        _x = _y = 0.0;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the color.
     * 
     * @return the color
     */
    public VisibleColor getColor() {
        return _color;
    }

    /**
     * Sets the color.
     * 
     * @param color the color
     */
    public void setColor( VisibleColor color ) {
        _color = color;
        notifyObservers();
    }

    /**
     * Gets the cut off angle.
     * The cut off angle is the angle outside of which the light intensity is 0.0.
     *  
     * @return the cut off angle (in degrees)
     */
    public double getCutOffAngle() {
        return _cutOffAngle;
    }

    /**
     * Sets the cut off angle.
     * Values outside the allowable range are silently clamped.
     * 
     * @param cutOffAngle the cut off angle (in degrees)
     */
    public void setCutOffAngle( double cutOffAngle ) {
        _cutOffAngle = MathUtil.clamp( CUT_OFF_ANGLE_MIN, cutOffAngle, CUT_OFF_ANGLE_MAX );
        notifyObservers();
    }

    /**
     * Gets the direction. 
     * The direction determines where the spotlight points.
     * 
     * @return the direction (in degrees)
     */
    public double getDirection() {
        return _direction;
    }

    /**
     * Sets the direction.
     * 
     * @param direction the direction (in degrees)
     */
    public void setDirection( double direction ) {
        _direction = direction;
        notifyObservers();
    }

    /**
     * Gets the drop off rate of the light.  The drop off rate is the rate
     * at which light intensity drops off from the primary direction.
     * 
     * @return the drop off rate
     */
    public double getDropOffRate() {
        return _dropOffRate;
    }

    /**
     * Sets the drop off rate.
     * Values range from 0.0 (constant intensity) to 1.0 (sharpest drop-off) inclusive.
     * Values outside the allowable range are silently clamped.
     * 
     * @param dropOffRate the drop off rate
     */
    public void setDropOffRate( double dropOffRate ) {
        _dropOffRate = MathUtil.clamp( DROP_OFF_RATE_MIN, dropOffRate, DROP_OFF_RATE_MAX );
        notifyObservers();
    }

    /**
     * Gets the intensity.
     * 
     * @return the intensity
     */
    public double getIntensity() {
        return _intensity;
    }

    /**
     * Sets the intensity.
     * Intensity is measured as a percentage.
     * Values range from 0.0 (no intensity) to 100.0 (full intensity) inclusive.
     * Values outside the allowable range are silently clamped.
     * 
     * @param intensity the intensity
     */
    public void setIntensity( double intensity ) {
        _intensity = MathUtil.clamp( INTENSITY_MIN, intensity, INTENSITY_MAX );
        notifyObservers();
    }

    /**
     * Gets the X coordinate of the location.
     * 
     * @return the X coordinate
     */
    public double getX() {
        return _x;
    }

    /**
     * Gets the Y coordinate of the location.
     * 
     * @return the Y coordinate
     */
    public double getY() {
        return _y;
    }

    /**
     * Sets the location.
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public void setLocation( double x, double y ) {
        _x = x;
        _y = y;
        notifyObservers();
    }

}