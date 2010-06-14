/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;


import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PNode that represents a flashlight in the view.  This node is set up such
 * that setting its offset based on the photon emission point in the model
 * should position it correctly.  This makes some assumptions about the
 * direction of photon emission.
 * 
 * @author John Blanco
 */
public class FlashlightNode extends PNode {

	private PImage flashlightImage;
	private ModelViewTransform2D mvt;
	
	/**
	 * Constructor.
	 * 
	 * @param flashlightWidth - Width of the flashlight in screen coords.  The
	 * height will be based on the aspect ratio of the image.  The size of
	 * the control box is independent of this - its size is based on the
	 * strings that define the user selections.
	 * @param mvt - Model-view transform for translating between model and
	 * view coordinate systems.
	 */
	public FlashlightNode(double flashlightWidth, ModelViewTransform2D mvt) {
		
		this.mvt = mvt;
		
		flashlightImage = new PImage(GreenhouseResources.getImage("flashlight.png"));
		flashlightImage.scale(flashlightWidth / flashlightImage.getFullBoundsReference().width);
		// Set the offset such that the center right side of the image is the
		// origin.  This assumes that photons will be emitted to the right.
		flashlightImage.setOffset(-flashlightWidth, -flashlightImage.getFullBoundsReference().height / 2);
		addChild(flashlightImage);
	}
}
