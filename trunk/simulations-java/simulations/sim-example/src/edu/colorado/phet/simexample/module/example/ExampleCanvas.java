/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simexample.module.example;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.simexample.SimExampleConstants;
import edu.colorado.phet.simexample.defaults.ExampleDefaults;
import edu.colorado.phet.simexample.view.ExampleNode;
import edu.umd.cs.piccolo.PNode;

/**
 * The canvas for the "Example" module.
 */
public class ExampleCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private ExampleModel model;
    
    // View 
    private PNode _rootNode;
    private ExampleNode _exampleNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleCanvas( ExampleModel model ) {
        super( ExampleDefaults.VIEW_SIZE );
        
        this.model = model;
        
        setBackground( SimExampleConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        _exampleNode = new ExampleNode( model.getExampleModelElement() );
        _rootNode.addChild( _exampleNode );
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
        else if ( SimExampleConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
