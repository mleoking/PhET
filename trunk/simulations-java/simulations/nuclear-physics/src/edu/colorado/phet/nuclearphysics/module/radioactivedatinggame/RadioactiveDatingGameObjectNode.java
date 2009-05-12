/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A node that represents an artifact in the view.
 * 
 * @author John Blanco
 */
public class RadioactiveDatingGameObjectNode extends PNode {
	
	private final DatableObject rdgObject;
	private final PImage image;
	
	// For debugging of placement, turns on a name so users can tell what's what.
	private final boolean SHOW_NAME = true;

	public RadioactiveDatingGameObjectNode(DatableObject rdgObject, ModelViewTransform2D mvt) {
		this.rdgObject = rdgObject;
		image = new PImage( rdgObject.getImage() );
		Point2D desiredSize = mvt.modelToViewDifferentialDouble(rdgObject.getWidth(), rdgObject.getHeight());
		image.scale(desiredSize.getX() / image.getFullBoundsReference().getWidth());
		image.setOffset(-image.getFullBoundsReference().width / 2, -image.getFullBoundsReference().height / 2);
		addChild(image);
		if (SHOW_NAME){
			PText name = new PText(rdgObject.getName());
			name.setFont(new PhetFont());
			addChild(name);
		}
	}
}
