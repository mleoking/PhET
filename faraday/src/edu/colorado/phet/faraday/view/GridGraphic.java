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
import edu.colorado.phet.faraday.model.BarMagnet;


/**
 * GridGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GridGraphic extends CompositePhetGraphic implements SimpleObserver {

    private BarMagnet _barMagnetModel;
    private int _xSpacing;
    private int _ySpacing;
    private Dimension _needleSize;
    private ArrayList _compasses; // array of MiniCompassGraphic
    
    /**
     * @param component
     */
    public GridGraphic( Component component, BarMagnet barMagnetModel, int xDensity, int yDensity) {
        super( component );
        
        _barMagnetModel = barMagnetModel;
        _needleSize = new Dimension( 40, 20 );
        _compasses = new ArrayList();
        
        setSpacing( xDensity, yDensity );
    }
    
    public void setSpacing( int xSpacing, int ySpacing ) {
        
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        
        // Clear existing compasses.
        _compasses.clear();
        super.clear();
        
        // Create new compasses.
        Component component = getComponent();
        System.out.println( "component="  + component );
        int width = component.getWidth();
        int height = component.getHeight();
        int xCount = (width / xSpacing) + 2;  // HACK
        int yCount = (height / ySpacing) + 2;  // HACK
        MiniCompassShape compass;
        
        System.out.println( "apparatus panel dimensions: " + component.getSize() );

        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                compass = new MiniCompassShape( component );
                compass.setLocation( i * xSpacing, j * ySpacing );
                _compasses.add( compass );
                super.addGraphic( compass );
            }
        }
        
        update();
    }
    
    public int getXSpacing() {
        return _xSpacing;
    }
    
    public int getYSpacing() {
        return _ySpacing;
    }

    public void setNeedleSize( final Dimension needleSize ) {
        _needleSize = new Dimension( needleSize );
        for ( int i = 0; i < _compasses.size(); i++ ) {
            MiniCompassShape compass = (MiniCompassShape)_compasses.get(i);
            compass.setSize( _needleSize );
        }
        update();
    }
    
    public Dimension getNeedleSize() {
        return new Dimension( _needleSize );
    }
    
    /**
     * Synchronize view with model.
     */
    public void update() {
        double magnetStrength = _barMagnetModel.getStrength();
        for ( int i = 0; i < _compasses.size(); i++ ) {
            
            MiniCompassShape compass = (MiniCompassShape)_compasses.get(i);
            
            Point2D p = new Point2D.Double( compass.getX(), compass.getY() );
            
            double direction = _barMagnetModel.getDirection( p );
            compass.setDirection( direction );
            
            double pointStrength = _barMagnetModel.getStrength( p );
            compass.setStrength( pointStrength / magnetStrength );
        }
        repaint();
    }
}
