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
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.model.AbstractMagnet;


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
    
    // Determines whether needle strength is displayed using alpha or color saturation. 
    private static final boolean DEFAULT_ALPHA_ENABLED = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Size of the parent component
    private Dimension _parentSize;
    
    // The magnet model element that the grid is observing.
    private AbstractMagnet _magnetModel;
    
    // The spacing between compass needles, in pixels.
    private int _xSpacing, _ySpacing;
    
    // The size of the compass needles, in pixels.
    private Dimension _needleSize;
    
    // The compass needles that are in the grid (array of CompassGridNeedle).
    private ArrayList _needles;
    
    // Controls whether alpha is used to render the needles.
    private boolean _alphaEnabled;
    
    // The grid's bounds.
    private Rectangle _bounds;
    
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
        
        _needleSize = new Dimension( 40, 20 );
        _needles = new ArrayList();
        
        _alphaEnabled = DEFAULT_ALPHA_ENABLED;
        
        _parentSize = component.getSize();
        
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
        int xCount = (int) ( _parentSize.width / xSpacing ) + 1;
        int yCount = (int) ( _parentSize.height / ySpacing ) + 1;
        
        // Create the needles.
        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                CompassGridNeedle needle = new CompassGridNeedle();
                needle.setLocation( i * xSpacing, j * ySpacing );
                needle.setSize( _needleSize );
                needle.setAlphaEnabled( _alphaEnabled );
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
        _needleSize = new Dimension( needleSize );
        for ( int i = 0; i < _needles.size(); i++ ) {
            CompassGridNeedle needle = (CompassGridNeedle) _needles.get(i);
            needle.setSize( _needleSize );
        }
        update();
    }
    
    /**
     * Gets the size of all needles.
     * 
     * @return the size
     */
    public Dimension getNeedleSize() {
        return new Dimension( _needleSize );
    }
    
    /**
     * Determines whether alpha or color saturation are used to render the needles.
     * Color saturation is implemented to work on a black background.
     * 
     * @param enabled true to use alpha, false to use color saturation
     */
    public void setAlphaEnabled( boolean enabled ) {
        if ( enabled != _alphaEnabled ) {
            _alphaEnabled = enabled;
            for ( int i = 0; i < _needles.size(); i++ ) {
                CompassGridNeedle needle = (CompassGridNeedle) _needles.get(i);
                needle.setAlphaEnabled( enabled );
            }
            repaint();
        }
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
     * Draws all of the needles in the grid.
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
            super.saveGraphicsState( g2 );
            g2.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            // Draw the needles.
            for ( int i = 0; i < _needles.size(); i++ ) {
                CompassGridNeedle needle = (CompassGridNeedle)_needles.get(i);
                needle.paint( g2 );
            }
            super.restoreGraphicsState();
            setBoundsDirty();
        }
    }
    
    /**
     * Determines the bounds of the grid.
     * For efficiency, the bounds are precomputed in update.
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
            
            super.setBoundsDirty();
            _bounds = new Rectangle();
            
            double magnetStrength = _magnetModel.getStrength();
            for ( int i = 0; i < _needles.size(); i++ ) {

                // Next needle...
                CompassGridNeedle needle = (CompassGridNeedle)_needles.get(i);
                
                // Add the needle to the bounds.
                _bounds = _bounds.union( needle.getBounds() );

                // Get the magnetic field information at the needle's location.
                Point2D p = needle.getLocation();
                AbstractVector2D fieldStrength = _magnetModel.getStrength( p );
                double angle = fieldStrength.getAngle();
                double magnitude = fieldStrength.getMagnitude();
                
                // Set the needle's direction.
                needle.setDirection( angle );
                
                // Set the needle's strength.
                {
                    // Convert the field strength to a value in the range 0...+1.
                    double scale = ( magnitude / magnetStrength );
                    
                    // Adjust the scale to improve the visual effect.
                    scale = Rescaler.rescale( scale, magnetStrength );
                    scale = MathUtil.clamp( 0, scale, 1 );
                    
                    // Set the needle strength.
                    needle.setStrength( scale );
                }
            }
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.view.ApparatusPanel2.ChangeListener#canvasSizeChanged(edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent)
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _parentSize = event.getCanvasSize();
        resetSpacing();   
    }
}
