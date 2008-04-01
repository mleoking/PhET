/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.faraday.model.BarMagnet;


public class BFieldInsideMagnet extends CompassGridGraphic implements SimpleObserver {

    private BarMagnet _barMagnetModel;
    
    /**
     * Constructor.
     * 
     * @param component
     * @param magnetModel
     * @param xSpacing
     * @param ySpacing
     */
    public BFieldInsideMagnet( Component component, BarMagnet magnetModel, int xSpacing, int ySpacing) {
        super( component, magnetModel, xSpacing, ySpacing );
        
        _barMagnetModel = magnetModel;
        _barMagnetModel.addObserver( this );
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _barMagnetModel.removeObserver( this );
        _barMagnetModel = null;
    }
    
    /*
     * Creates the description of the needles (grid points) in the grid.
     */
    protected ArrayList createNeedleDescriptors() {
        
        Point2D p = _barMagnetModel.getLocation();
        double width = _barMagnetModel.getWidth();
        double height = _barMagnetModel.getHeight();
        
        return null; //XXX
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the magnet changes, update the needle dscriptors.
     */
    public void update() {
        resetSpacing();
        updateNeedleDescriptors();
    }
}
