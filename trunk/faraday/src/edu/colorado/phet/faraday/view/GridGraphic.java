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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.faraday.model.IMagnet;


/**
 * GridGraphic is the graphical representation of a "compass grid".
 * As an alternative to a field diagram, the grid shows the strength
 * and orientation of a magnet field at various points in 2D space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GridGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private IMagnet _magnetModel;
    private int _xSpacing;
    private int _ySpacing;
    private Dimension _needleSize;
    private ArrayList _needles; // array of CompassNeedleGraphic
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param magnetModel the magnet model
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public GridGraphic( Component component, IMagnet magnetModel, int xSpacing, int ySpacing) {
        super( component );
        
        _magnetModel = magnetModel;
        _needleSize = new Dimension( 40, 20 );
        _needles = new ArrayList();
        
        setSpacing( xSpacing, ySpacing );
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
        
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        
        // Clear existing needles.
        _needles.clear();
        super.clear();
        
        // Create new compasses.
        Component component = getComponent();
        int width = component.getWidth();
        int height = component.getHeight();
        int xCount = (width / xSpacing) + 2;  // HACK
        int yCount = (height / ySpacing) + 2;  // HACK
        CompassNeedleGraphic needle;

        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                needle = new CompassNeedleGraphic( component );
                needle.setLocation( i * xSpacing, j * ySpacing );
                needle.setSize( _needleSize );
                _needles.add( needle );
                super.addGraphic( needle );
            }
        }
        
        update();
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
     * Sets the size of all compass needles.
     * 
     * @param needleSize the needle size
     */
    public void setNeedleSize( final Dimension needleSize ) {
        _needleSize = new Dimension( needleSize );
        for ( int i = 0; i < _needles.size(); i++ ) {
            CompassNeedleGraphic needle = (CompassNeedleGraphic)_needles.get(i);
            needle.setSize( _needleSize );
        }
        update();
    }
    
    /**
     * Gets the size of all compass needles.
     * 
     * @return the size
     */
    public Dimension getNeedleSize() {
        return new Dimension( _needleSize );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        double magnetStrength = _magnetModel.getStrength();
        for ( int i = 0; i < _needles.size(); i++ ) {
            
            CompassNeedleGraphic needle = (CompassNeedleGraphic)_needles.get(i);
            
            Point2D p = needle.getLocation();
            
            double direction = _magnetModel.getDirection( p );
            needle.setDirection( direction );
            
            double pointStrength = _magnetModel.getStrength( p );
            needle.setStrength( pointStrength / magnetStrength );
        }
        repaint();
    }
}
