/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A node that represents an item that can be dated through radiometric
 * method, i.e. and item that is "datable".
 * 
 * @author John Blanco
 */
public class DatableItemNode extends PNode {
	
	private final DatableItem datableItem;
	private final ModelViewTransform2D mvt;
	private final PImage image;
	
	// For debugging of placement, turns on a name so users can tell what's what.
	private final boolean SHOW_NAME = false;

	public DatableItemNode(DatableItem datableItem, ModelViewTransform2D mvt) {
		this.datableItem = datableItem;
		this.mvt = mvt;
		image = new PImage( datableItem.getImage() );
		Point2D desiredSize = mvt.modelToViewDifferentialDouble(datableItem.getWidth(), datableItem.getHeight());
		image.scale(desiredSize.getX() / image.getFullBoundsReference().getWidth());
		addChild(image);
		if (SHOW_NAME){
			PText name = new PText(datableItem.getName());
			name.setFont(new PhetFont());
			addChild(name);
		}
		updatePosition();
	}
	
	public void updatePosition(){
		Point2D centerCanvasPosition = mvt.modelToViewDouble(datableItem.getPosition());
		image.setOffset(centerCanvasPosition.getX() - ( image.getFullBoundsReference().width / 2 ),
				centerCanvasPosition.getY() - ( image.getFullBoundsReference().height / 2 ) );
	}
}
