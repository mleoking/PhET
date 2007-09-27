/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.dummy;

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
    
    private PImage _cartoon;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DummyCanvas( DummyModel model ) {
        super( DummyDefaults.VIEW_SIZE );
        
        _model = model;
        
        setBackground( GlaciersConstants.CANVAS_BACKGROUND );
        
        _cartoon = new PImage( GlaciersResources.getImage( "cartoon.png" ) );
        _cartoon.scale( 1.5 );
        
        addNode( _cartoon );
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
        
        // center the cartoon
        double x = ( worldSize.getWidth() - _cartoon.getFullBoundsReference().getWidth() ) / 2;
        double y = ( worldSize.getHeight() - _cartoon.getFullBoundsReference().getHeight() ) / 2;
        _cartoon.setOffset( x, y );
    }
}
