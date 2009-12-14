/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
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
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------

	private static final double SIZE = 10; // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-SIZE, 0), 
			new Color(185, 147, 187), new Point2D.Double(SIZE * 5, 0), Color.WHITE);
	private static final double EXISTENCE_TIME = 15; // Seconds.
	
	// Attachment point for glucose.  Note that glucose generally only
	// attaches when it is bound up in lactose, so this is essentially the
	// lactose offset too.
	private static final Dimension2D GLUCOSE_ATTACHMENT_POINT_OFFSET = new PDimension(0, -SIZE/2);
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------

	private Glucose glucoseAttachmentPartner = null;
	private AttachmentState glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------

	public LacZ(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT, true, EXISTENCE_TIME);
		setMotionStrategy(new StillnessMotionStrategy(this));
	}
	
	public LacZ(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
	public LacZ(){
		this(null);
	}
	
	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		if (getExistenceState() == ExistenceState.EXISTING &&
			glucoseAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE){
			
			// Look for some lactose to attach to.
			glucoseAttachmentPartner = getModel().findNearestFreeLactose(getPositionRef());
			
			if (glucoseAttachmentPartner != null){
				// We found a lactose that is free, so start the process of
				// attaching to it.
				if (glucoseAttachmentPartner.considerProposalFrom(this) != true){
					assert false;  // As designed, this should always succeed, so debug if it doesn't.
				}
				else{
					glucoseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
				}
			}
		}
		else if (glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
			// See if the glucose is close enough to finalize the attachment.
			if (getGlucoseAttachmentPointLocation().distance(glucoseAttachmentPartner.getLacZAttachmentPointLocation()) < ATTACHMENT_FORMING_DISTANCE){
				// Finalize the attachment.
				glucoseAttachmentPartner.attach(this);
			}
		}
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
	protected void onTransitionToExistingState() {
		setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
	}
	
	/**
	 * Get the location in absolute space of the attachment point for this
	 * type of model element.
	 */
	public Point2D getGlucoseAttachmentPointLocation(){
		return new Point2D.Double(getPositionRef().getX() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public static Dimension2D getGlucoseAttachmentPointOffset() {
		return new PDimension(GLUCOSE_ATTACHMENT_POINT_OFFSET);
	}
}
