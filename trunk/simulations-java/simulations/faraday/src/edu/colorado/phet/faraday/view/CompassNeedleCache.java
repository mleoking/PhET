/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.faraday.view.NeedleColorStrategy.AlphaColorStrategy;

/**
 * CompassNeedleCache is a cache of the graphics components needed to draw a needle.
 * It contains Shapes for various "directions", and Colors for various "strengths".
 * The cache is populated as Shapes and Colors are requested, or you can call
 * populate to populate the entire cache immediately.
 * <p>
 * Changing the needle size clears the Shape cache.
 * <p>
 * Changing the needle color strategy setting clears the Color cache.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class CompassNeedleCache {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static Dimension DEFAULT_NEEDLE_SIZE = new Dimension( 20, 20 );
    private static NeedleColorStrategy DEFAULT_NEEDLE_COLOR_STRATEGY = new AlphaColorStrategy();
    
    private static int NUMBER_OF_SHAPES = 360; // one for each degree of rotation (0-359)
    private static int NUMBER_OF_COLORS = 256; // one for each possible color component value (0-255)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The size of all needles in the cache.
    private Dimension _needleSize;
    
    // Strategy for creating a needle color that represents strength.
    private NeedleColorStrategy _needleColorStrategy;
    
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
     * Creates a cache with default needle size and color strategy.
     */
    public CompassNeedleCache() {
        this( DEFAULT_NEEDLE_SIZE );
    }
    
    /**
     * Creates a cache with specified needle size and default color strategy.
     * 
     * @param needleSize
     */
    public CompassNeedleCache( Dimension needleSize ) {
        this( needleSize, DEFAULT_NEEDLE_COLOR_STRATEGY );
    }
    
    /**
     * Creates a cache with specified needle size and color strategy.
     * 
     * @param needleSize
     * @param needleColorStrategy
     */
    public CompassNeedleCache( Dimension needleSize, NeedleColorStrategy needleColorStrategy ) {
        _needleSize = new Dimension( needleSize.width, needleSize.height );
        _needleColorStrategy = needleColorStrategy;
        _shapeTable = new Shape[NUMBER_OF_SHAPES];
        _northColorTable = new Color[NUMBER_OF_COLORS];
        _southColorTable = new Color[NUMBER_OF_COLORS];
        _shapeCount = _northColorCount = _southColorCount = 0;
        _somePath = new GeneralPath();
        _someTransform = new AffineTransform();
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
        if ( _needleSize.width != size.width && _needleSize.height != size.height ) {
            _needleSize.setSize( size.width, size.height );
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
     * Sets the strategy used to map strength to a needle color.
     * Clears the color portion of the cache.
     * 
     * @param needleColorStrategy
     */
    public void setNeedleColorStrategy( NeedleColorStrategy needleColorStrategy ) {
        _needleColorStrategy = needleColorStrategy;
        clearColors();
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
            color = _needleColorStrategy.getNorthColor( strength );
            _northColorTable[index] = color;
            _northColorCount++;
            assert ( _northColorCount <= NUMBER_OF_COLORS );
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
            color = _needleColorStrategy.getSouthColor( strength );
            _southColorTable[ index ] = color;
            _southColorCount++;
            assert( _southColorCount <= NUMBER_OF_COLORS );
        }
        return color;
    }
}