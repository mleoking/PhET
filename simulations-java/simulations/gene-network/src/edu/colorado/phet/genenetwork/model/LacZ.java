/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacZ, which is the model element that breaks up the
 * lactose.
 * 
 * @author John Blanco
 */
public class LacZ extends SimpleModelElement {
	
	private static double SIZE = 10; // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-SIZE, 0), 
			new Color(185, 147, 187), new Point2D.Double(SIZE * 5, 0), Color.WHITE);
	
	public LacZ(Point2D initialPosition) {
		super(createShape(), initialPosition, ELEMENT_PAINT);
		addBindingPoint(new BindingPoint(ModelElementType.GLUCOSE, new PDimension(0, -SIZE/2)));
		addBindingPoint(new BindingPoint(ModelElementType.GALACTOSE, new PDimension(0, -SIZE/2)));
		setMotionStrategy(new WeightedRandomWalkMotionStrategy(this, LacOperonModel.getModelBounds(), new Point2D.Double(0,0)));
	}
	
	public LacZ() {
		this(new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_Z;
	}

	
	private static Shape createShape(){
		// Start with a circle.
		Ellipse2D startingShape = new Ellipse2D.Double(-SIZE/2, -SIZE/2, SIZE, SIZE);
		Area area = new Area(startingShape);
		
		// Get the shape of a lactose molecule and shift it to the appropriate
		// position.
		Shape lactoseShape = new Lactose2().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, -SIZE/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		return area;
	}
}
