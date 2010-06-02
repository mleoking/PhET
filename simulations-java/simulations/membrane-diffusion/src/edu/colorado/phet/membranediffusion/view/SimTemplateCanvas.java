/* Copyright 2007, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.SimTemplateConstants;
import edu.colorado.phet.membranediffusion.model.SimTemplateModel;
import edu.colorado.phet.membranediffusion.module.SimTemplateDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas template.
 */
public class SimTemplateCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private SimTemplateModel model;
    
    // View 
    private PNode _rootNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SimTemplateCanvas( SimTemplateModel model ) {
        super( SimTemplateDefaults.VIEW_SIZE );
        
        this.model = model;
        
        setBackground( SimTemplateConstants.CANVAS_BACKGROUND );
        
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
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( SimTemplateConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
