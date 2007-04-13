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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;


/**
 * CompassNeedleCache is a cache of the graphics components needed to draw a needle.
 * It contains Shapes for various "directions", and Colors for various "strengths".
 * The cache is populated as Shapes and Colors are requested, or you can call
 * populate to populate the entire cache immediately.
 * <p>
 * Changing the needle size clears the Shape cache.
 * <p>
 * Changing the alpha setting clears the Color cache.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class CompassNeedleCache {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static int NUMBER_OF_SHAPES = 360; // one for each degree of rotation (0-359)
    private static int NUMBER_OF_COLORS = 256; // one for each possible color component value (0-255)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The size of all needles in the cache.
    private Dimension _needleSize;
    
    // Determines whether strength is displayed using alpha or color saturation.
    private boolean _alphaEnabled;
    
    // Cache of Shapes, indexed by rotation angle in degrees (0-359)
    private Shape[] _shapeTable;
    
    // Caches of Colors, indexed by color component value (0-255)
    private Color[] _northColorTable, _southColorTable;
    
    private int _shapeCount,_northColorCount,_southColorCount; // for debugging 
    private GeneralPath _somePath; // a reusable path
    private AffineTransform _someTransform; // a reusable transform
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a cache with 20x20 needles, alpha enabled.
     */
    public CompassNeedleCache() {
        this( new Dimension( 20, 20 ), true );
    }
    
    /**
     * Creates a cache with the specified needle size and alpha setting.
     * 
     * @param needleSize
     * @param alphaEnabled
     */
    public CompassNeedleCache( Dimension needleSize, boolean alphaEnabled ) {
        _needleSize = new Dimension();
        _shapeTable = new Shape[NUMBER_OF_SHAPES];
        _northColorTable = new Color[NUMBER_OF_COLORS];
        _southColorTable = new Color[NUMBER_OF_COLORS];
        _shapeCount = _northColorCount = _southColorCount = 0;
        _somePath = new GeneralPath();
        _someTransform = new AffineTransform();
        setNeedleSize( needleSize );
        setAlphaEnabled( alphaEnabled );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the size of the needles.
     * Calling this method clears the Shape portion of the cache.
     * 
     * @param size
     */
    public void setNeedleSize( Dimension size ) {
        setNeedleSize( size.width, size.height );
    }
    
    /**
     * Sets the size of the needles.
     * <p>
     * Calling this method clears the Shape portion of the cache.
     * 
     * @param width
     * @param height
     */
    public void setNeedleSize( int width, int height ) {
        assert( width > 0 && height > 0 );
        if ( _needleSize.width != width && _needleSize.height != height ) {
            _needleSize.setSize( width, height );
            clearShapes();
        }
    }
    
    /**
     * Gets the size of the needles.
     * 
     * @return the size
     */
    public Dimension getNeedleSize() {
        return new Dimension( _needleSize );
    }
    
    /**
     * Enables or disables alpha.
     * When alpha is enabled, the alpha component is used to indicate strength.
     * When alpha is disabled, color component saturation is used to indicate strength.
     * <p>
     * Calling this method clears the Color portion of the cache.
     * 
     * @param alphaEnabled true or false
     */
    public void setAlphaEnabled( boolean alphaEnabled ) {
        assert( _northColorTable.length == _southColorTable.length );
        if ( alphaEnabled != _alphaEnabled ) {
            _alphaEnabled = alphaEnabled;
            clearColors();
        }
    }
    
    /**
     * Gets the current alpha state.
     * See setAlphaEnabled.
     * 
     * @return true or false
     */
    public boolean isAlphaEnabled() {
        return _alphaEnabled;
    }
    
    //----------------------------------------------------------------------------
    // Cache access
    //----------------------------------------------------------------------------
    
    /**
     * Fully populates the cache.
     * If you don't call this, then the cache is populated "as needed".
     */
    public void populate() {
        // Populate the Shape table.
        double direction;
        for ( int i = 0; i < NUMBER_OF_SHAPES; i++ ) {
            direction = Math.toRadians( i );
            getNorthShape( direction );
        }
        // Populate the Color tables.
        double strength;
        double deltaStrength = 1.0 / NUMBER_OF_COLORS;
        for ( int i = 0; i < NUMBER_OF_COLORS; i++ ) {
            strength = i * deltaStrength;
            getNorthColor( strength );
            getSouthColor( strength );
        }
    }
    
    /**
     * Clears the Shapes portion of the cache.
     */
    private void clearShapes() {
        for ( int i = 0; i < _shapeTable.length; i++ ) {
            _shapeTable[ i ] = null;
        }
        _shapeCount = 0;
    }
    
    /**
     * Clears the Colors portion of the cache.
     */
    private void clearColors() {
        for ( int i = 0; i < _northColorTable.length; i++ ) {
            _northColorTable[ i ] = null;
            _southColorTable[ i ] = null;
        }
        _northColorCount = _southColorCount = 0;
    }
    
    /**
     * Gets the Shape of the north needle tip for a specified direction.
     * The direction is quantized to an integer in the range 0-355 degrees.
     * 
     * @param direction the direction, in radians
     * @return the Shape
     */
    public Shape getNorthShape( double direction ) {
        int index = (int) Math.abs( Math.toDegrees( direction ) ) % 360;
        if ( direction < 0 ) {
            index = ( -index + 360 ) % 360;
        }
        
        Shape shape = _shapeTable[ index ];
        if ( shape == null ) {
            _somePath.reset();
            _somePath.moveTo( 0, -( _needleSize.height / 2 ) );
            _somePath.lineTo( ( _needleSize.width / 2 ), 0 );
            _somePath.lineTo( 0, ( _needleSize.height / 2 ) );
            _somePath.closePath();
         
            _someTransform.setToIdentity();
            _someTransform.rotate( Math.toRadians( index ) );
            shape = _someTransform.createTransformedShape( _somePath );
            
            _shapeTable[ index ] = shape;
            
            _shapeCount++;
            assert( _shapeCount <= NUMBER_OF_SHAPES );
        }
        return shape;
    }
    
    /**
     * Gets the Shape of the south needle tip for a specified direction.
     * The direction is quantized to an integer in the range 0-355 degrees.
     * 
     * @param direction the direction, in radians
     * @return the Shape
     */
    public Shape getSouthShape( double direction ) {
        return getNorthShape( direction + Math.PI );
    }
    
    /**
     * Gets the Color of the north needle tip for a specified strength.
     * The strength is quantized to an integer in the range 0-255.
     * 
     * @param strength the strength, 0.0...1.0
     * @return the 
     */
    public Color getNorthColor( double strength ) {
        assert( strength >= 0 && strength <= 1 );
        int index = (int) ( 255 * strength );
        Color color = _northColorTable[ index ];
        if ( color == null ) {
            if ( _alphaEnabled ) {
                // Alpha works on any background, but you pay a peformance price.
                color = new Color( 255, 0, 0, index );
            }
            else {
                // Color saturation assumes a black background.
                color = new Color( index, 0, 0 );
            }
            _northColorTable[ index ] = color;
            
            _northColorCount++;
            assert( _northColorCount <= NUMBER_OF_COLORS );
        }
        return color;
    }
    
    /**
     * Gets the Color of the south needle tip for a specified strength.
     * The strength is quantized to an integer in the range 0-255.
     * 
     * @param strength the strength, 0.0...1.0
     * @return the Color
     */
    public Color getSouthColor( double strength ) {
        assert( strength >= 0 && strength <= 1 );
        int index = (int) ( 255 * strength );
        Color color = _southColorTable[ index ];
        if ( color == null ) {
            if ( _alphaEnabled ) {
                // Alpha works on any background, but you pay a peformance price.
                color = new Color( 255, 255, 255, index );
            }
            else {
                // Color saturation assumes a black background.
                color = new Color( index, index, index );
            }
            _southColorTable[ index ] = color;
            
            _southColorCount++;
            assert( _southColorCount <= NUMBER_OF_COLORS );
        }
        return color;
    }
}