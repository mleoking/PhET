// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
	
	private static double EXISTENCE_TIME = 0.01; // In seconds.
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	// Angle at which this arrow will point.  This is in radians, and a value
	// of 0 means that it points straight up.
	private final double pointingAngle;
	
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
	
	public TransformationArrow(IGeneNetworkModelControl model, Point2D initialPosition, double length, boolean fadeIn, double pointingAngle) {
		super(model, createShape(length, pointingAngle), initialPosition, ELEMENT_PAINT, fadeIn, EXISTENCE_TIME);
		this.pointingAngle = pointingAngle;
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	private static Shape createShape(double length, double angle){

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
		
		// Rotate the arrow to the specified angle.
		AffineTransform rotationTransform = AffineTransform.getRotateInstance(angle - Math.PI/2);
		
		return rotationTransform.createTransformedShape(area);
	}
	
	protected double getPointingAngle() {
		return pointingAngle;
	}
	
	protected double getHeadHeight(){
		return HEAD_HEIGHT;
	}
}
