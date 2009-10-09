/* Copyright 2007, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all Piccolo canvases in this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private RPALModel model;
    
    // View 
    private PNode _rootNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public RPALCanvas( RPALModel model ) {
        super( SandwichShopDefaults.VIEW_SIZE );
        
        this.model = model;
        
        setBackground( RPALConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
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
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( RPALConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
