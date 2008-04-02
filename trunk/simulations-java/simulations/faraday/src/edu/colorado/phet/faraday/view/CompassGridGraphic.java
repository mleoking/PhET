/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * CompassGridGraphic is the graphical representation of a "compass grid".
 * As an alternative to a field diagram, the grid shows the strength
 * and orientation of a magnetic field (B-field) at a grid of points in 2D space.
 * <p>
 * See paint for important assumptions about the implementation of this class.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CompassGridGraphic extends PhetGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Determines how the magnetic field decreases with the distance from the magnet.
    private static final double DISTANCE_EXPONENT = 3.0;
    
    // Threshold for applying rescaling of field strength. 
    private static final double RESCALE_THRESHOLD = 0.8;  // 0 ... 1
    
    // Exponent used for rescaling field strength.
    private static final double RESCALE_EXPONENT = 0.4;   // 0.3 ... 0.8
    
    private static final Dimension DEFAULT_NEEDLE_SIZE = new Dimension( 20, 40 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    
    // Controls rescaling of field strength.
    private boolean _rescalingEnabled;
    
    // The spacing between compass needles, in pixels.
    private int _xSpacing, _ySpacing;
    
    // Points in the grid, array of GridPoint
    private ArrayList _gridPoints;
    
    // Cache of needle Shapes and Colors
    private CompassNeedleCache _needleCache;
    
    // Needles with a strength below this value are not drawn.
    private double _strengthThreshold;
    
    // The grid's bounds.
    private Rectangle _gridBounds;
    
    // Rendering hints.
    private final RenderingHints _renderingHints;
    
    // A reusable point
    private Point _point;
    
    // A reusable vector
    private Vector2D _fieldVector;
    
    // Is the grid in the 2D plane of the magnet, or slightly outside the 2D plane?
    private final boolean _inMagnetPlane;
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    // Lightweight data structure for describing a grid point.
    protected class GridPoint {

        public final double _x, _y; // immutable
        public double _direction;
        public double _strength;

        public GridPoint( double x, double y ) {
            this( x, y, 0, 0 );
        }

        public GridPoint( double x, double y, double direction, double strength ) {
            _x = x;
            _y = y;
            _direction = direction;
            _strength = strength;
        }
        
        public double getX() {
            return _x;
        }
        
        public double getY() {
            return _y;
        }

        public void setDirection( double direction ) {
            _direction = direction;
        }

        public double getDirection() {
            return _direction;
        }

        public void setStrength( double strength ) {
            if ( !( strength >= 0 && strength <= 1 ) ) {
                throw new IllegalArgumentException( "strength out of bounds: " + strength );
            }
            _strength = strength;
        }

        public double getStrength() {
            return _strength;
        }
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param magnetModel
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     * @param inMagnetPlane are we showing the field in the 2D plane of the magnet?
     */
    public CompassGridGraphic( Component component, AbstractMagnet magnetModel, int xSpacing, int ySpacing, boolean inMagnetPlane ) {
        super( component );
        assert( component != null );
        
        setIgnoreMouse( true );
        
        _magnetModel = magnetModel;
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        _inMagnetPlane = inMagnetPlane;
        
        _rescalingEnabled = false;
        
        _strengthThreshold = FaradayConstants.GRID_BFIELD_THRESHOLD;
        
        _needleCache = new CompassNeedleCache();
        _needleCache.setNeedleSize( DEFAULT_NEEDLE_SIZE );
        _needleCache.populate();
        
        _gridBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() ); // default to parent size
        _point = new Point();
        _fieldVector = new Vector2D();
        
        _renderingHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------
    
    /*
     * Creates the points in the grid.
     * This method must be implemented by all subclasses.
     */
    protected abstract ArrayList createGridPoints();
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables and disables rescaling, used to make values look better when displayed.
     * 
     * @param rescalingEnabled true or false
     */
    public void setRescalingEnabled( boolean rescalingEnabled ) {
        if ( rescalingEnabled != _rescalingEnabled ) {
            _rescalingEnabled = rescalingEnabled;
            updateGridPoints();
        }
    }
    
    /**
     * Determines whether rescaling is enabled.
     * 
     * @return true or false
     */
    public boolean isRescalingEnabled() {
        return _rescalingEnabled;
    }
    
    /**
     * Sets the spacing between points on the grid.
     * 
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public void setSpacing( int xSpacing, int ySpacing ) {
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        updateGridPoints();
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
        updateGridPoints();
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
     * Convenience method for setting the needle color strategy for a specific background color.
     * 
     * @param color
     */
    public void setGridBackground( Color color ) {
        NeedleColorStrategy strategy = NeedleColorStrategy.createStrategy( color );
        setNeedleColorStrategy( strategy );
    }
    
    /*
     * Sets the strategy used to map strength to a needle color.
     * 
     * @param needleColorStrategy
     */
    protected void setNeedleColorStrategy( NeedleColorStrategy needleColorStrategy ) {
        _needleCache.setNeedleColorStrategy( needleColorStrategy );
    }
    
    /*
     * Sets the bounds for the grid.
     */
    protected void setGridBounds( int x, int y, int width, int height ) {
        _gridBounds.setBounds( x, y, width, height );
        updateGridPoints();
    }
    
    /*
     * Gets a reference to the bounds for the grid.
     * Do not modify the bounds returned; use setBounds to modify.
     */
    protected Rectangle getGridBoundsReference() {
        return _gridBounds;
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            updateGridPoints();
        }
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
            for ( int i = 0; i < _gridPoints.size(); i++ ) {
                GridPoint gridPoint = (GridPoint)_gridPoints.get(i);
                
                northColor = _needleCache.getNorthColor( gridPoint.getStrength() );
                southColor = _needleCache.getSouthColor( gridPoint.getStrength() );
                northShape = _needleCache.getNorthShape( gridPoint.getDirection() );
                southShape = _needleCache.getSouthShape( gridPoint.getDirection() );
                
                if ( gridPoint.getStrength() >= _strengthThreshold ) {
                    
                    g2.translate( gridPoint.getX() - previousX, gridPoint.getY() - previousY );
                    g2.setPaint( northColor );
                    g2.fill( northShape );
                    g2.setPaint( southColor );
                    g2.fill( southShape );
                    
                    previousX = gridPoint.getX();
                    previousY = gridPoint.getY();
                }
            }
            
            super.restoreGraphicsState();
        }
    }
    
    /**
     * Determines the bounds of this graphic.
     * The graphic's bounds are equivalent to the grid bounds.
     * 
     * @return the bounds of the grid
     */
    protected Rectangle determineBounds() {
        return _gridBounds;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    /*
     * Updates all aspects of the grid points.
     */
    protected void updateGridPoints() {
        updatePositions();
        updateStrengthAndOrientation();
        repaint();
    }
    
    /*
     * Updates the grid points' positions to match the current bounds and spacing.
     */
    protected void updatePositions() {
        _gridPoints = createGridPoints();
    }
    
    /*
     * Updates the grid points' strength and direction to match the magnet.
     */
    protected void updateStrengthAndOrientation() {
        
        if ( isVisible() ) {
            
            // For each grid point...
            for ( int i = 0; i < _gridPoints.size(); i++ ) {

                GridPoint gridPoint = (GridPoint)_gridPoints.get(i);

                // Get the magnetic field information at the needle's location.
                _point.setLocation( gridPoint.getX(), gridPoint.getY() );
                if ( _inMagnetPlane ) {
                    _magnetModel.getStrength( _point, _fieldVector /* output */, DISTANCE_EXPONENT );
                }
                else {
                    _magnetModel.getStrengthOutsidePlane( _point, _fieldVector /* output */, DISTANCE_EXPONENT );
                }
                double angle = _fieldVector.getAngle();
                double magnitude = _fieldVector.getMagnitude();
                
                // Set the direction.
                gridPoint.setDirection( angle );
                
                // Set the strength.
                {
                    // Convert the field strength to a value in the range 0...+1.
                    double magnetStrength = _magnetModel.getStrength();
                    double scale = 0;
                    if ( magnetStrength != 0 ) {
                        
                        // Start with a scale relative to the current strength of the magnet.
                        scale = ( magnitude / magnetStrength );
                        scale = MathUtil.clamp( 0, scale, 1 );
                        
                        // Adjust the scale to improve the visual effect.
                        if ( _rescalingEnabled ) {
                            scale = rescale( scale );
                        }
                        
                        // Adjust the scale to the maximum magnet strength.
                        scale = scale * ( magnetStrength / _magnetModel.getMaxStrength() );
                    }
                    
                    // Set the strength.
                    gridPoint.setStrength( scale );
                }
            }
        }
    }
    
    /**
     * Rescales a "field strength" scale value.
     * Since the field strength drops off with the cube of the distance,
     * the grid disappears very quickly around the magnet. 
     * Rescaling makes the grid more visually appealing, but less physically accurate.
     * 
     * @param scale a scale value, in the range 0...1
     * @return the modified scale, in the range 0...1
     */
    private double rescale( double scale ) {
        assert( scale >=0 && scale <= 1 );
        double newStrength = 1.0;
        if ( scale != 0 && scale <= RESCALE_THRESHOLD ) {
            newStrength = Math.pow( scale / RESCALE_THRESHOLD, RESCALE_EXPONENT );
        }
        return newStrength;
    }
}
