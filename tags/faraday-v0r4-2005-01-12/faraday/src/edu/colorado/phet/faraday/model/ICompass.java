/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * ICompass is the interface that must be implemented by all compass model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface ICompass {

    /**
     * Sets the location.
     * 
     * @param p the location
     */
    public abstract void setLocation( Point2D p );

    /**
     * Sets the location.
     * 
     * @param x the location's X coordinate
     * @param y the location's Y coordinate
     */
    public abstract void setLocation( double x, double y );

    /**
     * Gets the location.
     * 
     * @return the location
     */
    public abstract Point2D getLocation();

    /**
     * Gets the location's X coordinate.
     * 
     * @return the location's X coordinate
     */
    public abstract double getX();

    /**
     * Gets the location's Y coordinate.
     * 
     * @return the location's Y coordinate
     */
    public abstract double getY();

    /**
     * Gets the direction that the compass needle points, in degrees.
     * Zero degrees points down the positve X axis.
     * Positve values indicate clockwise rotation.
     * 
     * @return the direction, in degrees
     */
    public abstract double getDirection();

    /**
     * Gets the strength of the magnetic field at the compass location.
     * 
     * @return the field strength vector
     */
    public abstract AbstractVector2D getFieldStrength();
}