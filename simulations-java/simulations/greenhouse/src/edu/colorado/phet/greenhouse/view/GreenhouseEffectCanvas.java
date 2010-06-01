/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.Cloud;
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
    private PNode backgroundLayer;
    private PNode photonLayer;
    private PNode cloudLayer;
    
    // Background node.
    private PNode background;
    
    // Map for relating photons in the model to their graphical
    // representations in the view.
    private final HashMap<Photon, PhotonNode> photonToGraphicMap = new HashMap<Photon, PhotonNode>();
    
    // Map for relating clouds in the model to their graphical
    // representations in the view.
    private final HashMap<Cloud, CloudNode> cloudToGraphicMap = new HashMap<Cloud, CloudNode>();
    
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
    		public void photonRemoved(Photon photon) {
    			// A photon has been removed.
    			handlePhotonRemoved(photon);
    		};
    		public void cloudAdded(Cloud cloud) {
    			// A cloud has come into existence, so add it to the view.
    			handleCloudAdded(cloud);
    		};
    		public void cloudRemoved(Cloud cloud) {
    			// A cloud has been removed.
    			handleCloudRemoved(cloud);
    		};
        });

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild(myWorldNode);
        
        // Create and add the layers that will be used.  The order in which
        // these are added effectively defines the layering.
        backgroundLayer = new PNode();
        myWorldNode.addChild(backgroundLayer);
        photonLayer = new PNode();
        myWorldNode.addChild(photonLayer);
        cloudLayer = new PNode();
        myWorldNode.addChild(cloudLayer);
        
        // Add the background image.  Scale the image so that it fits the
        // height of the intermediate coordinates.
        background = new PImage(GreenhouseResources.getImage("today-2.gif"));
        background.setScale(GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.height / background.getFullBoundsReference().height);
        background.setOffset(GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width / 2 - background.getFullBoundsReference().width / 2, 0);
        backgroundLayer.addChild(background);
        
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
    
    private void handlePhotonAdded(Photon photon){
    	PhotonNode photonNode = new PhotonNode(photon, mvt);
    	photonLayer.addChild(photonNode);
    	photonToGraphicMap.put(photon, photonNode);
    }
    
    private void handlePhotonRemoved(Photon photon){
    	PhotonNode photonNode = photonToGraphicMap.get(photon);
    	if (!(photonNode != null && photonLayer.removeChild(photonNode) != null)){
   			System.out.println(getClass().getName() + " - Error: Unable to locate graphical representation of photon.");
   			assert false;
   			return;
    	}
    	photonToGraphicMap.remove(photon);
    }

    private void handleCloudAdded(Cloud cloud){
    	// Used fixed seeds in order to create consistently good-looking
    	// clouds.  The seed values were determined empirically.
    	long seed;
    	switch (cloudToGraphicMap.size()){
    	case 0:
    		seed = 11223354;
    		break;
    	case 1:
    		seed = 25;
    		break;
    	case 2:
    		seed = 22;
    		break;
		default:
			System.out.println(getClass().getName() + " - Waring: Unexpected number of clouds in existence.");
			assert false;
			seed = 12345;
    	}
    	CloudNode cloudNode = new CloudNode(cloud, mvt, seed);
    	cloudLayer.addChild(cloudNode);
    	cloudToGraphicMap.put(cloud, cloudNode);
    }
    
    private void handleCloudRemoved(Cloud cloud){
    	CloudNode cloudNode = cloudToGraphicMap.get(cloud);
    	if (!(cloudNode != null && cloudLayer.removeChild(cloudNode) != null)){
   			System.out.println(getClass().getName() + " - Error: Unable to locate graphical representation of cloud.");
   			assert false;
   			return;
    	}
    	cloudToGraphicMap.remove(cloud);
    }
}
