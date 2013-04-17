// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision.model;

import edu.colorado.phet.colorvision.view.PhotonBeamGraphic;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;


/**
 * Photon is the model of a single photon.
 *
 * @author Chris Malley, cmalley@pixelzoom.com
 * @version $Revision$
 */
public class Photon {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Location in 2D space
    private double _x, _y;
    // Direction in degrees
    private double _direction;
    // Color of the light source that emitted the photon.
    private VisibleColor _color;
    // Intensity of the light source when the photon was emitted, in percent (0-100)
    private double _intensity;

    /*
     * These members are not strictly part of the Photon model.
     * They are included here to improve performance of Photon
     * memory management in PhotonBeam and PhotonBeamGraphic.
     */
    private boolean _inUse; // False indicates the photon is available for reuse
    private boolean _isFiltered; // True if the photon has been previously filtered

    /*
     * These members are not strictly part of the Photon model.
     * They are related to how a Photon is displayed, and are included 
     * here so that we can optimize by pre-computing their values.  
     * See the setDirection method.
     */
    private double _deltaX, _deltaY; // change in location per clock tick
    private double _width, _height; // dimensions of the Photon's bounding box

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param color the color
     * @param intensity the intensity
     * @param x the X location
     * @param y the Y location
     * @param direction the direction
     */
    public Photon( VisibleColor color, double intensity, double x, double y, double direction ) {
        _inUse = true;
        _color = color;
        _intensity = intensity;
        _x = x;
        _y = y;
        _isFiltered = false;
        setDirection( direction ); // pre-calculates deltas  
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the "in use" state.
     * 
     * @param inUse true or false
     */
    public void setInUse( boolean inUse ) {
        _inUse = inUse;
    }

    /**
     * Indicates whether the photon is in use.
     * Photons that are not in use are available for reuse.
     * 
     * @return true or false
     */
    public boolean isInUse() {
        return _inUse;
    }

    /**
     * Sets the filter status of the photon.
     * 
     * @param isFiltered true if the photon has been filtered, false otherwise
     */
    public void setFiltered( boolean isFiltered ) {
        _isFiltered = isFiltered;
    }

    /**
     * Has the photon been filtered?
     * 
     * @return true if the photon has been filtered, false otherwise
     */
    public boolean isFiltered() {
        return _isFiltered;
    }

    /**
     * Sets the location.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setLocation( double x, double y ) {
        _x = x;
        _y = y;
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
     * Sets the direction of the photon.
     * 
     * @param direction the direction (in degrees)
     */
    public void setDirection( double direction ) {
        _direction = direction;

        // Pre-compute rendering values that are based on direction.
        double radians = Math.toRadians( direction );
        double cosAngle = Math.cos( radians );
        double sinAngle = Math.sin( radians );
        _deltaX = PhotonBeam.PHOTON_DS * cosAngle;
        _deltaY = PhotonBeam.PHOTON_DS * sinAngle;
        _width = PhotonBeamGraphic.PHOTON_LINE_LENGTH * cosAngle;
        _height = PhotonBeamGraphic.PHOTON_LINE_LENGTH * sinAngle;
    }

    /**
     * Gets the direction of the photon.
     * 
     * @return the direction (in degrees)
     */
    public double getDirection() {
        return _direction;
    }

    /**
     * Sets the color.
     * 
     * @param color the color
     */
    public void setColor( VisibleColor color ) {
        _color = color;
    }

    /**
     * Gets the color.
     * 
     * @return the color
     */
    public VisibleColor getColor() {
        return _color;
    }

    /**
     * Sets the intensity. of the light source at the time the 
     * photon was emitted.  This value is a percentage.
     * <p>
     * Values range from 0.0 (no intensity) to 100.0 (full intensity) inclusive.
     * 
     * @param intensity the intensity
     * @throws IllegalArgumentException if intensity is out of range
     */
    public void setIntensity( double intensity ) {
        if( intensity < 0 || intensity > 100 ) {
            throw new IllegalArgumentException( "intensity out of range: " + intensity );
        }
        _intensity = intensity;
    }

    /**
     * Gets the intensity of the light source at the time the 
     * photon was emitted.
     * 
     * @return the intensity
     */
    public double getIntensity() {
        return _intensity;
    }

    /**
     * Gets the width of the photon's bounding rectangle.
     * 
     * @return the width
     */
    public double getWidth() {
        return _width;
    }

    /**
     * Gets the height of the photon's bounding rectangle.
     * 
     * @return the height
     */
    public double getHeight() {
        return _height;
    }

    /**
     * Advances the location of the photon.
     * 
     * @param dt time delta, currently ignored
     */
    public void stepInTime( double dt ) {
        // Advance the location of the photon.
        _x += _deltaX;
        _y += _deltaY;
    }

}