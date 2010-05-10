/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Canvas on which the neuron simulation is depicted.
 *
 * @author John Blanco
 */
public class PhotonAbsorptionCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Local root node for world children.
    PNode myWorldNode;
    
    // Layers for the canvas.
    private PNode imageLayer;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhotonAbsorptionCanvas(  ) {

    	// Set up the canvas-screen transform.
    	setWorldTransformStrategy(new PhetPCanvas.CenteringBoxStrategy(this, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE));
    	
    	// Set up the model-canvas transform.
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point((int)Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.5), 
        				(int)Math.round(NeuronDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.5 )),
        				4,  // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        				true);

        setBackground( new Color(153, 204, 255) );

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild(myWorldNode);
        
        // TODO: This is temporary.  Add a node that is a sketch of what this
        // will really look like at some point.
        PImage sketchNode = new PImage(GreenhouseResources.getImage("photon-absorption-sketch-01.png"));
        sketchNode.setScale(1300 / sketchNode.getFullBoundsReference().width);
        sketchNode.setOffset(-200, 0);
        myWorldNode.addChild(sketchNode);
        
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
