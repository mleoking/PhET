/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.GreenhouseEffectModel;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Canvas on which the neuron simulation is depicted.
 *
 * @author John Blanco
 */
public class GreenhouseEffectCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Model that is being viewed.
	GreenhouseEffectModel model;

    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Local root node for world children.
    PNode myWorldNode;
    
    // Layers for the canvas.
    private PNode imageLayer;
    
    // Nodes on the canvas.
    private PNode background;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GreenhouseEffectCanvas( GreenhouseEffectModel model ) {
    	
    	this.model = model;

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
        
        // Register to listen to the model for events that we need to know
        // about.
        model.addListener(new GreenhouseEffectModel.Adapter(){
    		public void photonAdded(Photon photon) {
    			// A photon has come into existence, so add it to the view.
    			handlePhotonAdded(photon);
    		};
        });

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild(myWorldNode);
        
        // Add the background image.  Scale the image so that it fits the
        // height of the intermediate coordinates.
        background = new PImage(GreenhouseResources.getImage("today-2.gif"));
        background.setScale(GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.height / background.getFullBoundsReference().height);
        background.setOffset(GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width / 2 - background.getFullBoundsReference().width / 2, 0);
        myWorldNode.addChild(background);
        
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
        System.out.println("1: " + background.getWidth());
        if ( getWidth() <= 0 || getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else {
        	// Scale the background such that the screen is filled.  For this,
        	// it needs to be centered, scaled so that it fills up both the
        	// height and the width, and the bottom of the background image
        	// must be at the bottom of the play area.  In order to do this,
        	// we will need an explicit transform between intermediate coords
        	// and screen coords.
	    	AffineTransform intermediateToScreenTransform = getWorldTransformStrategy().getTransform();
	    	
	    	background.setScale(1);
	    	Rectangle2D imageBoundsIntermediateCoords = background.getFullBoundsReference();
	    	Rectangle2D imageBoundsScreenCoords = intermediateToScreenTransform.createTransformedShape(imageBoundsIntermediateCoords).getBounds2D();
	    	
	    	// Scale the image to fill the width of the screen.
	    	double scale = ((double)getWidth() / imageBoundsScreenCoords.getWidth());
	    	background.setScale(scale);
	    	
	    	// Position the image so that the bottom left corner is in the
	    	// bottom left of the view port.
	    	try {
				background.setOffset(intermediateToScreenTransform.inverseTransform(new Point2D.Double(0, getHeight() - imageBoundsScreenCoords.getHeight() * scale), null));
			} catch (NoninvertibleTransformException e) {
				System.err.println(getClass().getName() + "- Error: Uninvertible transform.");
				e.printStackTrace();
			}
        }
    }
    
    /**
     * Handle the addition of a new photon.
     * 
     * @param photon
     */
    private void handlePhotonAdded(Photon photon){
    	PhotonNode photonNode = new PhotonNode(photon, mvt);
    	addWorldChild(photonNode);
    }
}
