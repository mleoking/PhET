/* Copyright 2007, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
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
        super( NeuronDefaults.INTERMEDIATE_RENDERING_SIZE );
        setWorldTransformStrategy( new CenteringBoxStrategy( this, NeuronDefaults.INTERMEDIATE_RENDERING_SIZE ) );
        ModelViewTransform2D transform = new ModelViewTransform2D(
                new Rectangle2D.Double(-NeuronDefaults.CROSS_SECTION_RADIUS,-NeuronDefaults.CROSS_SECTION_RADIUS,NeuronDefaults.CROSS_SECTION_RADIUS*2,NeuronDefaults.CROSS_SECTION_RADIUS*2),
                new Rectangle2D.Double( 0,0,NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.width, NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.height) );
        
        this.model = model;

        System.out.println( "transform.modelToView = " + transform.modelToView(0,0) );
        
        setBackground( NeuronConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        ParticleNode particleNode = new ParticleNode( model.getParticle() ,transform);
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
            System.out.println( "getSize() = " + getSize() );
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
