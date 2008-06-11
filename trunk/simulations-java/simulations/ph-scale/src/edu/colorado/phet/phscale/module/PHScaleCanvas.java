/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.module;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.control.*;
import edu.colorado.phet.phscale.model.PHScaleModel;
import edu.colorado.phet.phscale.view.BeakerNode;
import edu.colorado.phet.phscale.view.MoleculeCountNode;
import edu.colorado.phet.phscale.view.ProbeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

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
    
    // Control
    
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
        
        ProbeNode probeNode = new ProbeNode( 500 );//XXX
        probeNode.setPH( 10.3 ); //XXX test
        addRootChild( probeNode );
        
        BeakerViewControlPanel beakerViewControlPanel = new BeakerViewControlPanel();
        PSwing beakerViewControlPanelWrapper = new PSwing( beakerViewControlPanel );
        addRootChild( beakerViewControlPanelWrapper );
        
        LiquidControlNode liquidControlNode = new LiquidControlNode( this );
        addRootChild( liquidControlNode );
        
        WaterControlNode waterControlNode = new WaterControlNode();
        addRootChild( waterControlNode );
        
        DrainControlNode drainControlNode = new DrainControlNode();
        addRootChild( drainControlNode );
        
        BeakerNode beakerNode = new BeakerNode( 350, 400 );//XXX
        addRootChild( beakerNode );
        
        MoleculeCountNode moleculeCountNode = new MoleculeCountNode();
        addRootChild( moleculeCountNode );

        //XXX layout, needs to be generalized
        beakerNode.setOffset( 75, 175 );//XXX
        liquidControlNode.setOffset( 25, 25 );//XXX
        waterControlNode.setOffset( 400, 25 );//XXX
        drainControlNode.setOffset( 25, 600 );//XXX
        probeNode.setOffset( 175, 75 );//XXX
        beakerViewControlPanelWrapper.setOffset( 225, 600 );//XXX
        moleculeCountNode.setOffset( 85, 275 );//XXX
        
        LogLinearControlPanel logLinearControlPanel = new LogLinearControlPanel();
        PSwing logLinearControlPanelWrapper = new PSwing( logLinearControlPanel );
        addRootChild( logLinearControlPanelWrapper );
        
        //XXX layout, needs to be generalized
        logLinearControlPanelWrapper.setOffset( 750, 600 );//XXX
    }
    

    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /* Convenience method for adding a child to the root. */
    private void addRootChild( PNode child ) {
        _rootNode.addChild( child );
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
        else if ( PHScaleConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PHScaleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
