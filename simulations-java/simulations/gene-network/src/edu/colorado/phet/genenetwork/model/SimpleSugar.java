/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;


/**
 * Class that represents LacZ, which is the model element that breaks up the
 * lactose.
 * 
 * @author John Blanco
 */
public abstract class SimpleSugar extends SimpleModelElement {
	
	public static double HEIGHT = 2;
	
	public SimpleSugar(Point2D initialPosition, Paint paint) {
		super(createShape(), initialPosition, paint);
	}
	
	private static Shape createShape(){
		// Create a hexagon shape.
		DoubleGeneralPath path = new DoubleGeneralPath();
		double length = HEIGHT / 2 / Math.sin(Math.PI/3);
		path.moveTo(-length / 2, -HEIGHT / 2);
		
		double angle = Math.PI;
		
		for (int i = 0; i < 6; i++){
			angle = lineToRelative(path, length, angle);
		}

        return AffineTransform.getTranslateInstance(-path.getGeneralPath().getBounds2D().getCenterX(), -path.getGeneralPath().getBounds2D().getCenterY()).createTransformedShape(path.getGeneralPath());
	}

	private static double lineToRelative(DoubleGeneralPath path, double length, double angle) {
		path.lineToRelative(Vector2D.Double.parseAngleAndMagnitude(length, angle));
		return angle + Math.PI / 3;
	}
}
