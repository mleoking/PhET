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
import java.util.ArrayList;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.util.IRescaler;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * CompassGridGraphic is the graphical representation of a "compass grid".
 * As an alternative to a field diagram, the grid shows the strength
 * and orientation of a magnetic field at a grid of points in 2D space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGridGraphic extends PhetGraphic implements SimpleObserver, ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Needles with strength below this value are not drawn.
    private static final double DEFAULT_STRENGTH_THRESHOLD = 0.01;
    
    // Strategy that uses alpha to indicated field strength.
    private static final int ALPHA_STRATEGY = 0;
    
    // Strategy that uses color saturation to indicated field strength.
    private static final int SATURATION_STRATEGY = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The magnet model element that the grid is observing.
    private AbstractMagnet _magnetModel;
    
    // Handles rescaling the field to improve the visual effect.
    private IRescaler _rescaler;
    
    // The spacing between compass needles, in pixels.
    private int _xSpacing, _ySpacing;
    
    // Descriptions of the needles that are in the grid (array of NeedleDescriptor).
    private ArrayList _needles;
    
    // Cache of needle Shapes and Colors
    private CompassNeedleCache _needleCache;
    
    // Strategy used to indicate field strength;
    private int _strengthStrategy;
    
    // Needles with a strength below this value are not drawn.
    private double _strengthThreshold;
    
    // The grid's bounds.
    private Rectangle _bounds;
    
    // Rendering hints.
    private final RenderingHints _renderingHints;
    
    // A reusable point
    private Point _point;
    
    // A reusable vector
    private Vector2D _fieldVector;
    
    // Lightweight data structure for holding a needle description.
    private class NeedleDescriptor {
        public double x, y;
        public double direction;
        public double strength;
    }
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param magnetModel the magnet model
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public CompassGridGraphic( Component component, AbstractMagnet magnetModel, int xSpacing, int ySpacing) {
        super( component );
        assert( component != null );
        assert( magnetModel != null );
        
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        _needles = new ArrayList();
        
        _strengthStrategy = ALPHA_STRATEGY;  // works on any background color
        _strengthThreshold = DEFAULT_STRENGTH_THRESHOLD;
        
        _needleCache = new CompassNeedleCache();
        _needleCache.setNeedleSize( 40, 20 );
        _needleCache.setAlphaEnabled( _strengthStrategy == ALPHA_STRATEGY );
        _needleCache.populate();
        
        _bounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        _point = new Point();
        _fieldVector = new Vector2D();
        
        _renderingHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        setSpacing( xSpacing, ySpacing );
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Set the rescaler, used to make values look better when displayed.
     * 
     * @param rescaler
     */
    public void setRescaler( IRescaler rescaler ) {
        _rescaler = rescaler;
    }
    
    /**
     * Sets the spacing between points on the grid.
     * 
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public void setSpacing( int xSpacing, int ySpacing ) {
        
        // Save the spacing, for use by getters and restore.
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        
        // Clear existing needles.
        _needles.clear();
        
        // Determine how many needles are needed to fill the parent component.
        int xCount = (int) ( _bounds.width / xSpacing ) + 1;
        int yCount = (int) ( _bounds.height / ySpacing ) + 1;
        
        // Create the needles.
        boolean alphaEnabled = ( _strengthStrategy == ALPHA_STRATEGY );
        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                NeedleDescriptor needle = new NeedleDescriptor();
                needle.x = i * xSpacing;
                needle.y = j * ySpacing;
                _needles.add( needle );
            }
        }
        
        update();
    }
    
    /**
     * Resets the grid spacing.
     * This should be called when the parent container is resized.
     */
    public void resetSpacing() {
        setSpacing( _xSpacing, _ySpacing );
    }
    
    /**
     * Gets the spacing between grid point in the X direction.
     * 
     * @return X spacing
     */
    public int getXSpacing() {
        return _xSpacing;
    }
    
    /**
     * Gets the spacing between grid point in the Y direction.
     * 
     * @return Y spacing
     */
    public int getYSpacing() {
        return _ySpacing;
    }

    /**
     * Sets the size of all needles.
     * 
     * @param needleSize the needle size
     */
    public void setNeedleSize( final Dimension needleSize ) {
        assert( needleSize != null );
        _needleCache.setNeedleSize( needleSize );
        _needleCache.populate();
        update();
    }
    
    /**
     * Gets the size of all needles.
     * 
     * @return the size
     */
    public Dimension getNeedleSize() {
        return new Dimension( _needleCache.getNeedleSize() );
    }
   
    
    /**
     * Convenience method for setting the strategy used to represent field strength.
     * If the color is black, then color saturation is used.
     * 
     * @see setStrengthStrategy
     * @param color
     */
    public void setGridBackground( Color color ) {
        if ( color.equals( Color.BLACK ) ) {
            setStrengthStrategy( SATURATION_STRATEGY );
        }
        else {
            setStrengthStrategy( ALPHA_STRATEGY );
        }
    }
    
    /*
     * Sets the strategy used to represent field strength when rendering needles.
     * <p>
     * ALPHA_STRATEGY uses the alpha component and will work on any background color.
     * SATURATION_STRATEGY uses color saturation and will work only on a black background.
     * 
     * @param strengthStrategy ALPHA_STRATEGY or SATURATION_STRATEGY
     */
    private void setStrengthStrategy( int strengthStrategy ) {
        assert( strengthStrategy == ALPHA_STRATEGY || strengthStrategy == SATURATION_STRATEGY );
        _needleCache.setAlphaEnabled( strengthStrategy == ALPHA_STRATEGY );
        repaint();
    }
    
    /**
     * Sets the strength threshold.
     * Needles with a strength below this value will not be drawn.
     * 
     * @param strengthThreshold the strength threshold, in Gauss
     */
    public void setStrengthThreshold( double strengthThreshold ) {
        if ( strengthThreshold != _strengthThreshold ) {
            _strengthThreshold = strengthThreshold;
            repaint();
        }
    }
    
    /**
     * Gets the strength threshold.
     * Needles with a strength below this value will not be drawn.
     * 
     * @return the strength threshold, in Gauss
     */
    public double getStrengthThreshold() {
        return _strengthThreshold;
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
    /**
     * Since this graphic does not handle location, override it to throw an exception.
     */
    public void setLocation( int x, int y ) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Since this graphic does not handle location, override it to throw an exception.
     */
    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    /**
     * Draws all of the needles in the grid that have a strength above the threshold.
     * <p>
     * This method is optimized with the following assumptions:
     * <ul>
     * <li>the grid's location is (0,0)
     * <li>the grid's registration point is (0,0)
     * <li>the grid has no transforms applied to it
     * </ul>
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) { 
            Color northColor, southColor;
            Shape northShape, southShape;
            
            super.saveGraphicsState( g2 );
            g2.setRenderingHints( _renderingHints );
            
            // Draw the needles.
            double previousX, previousY;
            previousX = previousY = 0;
            for ( int i = 0; i < _needles.size(); i++ ) {
                NeedleDescriptor needle = (NeedleDescriptor)_needles.get(i);
                
                northColor = _needleCache.getNorthColor( needle.strength );
                southColor = _needleCache.getSouthColor( needle.strength );
                northShape = _needleCache.getNorthShape( needle.direction );
                southShape = _needleCache.getSouthShape( needle.direction );
                
                
                if ( needle.strength >= _strengthThreshold ) {
                    
                    g2.translate( needle.x - previousX, needle.y - previousY );
                    g2.setPaint( northColor );
                    g2.fill( northShape );
                    g2.setPaint( southColor );
                    g2.fill( southShape );
                    
                    previousX = needle.x;
                    previousY = needle.y;
                }
            }
            
            super.restoreGraphicsState();
        }
    }
    
    /**
     * Determines the bounds of the grid.
     * The bounds are based on the apparatus panel's canvas size, as set in canvasSizeChanged.
     * 
     * @return the bounds of the grid
     */
    protected Rectangle determineBounds() {
        return _bounds;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( isVisible() ) {
            
            // For each needle in the grid...
            for ( int i = 0; i < _needles.size(); i++ ) {

                // Next needle...
                NeedleDescriptor needle = (NeedleDescriptor)_needles.get(i);

                // Get the magnetic field information at the needle's location.
                _point.setLocation( needle.x, needle.y );
                _magnetModel.getStrength( _point, _fieldVector /* output */ );
                double angle = _fieldVector.getAngle();
                double magnitude = _fieldVector.getMagnitude();
                
                // Set the needle's direction.
                needle.direction = angle;
                
                // Set the needle's strength.
                {
                    // Convert the field strength to a value in the range 0...+1.
                    double magnetStrength = _magnetModel.getStrength();
                    double scale = 0;
                    if ( magnetStrength != 0 ) {
                        
                        // Start with a scale relative to the current strength of the magnet.
                        scale = ( magnitude / magnetStrength );
                        scale = MathUtil.clamp( 0, scale, 1 );
                        
                        // Adjust the scale to improve the visual effect.
                        if ( _rescaler != null ) {
                            scale = _rescaler.rescale( scale );
                        }
                        
                        // Adjust the scale to the maximum magnet strength.
                        scale = scale * ( magnetStrength / _magnetModel.getMaxStrength() );
                    }
                    
                    // Set the needle strength.
                    needle.strength = scale;
                }
            }
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the grid bounds whenever the apparatus panel's canvas size changes.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        Dimension parentSize = event.getCanvasSize();
        _bounds.setBounds( 0, 0, parentSize.width, parentSize.height );
        super.setBoundsDirty();
        resetSpacing(); 
    }
}
