/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;


/**
 * CompassNeedle is the description of a compass needle.
 * It contains the information needed to draw a compass needle.
 * It is used by both the compass and the compass "grid".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class CompassNeedle {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Dimension _size;
    private Point2D _location;
    private double _direction;
    private double _strength;
    private boolean _alphaEnabled;
    private Shape _northShape, _southShape;
    private Color _northColor, _southColor;
    
    private AffineTransform _transform; // reusable transform

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole contructor.
     */
    public CompassNeedle() {
        _size = new Dimension( 40, 20 );
        _location = new Point2D.Double( 0, 0 );
        _direction = 0.0;
        _strength = 0.0;
        _alphaEnabled = false;
        _transform = new AffineTransform();
        updateShapes();
        updateColors();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the location.
     * 
     * @param p the location
     */
    public void setLocation( Point2D p ) {
        assert( p != null );
        setLocation( p.getX(), p.getY() );
    }

    /**
     * Convenience method for setting the location.
     * 
     * @param x
     * @param y
     */
    public void setLocation( double x, double y ) {
        _location.setLocation( x, y );
        updateShapes();
    }
    
    /**
     * Gets the location.
     * 
     * @return the location
     */
    public Point2D getLocation() {
        return _location;
    }
    
    /**
     * Gets the location X coordinate.
     * 
     * @return X
     */
    public double getX() {
        return _location.getX();
    }
    
    /**
     * Gets the location Y coordinate.
     * 
     * @return Y
     */
    public double getY() {
        return _location.getY();
    }
    
    /**
     * Sets the size of the needle.
     * 
     * @param size
     */
    public void setSize( Dimension size ) {
        assert( size != null );
        _size.setSize( size );
        updateShapes();
    }

    /**
     * Gets the size of the needle.
     * 
     * @return
     */
    public Dimension getSize() {
        return _size;
    }

    /**
     * Sets the strength, a value from 0-1.
     * 
     * @param strength
     */
    public void setStrength( double strength ) {
        assert( strength >= 0 && strength <= 1 );
        _strength = strength;
        updateColors();
    }

    /** 
     * Gets the strength
     * 
     * @return
     */
    public double getStrength() {
        return _strength;
    }

    /**
     * Sets the direction, in radians.
     * 
     * @param direction
     */
    public void setDirection( double direction ) {
        _direction = direction;
        updateShapes();
    }

    /**
     * Gets the direction, in radians.
     * 
     * @return the direction
     */
    public double getDirections() {
        return _direction;
    }
    
    /**
     * Controls how strength is represented.
     * If alpha is enabled, then the Color's alpha component is adjusted.
     * If alpha is disabled, then the Color's saturation is adjusted.
     * 
     * @param enabled true or false
     */
    public void setAlphaEnabled( boolean enabled ) {
       _alphaEnabled = enabled;
       updateColors();
    }
    
    /**
     * Is alpha enabled?
     * 
     * @return true or false
     */
    public boolean isAlphaEnabled() {
        return _alphaEnabled;
    }
    
    /**
     * Gets the color used to fill the needle's north tip.
     * 
     * @return the color
     */
    public Color getNorthColor() {
        return _northColor;
    }
    
    /**
     * Gets the color used to fill the needle's south tip.
     * 
     * @return the color
     */
    public Color getSouthColor() {
        return _southColor;
    }
    
    /**
     * Gets the shape that describes the needle's north tip.
     * 
     * @return the shape
     */
    public Shape getNorthShape() {
        return _northShape;
    }
    
    /**
     * Gets the shape that describes the needle's south tip.
     * 
     * @return the shape
     */
    public Shape getSouthShape() {
        return _southShape;
    }
    
    //----------------------------------------------------------------------------
    // Shapes & Colors
    //----------------------------------------------------------------------------
    
    /**
     * Updates the needle's Shapes.
     */
    private void updateShapes() {
        
        _transform.setToIdentity();
        _transform.translate( _location.getX(), _location.getY() );
        _transform.rotate( _direction );

        // North tip of needle
        GeneralPath northPath = new GeneralPath();
        northPath.moveTo( 0, -( _size.height / 2 ) );
        northPath.lineTo( ( _size.width / 2 ), 0 );
        northPath.lineTo( 0, ( _size.height / 2 ) );
        northPath.closePath();
        _northShape = _transform.createTransformedShape( northPath );

        // South tip of needle
        GeneralPath southPath = new GeneralPath();
        southPath.moveTo( 0, -( _size.height / 2 ) );
        southPath.lineTo( 0, ( _size.height / 2 ) );
        southPath.lineTo( -( _size.width / 2 ), 0 );
        southPath.closePath();
        _southShape = _transform.createTransformedShape( southPath );
    }

    /**
     * Updates the needle's Colors.
     */
    private void updateColors() {
        if ( _alphaEnabled ) {
            // Alpha works on any background, but you pay a peformance price.
            int alpha = (int) ( 255 * _strength );
            _northColor = new Color( 255, 0, 0, alpha );
            _southColor = new Color( 255, 255, 255, alpha );
        }
        else {
            // Color saturation assumes a black background.
            int saturation = (int) ( 255 * _strength );
            _northColor = new Color( saturation, 0, 0 );
            _southColor = new Color( saturation, saturation, saturation );
        }
    }
}
