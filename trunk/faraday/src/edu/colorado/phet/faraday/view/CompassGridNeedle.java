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
 * CompassGridNeedle draws a compass needle, to be used only by CompassGridGraphic.
 * It is not an inner class because of its size, and its visibility is package private.
 * <p>
 * CompassGridNeedle is not a descendant of PhetGraphic, so that we can avoid the
 * overhead of computing AffineTransforms. (This overhead is built into PhetGraphic,
 * specifically in PhetGraphic.getNetTransform.)
 * <p>
 * This class is "streamlined" and avoids unnecessary checks and updates.
 * It assumes that CompassGridGraphic will handle saving/restoring the
 * graphics context.  And it assumes that the grid will be positioned at the
 * origin of its parent component.  These assumptions allow us to bypass most
 * of the expensive transforms in PhetGraphic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class CompassGridNeedle {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point2D _location;
    private Dimension _size;
    private double _strength;
    private double _direction;
    private Shape _northShape, _southShape;
    private Color _northColor, _southColor;
    private boolean _alphaEnabled;
    private AffineTransform _transform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole contructor.
     */
    public CompassGridNeedle() {
        _location = new Point2D.Double( 0, 0 );
        _size = new Dimension( 40, 20 );
        _direction = 0.0;
        _alphaEnabled = false;
        _transform = new AffineTransform();
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
     * Sets the size of the needle.
     * 
     * @param size
     */
    public void setSize( Dimension size ) {
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
    
    //----------------------------------------------------------------------------
    // Shapes & Colors
    //----------------------------------------------------------------------------
    
    /**
     * Updates the Shapes used to draw the needle.
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
     * Updates the Colors used to draw the needle.
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
    
    //----------------------------------------------------------------------------
    // Drawing
    //----------------------------------------------------------------------------
    
    /**
     * Draws the needle.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        g2.setPaint( _northColor );
        g2.fill( _northShape );
        g2.setPaint( _southColor );
        g2.fill( _southShape );
    }
}
