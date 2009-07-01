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
 * method, i.e. and item that is "datable".  Note that this items can move
 * around, rotate, and change their appearance in other ways, so this node
 * must be able to handle such occurrences.
 * 
 * @author John Blanco
 */
public class DatableItemNode extends PNode {
	
	private final DatableItem datableItem;
	private final ModelViewTransform2D mvt;
	private final PImage image;
	
	// For debugging of placement, turns on a name so users can tell what's what.
	private final boolean SHOW_NAME = false;

	/**
	 * Constructor
	 * 
	 * @param datableItem
	 * @param mvt
	 */
	public DatableItemNode(DatableItem datableItem, ModelViewTransform2D mvt) {
		
		this.datableItem = datableItem;
		this.mvt = mvt;
		
		// Register with the datable items for changes in its position, image, etc.
		datableItem.addAnimationListener(new ModelAnimationListener(){

			public void imageChanged() {
				// TODO Auto-generated method stub
			}

			public void positionChanged() {
				// TODO Auto-generated method stub
			}

			public void rotationalAngleChanged() {
				// TODO Auto-generated method stub
			}

			public void sizeChanged() {
				handleSizeChanged();
			}
		});
		
		// Load up the initial image.
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
	
	public void handleSizeChanged(){
		Point2D desiredSize = mvt.modelToViewDifferentialDouble(datableItem.getWidth(), datableItem.getHeight());
		// We ignore the height here, since we can't scale in each direction
		// due to limitations of Piccolo.
		image.scale(desiredSize.getX() / image.getFullBoundsReference().getWidth());
		updatePosition();
	}
}
