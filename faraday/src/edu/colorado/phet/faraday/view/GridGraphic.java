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
    private ArrayList _compasses; // array of MiniCompassGraphic
    
    /**
     * @param component
     */
    public GridGraphic( Component component, BarMagnet barMagnetModel, int xDensity, int yDensity) {
        super( component );
        
        _barMagnetModel = barMagnetModel;
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
        MiniCompassGraphic compass;
        
        System.out.println( "apparatus panel dimensions: " + component.getSize() );

        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                compass = new MiniCompassGraphic( component );
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

    /**
     * Synchronize view with model.
     */
    public void update() {
        MiniCompassGraphic compass;
        for ( int i = 0; i < _compasses.size(); i++ ) {
            compass = (MiniCompassGraphic)_compasses.get(i);
            // XXX set strength and direction based on magnet field strength at compass location.
        }
    }
}
