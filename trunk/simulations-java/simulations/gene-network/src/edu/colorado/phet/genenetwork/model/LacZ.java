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
	private static final double EXISTENCE_TIME = 25; // Seconds.
	
	// Attachment point for glucose.  Note that glucose generally only
	// attaches when it is bound up in lactose, so this is essentially the
	// lactose offset too.
	private static final Dimension2D GLUCOSE_ATTACHMENT_POINT_OFFSET = new PDimension(0, -SIZE/2);
	
	// Amount of time that lactose is attached before it is "digested",
	// meaning that it is broken apart and released.
	private static final double LACTOSE_ATTACHMENT_TIME = 1;  // In seconds.
	
	// Amount of time after releasing one lactose molecule until it is okay
	// to start trying to attach to another.  This is needed to prevent the
	// LacZ from getting into a state where it can never fade out.
	private static final double RECOVERY_TIME = 0.250;  // In seconds.
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------

	private Glucose glucoseAttachmentPartner = null;
	private AttachmentState glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private double lactoseAttachmentCountdownTimer;
	private double recoverCountdownTimer;
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------

	public LacZ(IGeneNetworkModelControl model, Point2D initialPosition, boolean fadeIn) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT, fadeIn, EXISTENCE_TIME);
		setMotionStrategy(new StillnessMotionStrategy());
	}
	
	public LacZ(IGeneNetworkModelControl model, boolean fadeIn) {
		this(model, new Point2D.Double(), fadeIn);
	}
	
	public LacZ(){
		this(null, false);
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
			glucoseAttachmentPartner = getModel().getNearestFreeLactose(getPositionRef());
			
			if (glucoseAttachmentPartner != null){
				// We found a lactose that is free, so start the process of
				// attaching to it.
				if (glucoseAttachmentPartner.considerProposalFrom(this) != true){
					assert false;  // As designed, this should always succeed, so debug if it doesn't.
				}
				else{
					glucoseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
					
					// Prevent fadeout from occurring while attached to lactose.
					setOkayToFade(false);
					
					// Start the attachment timer/counter.
					lactoseAttachmentCountdownTimer = LACTOSE_ATTACHMENT_TIME;
					
					// Move towards the partner.
					Dimension2D offsetFromTarget = new PDimension(
							Glucose.getLacZAttachmentPointOffset().getWidth() - getGlucoseAttachmentPointOffset().getWidth(),
							Glucose.getLacZAttachmentPointOffset().getHeight() - getGlucoseAttachmentPointOffset().getHeight());
					setMotionStrategy(new CloseOnMovingTargetMotionStrategy(glucoseAttachmentPartner, offsetFromTarget,
							LacOperonModel.getMotionBounds()));
				}
			}
		}
		else if (glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
			// See if the glucose is close enough to finalize the attachment.
			if (getGlucoseAttachmentPointLocation().distance(glucoseAttachmentPartner.getLacZAttachmentPointLocation()) < ATTACHMENT_FORMING_DISTANCE){
				// Finalize the attachment.
				glucoseAttachmentPartner.attach(this);
				glucoseAttachmentState = AttachmentState.ATTACHED;
				setMotionStrategy(new RandomWalkMotionStrategy(LacOperonModel.getMotionBounds()));
			}
		}
		else if (glucoseAttachmentState == AttachmentState.ATTACHED){
			lactoseAttachmentCountdownTimer -= dt;
			if (lactoseAttachmentCountdownTimer <= 0){
				// Time to break down and release the lactose.
				glucoseAttachmentPartner.releaseGalactose();
				glucoseAttachmentPartner.detach(this);
				glucoseAttachmentPartner = null;
				glucoseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
				setOkayToFade(true);
				recoverCountdownTimer = RECOVERY_TIME;
			}
		}
		else if (glucoseAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE){
			recoverCountdownTimer -= dt;
			if (recoverCountdownTimer <= 0){
				// Recovery is complete.
				glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			}
		}
	}
	
	private static Shape createShape(){
		// Start with a circle.
		Ellipse2D startingShape = new Ellipse2D.Double(-SIZE/2, -SIZE/2, SIZE, SIZE);
		Area area = new Area(startingShape);
		
		// Get the shape of a lactose molecule and shift it to the appropriate
		// position.
		Shape lactoseShape = Lactose.getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, -SIZE/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		return area;
	}

	@Override
	protected void onTransitionToExistingState() {
		setMotionStrategy(new RandomWalkMotionStrategy(LacOperonModel.getMotionBoundsAboveDna()));
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
