/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.module;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.view.ProbeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * PHScaleCanvas is the canvas for PHScaleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension RENDERING_SIZE = new Dimension( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private PHScaleModel _model;
    
    // View 
    private PNode _rootNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHScaleCanvas( PHScaleModel model ) {
        super();
        setWorldTransformStrategy( new RenderingSizeStrategy( this, RENDERING_SIZE ) );
        
        _model = model;
        
        setBackground( PHScaleConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        ProbeNode probeNode = new ProbeNode( 500 );
        probeNode.setOffset( 100, 100 );//XXX
        _rootNode.addChild( probeNode );
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
        else if ( PHScaleConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PHScaleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
