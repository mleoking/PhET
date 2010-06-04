/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PNode that represents a flashlight in the view.  The flashlight is oriented
 * such that the point of origin and the expected direction line up with the
 * image.
 * 
 * @author John Blanco
 */
public class FlashlightNode extends PNode {

	private PImage flashlightImage;
	private ModelViewTransform2D mvt;
	
	public FlashlightNode(ModelViewTransform2D mvt) {
		
		this.mvt = mvt;
		
		flashlightImage = new PImage(GreenhouseResources.getImage("flashlight.png"));
		addChild(flashlightImage);
	}
}
