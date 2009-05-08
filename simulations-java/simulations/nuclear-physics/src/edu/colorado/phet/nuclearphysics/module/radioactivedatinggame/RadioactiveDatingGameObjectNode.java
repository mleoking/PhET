/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A node that represents an artifact in the view.
 * 
 * @author John Blanco
 */
public class RadioactiveDatingGameObjectNode extends PNode {
	
	private final RadioactiveDatingGameObject rdgObject;
	private final PImage image;

	public RadioactiveDatingGameObjectNode(RadioactiveDatingGameObject rdgObject, ModelViewTransform2D mvt) {
		this.rdgObject = rdgObject;
		image = new PImage( rdgObject.getImage() ); 
		Point2D viewLocation = mvt.modelToView( rdgObject.getCenter() );
		image.setOffset(viewLocation);
		addChild(image);
		Point2D desiredSize = mvt.modelToViewDifferentialDouble(rdgObject.getWidth(), rdgObject.getHeight());
		image.scale(desiredSize.getX() / image.getFullBoundsReference().getWidth());
	}
}
