/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.defaults.DummyDefaults;
import edu.colorado.phet.glaciers.module.GlaciersAbstractCanvas;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * DummyCanvas
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DummyCanvas extends GlaciersAbstractCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private DummyModel _model;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DummyCanvas( DummyModel model ) {
        super( DummyDefaults.VIEW_SIZE );
        
        _model = model;
        
        setBackground( GlaciersConstants.CANVAS_BACKGROUND );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    public void updateLayout() {

        Dimension2D worldSize = getWorldSize();
//        System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        
        //XXX lay out nodes
    }
}
