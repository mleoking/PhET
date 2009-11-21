/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;


/**
 * Class that represents a "transformation arrow", which is an arrow that
 * exists in the view, generally for a short time, and that depicts the
 * transformation of one type of model element into another.
 * 
 * @author John Blanco
 */
public class TransformationArrow extends SimpleModelElement {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Constants that control size and appearance.
	private static final Paint ELEMENT_PAINT = Color.BLACK;
	private static double WIDTH = 0.5;          // In nanometers.
	private static double HEAD_WIDTH = 2;       // In nanometers.
	private static double HEAD_HEIGHT = 2;      // In nanometers.
	private static double DEFAULT_LENGTH = 7;   // In nanometers.
	
	private static double EXISTENCE_TIME = 0.1; // In seconds.
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
	
	public TransformationArrow(IObtainGeneModelElements model, Point2D initialPosition, double length) {
		
		super(model, createShape(length), initialPosition, ELEMENT_PAINT, true, EXISTENCE_TIME);
		
		setMotionStrategy(new StillnessMotionStrategy(this));
	}
	
	public TransformationArrow(IObtainGeneModelElements model, Point2D initialPosition) {
		this(model, initialPosition, DEFAULT_LENGTH);
	}

	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	private static Shape createShape(double length){

		// Create the overall outline.
		DoubleGeneralPath outline = new DoubleGeneralPath();
		
		outline.moveTo(-WIDTH / 2, -length/2);
		outline.lineTo(WIDTH/2, -length/2);
		outline.lineTo(WIDTH/2, length/2 - HEAD_HEIGHT);
		outline.lineTo(HEAD_WIDTH / 2, length/2 - HEAD_HEIGHT);
		outline.lineTo(0, length/2);
		outline.lineTo(-HEAD_WIDTH / 2, length/2 - HEAD_HEIGHT);
		outline.lineTo(-WIDTH/2, length/2 - HEAD_HEIGHT);
		outline.closePath();
		
		Area area = new Area(outline.getGeneralPath());
		
		// Subtract off two chunks so that the arrow looks dashed.
		double spaceSize = (length - HEAD_HEIGHT) / 5;
		Rectangle2D space1 = new Rectangle2D.Double(-WIDTH/2, -length/2 + spaceSize, WIDTH, spaceSize);
		area.subtract(new Area(space1));
		Rectangle2D space2 = new Rectangle2D.Double(-WIDTH/2, -length/2 + spaceSize * 3, WIDTH, spaceSize);
		area.subtract(new Area(space2));
		
		return area;
	}
	
	@Override
	public ModelElementType getType() {
		// TODO Here until the whole type thing is permanently removed.
		return null;
	}
}
