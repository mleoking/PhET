/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This is a base class for model elements that exist inside a cell or in
 * extracellular space, and that are not composed of any other model elements.
 * They are, in a sense, the atomic elements of this model.
 * 
 * @author John Blanco
 */
public abstract class SimpleModelElement {

	private Shape shape;
	private Point2D position;
	private Paint paint;  // The paint to use when representing this element in the view.
	private Vector2D.Double velocity;
	
	public SimpleModelElement(Shape initialShape, Point2D initialPosition, Paint paint){
		this.shape = initialShape;
		this.position = initialPosition;
		this.paint = paint;
	}
	
	public Shape getShape(){
		return shape;
	}
	
	protected void setShape(Shape shape){
		this.shape = shape;  
	}
	
	public Point2D getPosition(){
		return position;
	}
	
	public void setPosition(double xPos, double yPos ){
		position.setLocation(xPos, yPos);
	}
	
	public Paint getPaint(){
		return paint;
	}
	
	public void setVelocity(double xVel, double yVel){
		velocity.setComponents(xVel, yVel);
	}
	
	public Vector2D getVelocityRef(){
		return velocity;
	}
}
