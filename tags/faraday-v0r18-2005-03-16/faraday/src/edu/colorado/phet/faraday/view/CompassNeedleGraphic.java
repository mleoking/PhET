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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


/**
 * CompassNeedleGraphic is the graphical representation of a compass needle.
 * A needle has a "north tip" and a "south tip".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassNeedleGraphic extends CompositePhetGraphic {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _direction; // in radians
    private Dimension _size;
    private double _strength;  // 0.0 - 1.0
    private PhetShapeGraphic _northGraphic, _southGraphic;
    
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
        assert( component != null );
        
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        _size = new Dimension( 40, 20 );
        _direction = 0.0;
        _strength = 1.0;
        _northGraphic = new PhetShapeGraphic( component );
        _southGraphic = new PhetShapeGraphic( component );
        
        addGraphic( _northGraphic );
        addGraphic( _southGraphic );
        
        updateGraphics();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the direction that the north pole of the needle points.  
     * Zero degrees points down the positive X axis.
     * 
     * @param direction the direction, in radians
     */
    public void setDirection( double direction ) {
        if ( _direction != direction ) {
            _direction = direction;
            clearTransform();
            rotate( direction );
            repaint();
        }
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
        assert ( size != null );
        _size.setSize( size );
        updateGraphics();
    }
    
    /** 
     * Sets the size of the needle.
     * Width is measured from the tip-to-tip (north-to-south ).
     * 
     * @param width the width
     * @param height the height
     */
    public void setSize( int width, int height ) {
        setSize( new Dimension( width, height ) );
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
     * This is a value between 0-1. The strength value is 
     * a multiplier used to set the alpha channel of the rendered needle.
     * 0 is fully transparent, 1 is fully opaque, values in between are partially transparent.
     * 
     * @param strength the strength
     * @throws IllegalArgumentException if strength is out of range
     */
    public void setStrength( double strength ) {
        if ( ! ( strength >= 0 && strength <= 1 ) ) {
            throw new IllegalArgumentException( "strength must be 0.0-1.0 : " + strength );
        }
        if ( strength != _strength ) {
            _strength = strength;
            updateGraphics();
        }
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
     * Updates the needle graphics to match the settings provided.
     * <p>
     * Prior to applying transforms, the north tip of the needle points
     * down the X-axis, and the south tip points down the Y-axis.
     */
    private void updateGraphics() {

        // Shapes
        {
            GeneralPath northPath = new GeneralPath();
            northPath.moveTo( 0, -( _size.height / 2 ) );
            northPath.lineTo( ( _size.width / 2 ), 0 );
            northPath.lineTo( 0, ( _size.height / 2 ) );
            northPath.closePath();

            GeneralPath southPath = new GeneralPath();
            southPath.moveTo( 0, -( _size.height / 2 ) );
            southPath.lineTo( 0, ( _size.height / 2 ) );
            southPath.lineTo( -( _size.width / 2 ), 0 );
            southPath.closePath();

            _northGraphic.setShape( northPath );
            _southGraphic.setShape( southPath );
        }

        // Colors
        {
            // Control the alpha of the color to make the needle look "dimmer".
            int alpha = (int) ( 255 * _strength );
            Color northColor = new Color( 255, 0, 0, alpha ); // red
            Color southColor = new Color( 255, 255, 255, alpha ); // white

            _northGraphic.setPaint( northColor );
            _southGraphic.setPaint( southColor );
        }
        
        repaint();
    }
}
