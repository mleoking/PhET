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
	
	private static final double SIZE = 10; // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-SIZE, 0), 
			new Color(185, 147, 187), new Point2D.Double(SIZE * 5, 0), Color.WHITE);
	private static final double EXISTENCE_TIME = 15; // Seconds.
	
	private double existenceTimeCountdown = EXISTENCE_TIME;
	
	public LacZ(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT);
		addAttachmentPoint(new AttachmentPoint(ModelElementType.GLUCOSE, new PDimension(0, -SIZE/2)));
		addAttachmentPoint(new AttachmentPoint(ModelElementType.GALACTOSE, new PDimension(0, -SIZE/2)));
		setMotionStrategy(new StillnessMotionStrategy(this));
		setExistenceState(ExistenceState.FADING_IN);
		setExistenceStrength(0.01);
	}
	
	public LacZ(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	public LacZ(){
		this(null);
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
		Shape lactoseShape = new Lactose().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, -SIZE/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		return area;
	}
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		
		switch (getExistenceState()){
		case FADING_IN:
			if (getExistenceStrength() < 1){
				setExistenceStrength(Math.min(getExistenceStrength() + FADE_RATE, 1));
			}
			else{
				// Must be fully faded in, so move to next state.
				setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
				setExistenceState(ExistenceState.EXISTING);
				existenceTimeCountdown = EXISTENCE_TIME;
			}
			break;
			
		case EXISTING:
			existenceTimeCountdown -= dt;
			if (existenceTimeCountdown <= 0){
				// Time to fade out.
				setExistenceState(ExistenceState.FADING_OUT);
			}
			break;
			
		case FADING_OUT:
			if (getExistenceStrength() > 0){
				setExistenceStrength(Math.max(getExistenceStrength() - FADE_RATE, 0));
			}
			// Note: When we get fully faded out, we will be removed from the model.
			break;
			
		default:
			assert false;
			break;
		}
	}
}
