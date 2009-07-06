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
	private PImage image = null;
	
	// For debugging of placement, turns on a name so users can tell what's what.
	private final boolean SHOW_NAME = false;
	
	private double rotationAngle = 0;

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
				updateImage();
			}

			public void positionChanged() {
				updatePosition();
			}

			public void rotationalAngleChanged() {
				updateRotationAngle();
			}

			public void sizeChanged() {
				updateSize();
			}
		});

		updateImage();
		
		if (SHOW_NAME){
			PText name = new PText(datableItem.getName());
			name.setFont(new PhetFont());
			addChild(name);
		}
		
		// Set the initial position and orientation.
		updatePosition();
		updateRotationAngle();
	}
	
	private void updatePosition(){
		Point2D centerCanvasPosition = mvt.modelToViewDouble(datableItem.getPosition());
//		image.setOffset(centerCanvasPosition.getX() - ( image.getFullBoundsReference().width / 2 ),
//				centerCanvasPosition.getY() - ( image.getFullBoundsReference().height / 2 ) );
//		double compensatedWidth = image.getFullBoundsReference().width * Math.cos(rotationAngle);
//		double compensatedHeight = image.getFullBoundsReference().height * Math.sin(rotationAngle);
//		image.setOffset(centerCanvasPosition.getX() - ( compensatedWidth / 2 ),
//				centerCanvasPosition.getY() - ( compensatedHeight / 2 ) );
		setOffset(centerCanvasPosition);
	}
	
	private void updateSize(){
		Point2D desiredSize = mvt.modelToViewDifferentialDouble(datableItem.getWidth(), datableItem.getHeight());
		// We ignore the height here, since we can't scale in each direction
		// due to limitations of Piccolo.
		image.scale(desiredSize.getX() / image.getFullBoundsReference().getWidth());
		updatePosition();
	}
	
	private void updateRotationAngle(){
		rotateAboutPoint(datableItem.getRotationalAngle() - rotationAngle, 
				new Point2D.Double(image.getFullBoundsReference().width / 2, getFullBoundsReference().height / 2));
		rotationAngle = datableItem.getRotationalAngle();
		updatePosition();
	}
	
	private void updateImage(){
		if (image != null){
			removeChild(image);
		}
		image = new PImage( datableItem.getImage() );
		
		// Since Piccolo only has overall scaling (as opposed to scaling in
		// both the height and width dimensions), scale the image so that it
		// fits within the width and height defined by the model element.
		double itemWidth = mvt.modelToViewDifferentialXDouble(datableItem.getWidth());
		double itemHeight = mvt.modelToViewDifferentialYDouble(-datableItem.getHeight());
		double scalingFactor = Math.min(itemWidth / image.getFullBoundsReference().getWidth(), 
				itemHeight / image.getFullBoundsReference().getHeight());
		image.scale(scalingFactor);
		
		// Set the offset of the image such that it is centered around the
		// parent node's offset.
		image.setOffset(-image.getFullBoundsReference().width / 2, -image.getFullBoundsReference().height / 2);
		
		// Add the image to the node.
		addChild(image);
	}
}
