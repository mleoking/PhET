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

import java.awt.Dimension;
import java.awt.geom.Point2D;


/**
 * IMagnet is the interface implemented by all magnets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IMagnet {
    
    /** 
     * Sets the magnet's strength.
     * 
     * @param strength the strength
     */
    public abstract void setStrength( double strength );

    /**
     * Gets the magnet's strength.
     * 
     * @return the strength
     */
    public abstract double getStrength();

    /**
     * Gets the strength of the magnetic field at a point in 2D space.
     * 
     * @param p the point
     * @return the strength
     */
    public abstract double getStrength( final Point2D p );

    /**
     * Sets the location of the magnet in 2D space.
     * 
     * @param location the location
     */
    public abstract void setLocation( final Point2D location );

    /**
     * Sets the location of the magnet in 2D space.
     * 
     * @param x location X coordinate
     * @param y location Y coordinate
     */
    public abstract void setLocation( double x, double y );

    /**
     * Gets the location of the magnet in 2D space.
     * 
     * @return the location
     */
    public abstract Point2D getLocation();

    /**
     * Gets the location's X coordinate.
     * 
     * @return X coordinate
     */
    public abstract double getX();

    /**
     * Gets the location's Y coordinate.
     * 
     * @return Y coordinate
     */
    public abstract double getY();

    /**
     * Sets the magnet's direction in degrees.
     * Zero degrees is pointing down the positive X axis.
     * Positive angles indicate in clockwise rotation.
     * 
     * @param direction the direction, in degrees
     */
    public abstract void setDirection( double direction );

    /**
     * Gets the magnet's direction.
     * 
     * @return the direction
     * @see edu.colorado.phet.faraday.model.IMagnet#setDirection(java.awt.geom.Point2D)
     */
    public abstract double getDirection();

    /**
     * Gets the direction of the magnet field at a point in 2D space.
     * Zero degrees is pointing down the positive X axis.
     * Positive angles indicate in clockwise rotation.
     * 
     * @param p the point
     * @return the direction
     */
    public abstract double getDirection( final Point2D p );

    /**
     * Sets the physical size of the magnet.
     * 
     * @param size the size
     */
    public abstract void setSize( Dimension size );

    /**
     * Sets the physical size of the magnet.
     * 
     * @param width the width
     * @param height the height
     */
    public abstract void setSize( double width, double height );

    /** 
     * Gets the physical size of the magnet.
     * 
     * @return the size
     */
    public abstract Dimension getSize();

    /**
     * Gets the physical width of the magnet.
     * 
     * @return the width
     */
    public abstract double getWidth();

    /**
     * Gets the physical height of the magnet.
     * 
     * @return the height
     */
    public abstract double getHeight();
}
