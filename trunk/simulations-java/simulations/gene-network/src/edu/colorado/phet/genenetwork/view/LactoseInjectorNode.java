/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.genenetwork.GeneNetworkResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Node that represents the thing that the user interacts with in order to
 * inject lactose into the system.
 * 
 * @author John Blanco
 */
public class LactoseInjectorNode extends PNode {

	//------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Height of the injector prior to rotation.  This is in world size, which
	// is close to pixels (but not quite exactly due to all that transform
	// craziness).
	private static final double INJECTOR_HEIGHT = 30; 

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	PNode bodyImage;
	PNode unpressedButtonImage;
	PNode pressedButtonImage;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public LactoseInjectorNode() {
        // Load the graphic images for this device.
        BufferedImage bufferedImage = GeneNetworkResources.getImage( "squeezer_background.png" );
        bodyImage = new PImage( bufferedImage );
        bodyImage.scale(0.5);
        bodyImage.rotate(Math.PI / 4);
        addChild(bodyImage);
	}
}
