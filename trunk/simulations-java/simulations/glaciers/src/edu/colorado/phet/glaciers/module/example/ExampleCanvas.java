/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.defaults.ExampleDefaults;
import edu.colorado.phet.glaciers.module.GlaciersAbstractCanvas;
import edu.colorado.phet.glaciers.view.ExampleNode;

/**
 * ExampleCanvas is the canvas for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleCanvas extends GlaciersAbstractCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private ExampleModel _model;
    
    // View 
    private ExampleNode _exampleNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleCanvas( ExampleModel model ) {
        super( ExampleDefaults.VIEW_SIZE );
        
        _model = model;
        
        setBackground( GlaciersConstants.CANVAS_BACKGROUND );
        
        _exampleNode = new ExampleNode( _model.getExampleModelElement(), _model.getModelViewTransform() );
        
        addNode( _exampleNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ExampleNode getExampleNode() {
        return _exampleNode;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GlaciersConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
