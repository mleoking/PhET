/* Copyright 2004, University of Colorado */

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

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;


/**
 * CompassNeedleGraphic is the graphical representation of a compass needle.
 * A needle has a "north tip" and a "south tip".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassNeedleGraphic extends PhetGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Point _location;
    private double _direction;
    private Dimension _size;
    private Shape _northTip, _southTip;
    private Color _northColor, _southColor;
    private double _strength;  // 0.0 - 1.0
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public CompassNeedleGraphic( Component component ) {
        
        super( component );
        
        _location = new Point( 0, 0 );
        _direction = 0.0;
        _size = new Dimension( 40, 20 );
        _northColor = Color.RED;
        _southColor = Color.BLUE;
        _strength = 1.0;
        
        updateShape();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the location.
     * 
     * @param p the location point
     */
    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }
    
    /**
     * Sets the location.
     * 
     * @param x location X-axis coordinate
     * @param y location Y-axis coordinate
     */
    public void setLocation( int x, int y ) {
        if ( _location == null ) {
            _location = new Point(x,y);
        }
        else {
            _location.setLocation( x, y );
        }
        repaint();
    }
    
    /**
     * Gets the location.
     * 
     * @return the location
     */
    public Point getLocation() {
        return new Point( _location );
    }
    
    /**
     * Gets the X coordinate of the location
     * 
     * @return X coordinate
     */
    public int getX() { 
        return _location.x;
    }
    
    /**
     * Gets the Y coordinate of the location.
     * 
     * @return Y coordinate
     */
    public int getY() {
        return _location.y;
    }
    
    /**
     * Sets the direction that the north pole of the needle points.  
     * Zero degrees points down the positive X axis.
     * 
     * @param direction the direction, in degrees
     */
    public void setDirection( double direction ) {
        _direction = direction;
        repaint();
    }
    
    /**
     * Gets the direction that the north pole of the needle points.
     * Zero degrees points down the positive X axis.
     * 
     * @return the direction, in degrees
     */
    public double getDirection() {
        return _direction;
    }

    /** 
     * Sets the size of the needle.
     * Width is measured from the tip-to-tip (north-to-south ).
     * 
     * @param size the size
     */
    public void setSize( Dimension size ) {
        _size = new Dimension( size );
        updateShape();
        repaint();
    }
    
    /** 
     * Gets the size of the needle.
     * Width is measured from the tip-to-tip (north-to-south ).
     * 
     * @return the size
     */
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    /**
     * Sets the relative strength that is to be displayed by the needle.
     * This is a value between 0-1, and is the multiplier used to set
     * the alpha channel of the rendered needle.  0 is fully transparent,
     * 1 is fully opaque, values in between are partially transparent.
     * 
     * @param strength the strength
     */
    public void setStrength( double strength ) {
        if ( strength < 0 || strength > 1 ) {
            throw new IllegalArgumentException( "strength must be 0.0-1.0 : " + strength );
        }
        _strength = strength;
        _northColor = new Color( 255, 0, 0, (int) ( 255 * _strength ) );
        _southColor = new Color( 0, 0, 255, (int) ( 255 * _strength ) );
        repaint();
    }
    
    /**
     * Gets the strength.
     * 
     * @see setStrength
     * @return the strength
     */
    public double getStrength() {
        return _strength;
    }
    
    //----------------------------------------------------------------------------
    // Shapes
    //----------------------------------------------------------------------------

    /*
     * Updates the shape of the needle to match the settings provided.
     * <p>
     * Prior to applying transforms, the north tip of the needle points
     * down the X-axis, and the south tip points down the Y-axis.
     */
    private void updateShape() {
        
        GeneralPath northPath = new GeneralPath();
        northPath.moveTo( 0, -(_size.height/2) );
        northPath.lineTo( (_size.width/2), 0 );
        northPath.lineTo( 0, (_size.height/2) );
        northPath.closePath();
        _northTip = northPath;
        
        GeneralPath southPath = new GeneralPath();
        southPath.moveTo( 0, -(_size.height/2) );
        southPath.lineTo( 0, (_size.height/2) );
        southPath.lineTo( -(_size.width/2), 0 );
        southPath.closePath();
        _southTip = southPath;
    }
    
    //----------------------------------------------------------------------------
    // PhetGraphic implementation
    //----------------------------------------------------------------------------

    /*
     * Determines the bounds of the needle, after transforms are applied.
     * 
     * @return the bounds
     */
    protected Rectangle determineBounds() {
        // XXX needs to incorporate getNetTransform !
        AffineTransform transform = new AffineTransform();
        transform.translate( _location.x, _location.y );
        transform.rotate( _direction );
        Rectangle r = new Rectangle( _northTip.getBounds() );
        r.add( _southTip.getBounds() );
        return transform.createTransformedShape( r ).getBounds();
    }

    /*
     * Draws the needle.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        // XXX needs to incorporate getNetTransform !
        if ( isVisible() ) {
            super.saveGraphicsState( g2 );
            {
                // Request antialiasing
                RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHints( hints );
                
                // Transform
                g2.translate( _location.x, _location.y );
                g2.rotate( Math.toRadians( _direction ) );
                
                // Draw the "north" tip.
                g2.setPaint( _northColor );
                g2.fill( _northTip );
                
                // Draw the "south" tip.
                g2.setPaint( _southColor );
                g2.fill( _southTip );
            }
            super.restoreGraphicsState();
        }
    }
}
