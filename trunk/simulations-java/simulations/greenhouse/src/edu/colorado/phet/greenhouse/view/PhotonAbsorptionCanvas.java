/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas on which the neuron simulation is depicted.
 *
 * @author John Blanco
 */
public class PhotonAbsorptionCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double FLASHLIGHT_WIDTH = 300;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Model that is being viewed by this canvas.
	private PhotonAbsorptionModel photonAbsorptionModel;

    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Local root node for world children.
    PNode myWorldNode;
    
    // Layers for the canvas.
    private PNode imageLayer;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhotonAbsorptionCanvas(PhotonAbsorptionModel photonAbsorptionModel  ) {
        
        this.photonAbsorptionModel = photonAbsorptionModel;

    	// Set up the canvas-screen transform.
    	setWorldTransformStrategy(new PhetPCanvas.CenteringBoxStrategy(this, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE));
    	
    	// Set up the model-canvas transform.
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point((int)Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.5), 
        				(int)Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.5 )),
        				4,  // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        				true);

        setBackground( Color.BLACK );
        
        // Listen to the model for notifications that we care about.
        photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter() {
            
            public void photonRemoved( Photon photon ) {
            // TODO Auto-generated method stub
            
            }
            
            public void photonAdded( Photon photon ) {
                myWorldNode.addChild( new PhotonNode(photon, mvt) );
            }
        });

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild(myWorldNode);
        
        // Add the chamber.
        // TODO: Shape and location of chamber will eventually be determined by the model.
        PhetPPath chamberNode = new PhetPPath(new Rectangle2D.Double(-350, -350, 700, 700), new BasicStroke(6), Color.LIGHT_GRAY);
        chamberNode.setOffset(500, 400);
        myWorldNode.addChild(chamberNode);
        
        // Add the flashlight.
        PNode flashlightNode = new FlashlightNode(FLASHLIGHT_WIDTH, mvt, photonAbsorptionModel);
        flashlightNode.setOffset(mvt.modelToViewDouble(photonAbsorptionModel.getPhotonEmissionLocation()));
        myWorldNode.addChild(flashlightNode);
        
        // Update the layout.
        updateLayout();
        
        // Update other initial state.
        // TODO: TBD
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        Dimension2D screenSize = getScreenSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else {
        	// TODO: TBD
        }
    }
}
