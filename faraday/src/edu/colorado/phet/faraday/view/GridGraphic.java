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
    private ArrayList _needles; // array of NeedleGraphic
    
    /**
     * @param component
     */
    public GridGraphic( Component component, BarMagnet barMagnetModel, int xDensity, int yDensity) {
        super( component );
        
        _barMagnetModel = barMagnetModel;
        _needleSize = new Dimension( 40, 20 );
        _needles = new ArrayList();
        
        setSpacing( xDensity, yDensity );
    }
    
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
        NeedleGraphic needle;

        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                needle = new NeedleGraphic( component );
                needle.setLocation( i * xSpacing, j * ySpacing );
                needle.setSize( _needleSize );
                _needles.add( needle );
                super.addGraphic( needle );
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
        for ( int i = 0; i < _needles.size(); i++ ) {
            NeedleGraphic needle = (NeedleGraphic)_needles.get(i);
            needle.setSize( _needleSize );
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
        for ( int i = 0; i < _needles.size(); i++ ) {
            
            NeedleGraphic needle = (NeedleGraphic)_needles.get(i);
            
            Point2D p = needle.getLocation();
            
            double direction = _barMagnetModel.getDirection( p );
            needle.setDirection( direction );
            
            double pointStrength = _barMagnetModel.getStrength( p );
            needle.setStrength( pointStrength / magnetStrength );
        }
        repaint();
    }
}
