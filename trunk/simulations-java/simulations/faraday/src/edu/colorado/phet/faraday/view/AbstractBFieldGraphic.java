/* Copyright 2004-2010, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * AbstractBFieldGraphic is the base class for our visualization of a B-Field.
 * As an alternative to a field diagram, the grid shows the strength and orientation 
 * of a magnetic field (B-field) at a 2D grid of compass needles.
 * <p>
 * See paint for important assumptions about the implementation of this class.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractBFieldGraphic extends PhetGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension DEFAULT_NEEDLE_SIZE = new Dimension( 20, 40 );
    
    private static final RenderingHints RENDERING_HINTS = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AbstractMagnet _magnetModel;
    
    // The spacing between grid points, in pixels.
    private int _xSpacing, _ySpacing;
    
    // Points in the grid.
    private GridPoint[] _gridPoints;
    
    // Cache of needle Shapes and Colors
    private CompassNeedleCache _needleCache;
    
    // Needles with a strength below this value are not drawn.
    private final double _strengthThreshold;
    
    // The grid's bounds.
    private final Rectangle _gridBounds;
    
    // A reusable point
    private final Point _point;
    
    // A reusable vector
    private final Vector2D _fieldVector;
    
    // Scaling of the intensity, see setIntensityScale
    private double _intensityScale;
    
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * GridPoint describes a point on the grid.
     */
    protected class GridPoint {

        public double _x, _y;
        public double _direction;
        public double _intensity;

        public GridPoint( double x, double y ) {
            this( x, y, 0, 0 );
        }

        public GridPoint( double x, double y, double direction, double strength ) {
            _x = x;
            _y = y;
            _direction = direction;
            _intensity = strength;
        }
        
        public void setX( double x ) {
            _x = x;
        }
        
        public double getX() {
            return _x;
        }
        
        public void setY( double y ) {
            _y = y;
        }
        
        public double getY() {
            return _y;
        }
        
        public void setLocation( double x, double y ) {
            _x = x;
            _y = y;
        }

        public void setDirection( double direction ) {
            _direction = direction;
        }

        public double getDirection() {
            return _direction;
        }

        public void setIntensity( double intensity ) {
            if ( !( intensity >= 0 && intensity <= 1 ) ) {
                throw new IllegalArgumentException( "intensity out of bounds: " + intensity );
            }
            _intensity = intensity;
        }

        public double getIntensity() {
            return _intensity;
        }
        
        @Override
        public String toString() {
            return "GridPoint x=" + _x + " y=" + _y + " direction=" + _direction + " intensity=" + _intensity;
        }
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param component the parent Component
     * @param magnetModel
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public AbstractBFieldGraphic( Component component, AbstractMagnet magnetModel, int xSpacing, int ySpacing ) {
        super( component );
        assert( component != null );
        
        setIgnoreMouse( true );
        
        _magnetModel = magnetModel;
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        
        _strengthThreshold = FaradayConstants.GRID_BFIELD_THRESHOLD;
        
        _needleCache = new CompassNeedleCache();
        _needleCache.setNeedleSize( DEFAULT_NEEDLE_SIZE );
        _needleCache.populate();
        
        _gridBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() ); // default to parent size
        _point = new Point();
        _fieldVector = new Vector2D();
        _intensityScale = FaradayConstants.GRID_INTENSITY_SCALE;
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------
    
    /*
     * Creates the points in the grid.
     * This method must be implemented by all subclasses.
     */
    protected abstract GridPoint[] createGridPoints();
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
        setBoundsDirty(); // the graphic's bounds are now dirty
        updateGridPoints();
    }
    
    /*
     * Gets a reference to the bounds for the grid.
     * Do not modify the bounds returned; use setBounds to modify.
     */
    protected Rectangle getGridBoundsReference() {
        return _gridBounds;
    }
    
    /**
     * Set the intensity scale.
     * In reality, the B-field drops off very quickly as we move away from the magnet,
     * and we wouldn't be able to see very much of the field.  So we scale the intensity
     * of the compass needles in our view so that we see more of the field.
     * Smaller values make the field appear to drop off more rapidly.
     * Larger values make the field appear to drop off more slowly.
     * 
     * @param intensityScale
     */
    public void setIntensityScale( double intensityScale ) {
        if ( intensityScale < 1 ) {
            throw new IllegalArgumentException( "intensityScale must be >= 1: " + intensityScale );
        }
        if ( intensityScale != _intensityScale ) {
            _intensityScale = intensityScale;
            updateGridPoints();
        }
    }
    
    public double getIntensityScale() {
        return _intensityScale;
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
            g2.setRenderingHints( RENDERING_HINTS );
            
            // Draw the needles.
            double previousX, previousY;
            previousX = previousY = 0;
            for ( int i = 0; i < _gridPoints.length; i++ ) {
                
                GridPoint gridPoint = _gridPoints[i];
                
                northColor = _needleCache.getNorthColor( gridPoint.getIntensity() );
                southColor = _needleCache.getSouthColor( gridPoint.getIntensity() );
                northShape = _needleCache.getNorthShape( gridPoint.getDirection() );
                southShape = _needleCache.getSouthShape( gridPoint.getDirection() );
                
                if ( gridPoint.getIntensity() >= _strengthThreshold ) {
                    
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
            for ( int i = 0; i < _gridPoints.length; i++ ) {

                GridPoint gridPoint = _gridPoints[i];
                
                if ( _magnetModel.getStrength() == 0 ) {
                    gridPoint.setIntensity( 0 );
                }
                else {
                    // Get the magnetic field information at the needle's location.
                    _point.setLocation( gridPoint.getX(), gridPoint.getY() );
                    _magnetModel.getBField( _point, _fieldVector /* output */ );
                    double angle = _fieldVector.getAngle();
                    double magnitude = _fieldVector.getMagnitude();
                    
                    // convert scaled magnitude to intensity
                    double intensity = magnitude / _magnetModel.getMaxStrength();
                    assert( intensity >= 0 && intensity <= 1 );
                    
                    // scale the intensity, because in reality this drops off and we wouldn't see much of the field
                    double scaledIntensity = Math.pow( intensity, 1 / _intensityScale );

                    // Update the grid point.
                    gridPoint.setIntensity( scaledIntensity );
                    gridPoint.setDirection( angle );
                }
            }
        }
    }
}
