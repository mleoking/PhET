/* Copyright 2007, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.model.NeuronModel;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * ExampleCanvas is the canvas for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeuronCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private NeuronModel model;
    
    // View 
    private PNode _rootNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NeuronCanvas( NeuronModel model ) {
        super( NeuronDefaults.VIEW_SIZE );
        
        this.model = model;
        
        setBackground( NeuronConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        ParticleNode particleNode = new ParticleNode( model.getParticle() );
        _rootNode.addChild(particleNode);
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
        else if ( NeuronConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
