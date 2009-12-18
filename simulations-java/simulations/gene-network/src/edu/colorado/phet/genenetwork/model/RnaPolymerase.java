/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;



/**
 * Class that represents RNA Polymerase, which is the stuff that transcribes
 * DNA, in the model.
 * 
 * @author John Blanco
 */
public class RnaPolymerase extends SimpleModelElement {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static float WIDTH = CapBindingRegion.WIDTH + LacPromoter.WIDTH;
	private static float HEIGHT = 8;  // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-WIDTH, 0), 
			new Color(17, 149, 210), new Point2D.Double(WIDTH * 5, 0), Color.WHITE);
	private static Dimension2D LAC_PROMOTER_ATTACHMENT_POINT_OFFSET = new PDimension(WIDTH * 0.15, -HEIGHT * 0.3);
	private static Dimension2D MESSENGER_RNA_OUTPUT_OFFSET = new PDimension(-WIDTH * 0.2, -HEIGHT * 0.05);
	private static double RECOVERY_TIME = 7;                  // Seconds.
	private static double MAX_TRAVERSAL_TIME = 10; // In seconds.
	private static double TRAVERSAL_SPEED = 5; // In nanometers/second.
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private LacPromoter lacPromoterAttachmentPartner = null;
	private AttachmentState lacPromoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private Point2D targetPositionForLacPromoterAttachment = new Point2D.Double();
	private double recoveryCountdownTimer;
	private boolean traversing = false;
	private boolean transcribing = false;
	private Point2D traversalStartPt = new Point2D.Double();
	private MessengerRna mRna = null;
	private boolean bumpingLacI;
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	public RnaPolymerase(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
		
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBoundsAboveDna()));
	}
	
	public RnaPolymerase(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
	public RnaPolymerase(){
		this(null);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	public void stepInTime(double dt) {
		if (lacPromoterAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE){
			if (traversing){
				if (!transcribing){
					if (isOnLacZGene()){
						// We have moved into contact with the LacZ gene, so
						// it is time to start transcribing.
						mRna = new LacZMessengerRna(getModel(), 0);
						mRna.setPosition(getPositionRef().getX() + MESSENGER_RNA_OUTPUT_OFFSET.getWidth(), 
								getPositionRef().getY() + MESSENGER_RNA_OUTPUT_OFFSET.getHeight());
						getModel().addMessengerRna(mRna);					
						transcribing = true;
					}
				}
				else{
					// We are in the process of transcribing the DNA.
					// Continue growing the messenger RNA until we run off the
					// end of the gene.
					mRna.grow(getVelocityRef().getMagnitude() * dt);
					if (!isOnLacZGene()){
						// We have traversed the gene.  Time to detach the
						// mRNA as well as ourself.
						mRna.setMotionStrategy(new LinearMotionStrategy(mRna, LacOperonModel.getMotionBounds(),
								new Point2D.Double(mRna.getPositionRef().getX(), mRna.getPositionRef().getY() + 30), 4));
						mRna = null;
						transcribing = false;
						traversing = false;
						setMotionStrategy(new DetachFromDnaThenRandomMotionWalkStrategy(this, LacOperonModel.getMotionBoundsAboveDna()));
						recoveryCountdownTimer = RECOVERY_TIME;
					}
				}
			}
			else{
				recoveryCountdownTimer -= dt;
				if (recoveryCountdownTimer <= 0){
					// This has been unattached long enough and is ready to attach
					// again.
					lacPromoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
				}
			}
		}
		else if (lacPromoterAttachmentState == AttachmentState.ATTACHED){
			if (bumpingLacI){
				// We are in the process of simulating a "bump" motion.  See
				// if we are done.
				assert getMotionStrategyRef() instanceof ThereAndBackMotionStrategy;
				ThereAndBackMotionStrategy strategy = (ThereAndBackMotionStrategy)getMotionStrategyRef();
				if (strategy.isTripCompleted()){
					// The simulated bump is complete - go back to being still.
					setMotionStrategy(new StillnessMotionStrategy(this));
					bumpingLacI = false;
				}
			}
		}
		super.stepInTime(dt);
	}
	
	@Override
	public void setDragging(boolean dragging) {
		if (dragging = true && lacPromoterAttachmentPartner != null){
			// The user has grabbed this node and is moving it, so release
			// any hold on the lac promoter.
			lacPromoterAttachmentPartner.detach(this);
			lacPromoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			lacPromoterAttachmentPartner = null;
			setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBoundsAboveDna()));
			bumpingLacI = false;
		}
		super.setDragging(dragging);
	}

	public Point2D getAttachmentPointLocation(LacPromoter lacPromoter){
		return new Point2D.Double(getPositionRef().getX() + LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public void attach(LacPromoter lacPromoter){
		if (lacPromoter != lacPromoterAttachmentPartner){
			System.err.println(getClass().getName() + " - Error: Attachment request from non-partner.");
			assert false;
			return;
		}
		setMotionStrategy(new StillnessMotionStrategy(this));
		setPosition(targetPositionForLacPromoterAttachment);
		lacPromoterAttachmentState = AttachmentState.ATTACHED;
	}
	
	public void detach(LacPromoter lacPromoter){
		// Error checking.
		if (lacPromoter != lacPromoterAttachmentPartner){
			System.err.println(getClass().getName() + " - Error: Attachment request from non-partner.");
			assert false;
			return;
		}
		
		if (lacPromoterAttachmentState == AttachmentState.ATTACHED){
			if (getModel().isLacZGenePresent() && !getModel().isLacIAttachedToDna()){
				// The way is clear for traversing.
				traversing = true;
				traversalStartPt.setLocation(getPositionRef());
				setMotionStrategy(new LinearMotionStrategy(this, LacOperonModel.getMotionBounds(), 
						new Vector2D.Double(TRAVERSAL_SPEED, 0), MAX_TRAVERSAL_TIME));
			}
			else{
				// Can't traverse, so just detach.
				setMotionStrategy(new DetachFromDnaThenRandomMotionWalkStrategy(this, 
						LacOperonModel.getMotionBoundsAboveDna()));
				recoveryCountdownTimer = RECOVERY_TIME;
			}
			lacPromoterAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		}
		else if (lacPromoterAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
			// We are being asked to terminate the engagement, which can
			// happen in cases such as when our potential partner gets removed
			// from the model.
			lacPromoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBoundsAboveDna()));
		}
		lacPromoterAttachmentPartner = null;
		// Make sure the flag that indicates that LacI is being bumped is not
		// set.
		bumpingLacI = false;
	}
	
	/**
	 * Simulate a motion that is meant to look like this polymerase molecule
	 * bumps against the LacI that is on the DNA strand and is thus blocked
	 * from proceeding, and so returns to its original position.
	 */
	public void doLacIBump(){
		
		// This is set up to moved a fixed, hard-coded distance and then
		// return.  At some point, it may be desirable to make this more
		// "real", in the sense that it would detect when it comes in contact
		// with the LacI and turn around at that point.  For now (Dec 17 2009),
		// this is the quickest and easiest way to get the desired behavior.
		double distanceToTravel = 1; // In nanometers.
		Point2D turnAroundPoint = new Point2D.Double(getPositionRef().getX() + distanceToTravel, getPositionRef().getY());
		setMotionStrategy(new ThereAndBackMotionStrategy(this, turnAroundPoint, LacOperonModel.getMotionBounds(), 5));
		bumpingLacI = true;
	}
	
	private static Shape createActiveConformationShape(){
		
		// Create the overall outline.
		GeneralPath basicShape = new GeneralPath();
		
		basicShape.moveTo(WIDTH / 2, -HEIGHT/2);
		basicShape.lineTo(0, -HEIGHT/2);
		basicShape.lineTo(-WIDTH * 0.4f, 0);
		basicShape.curveTo(0, HEIGHT * 0.6f, WIDTH/4, HEIGHT * 0.4f, WIDTH / 2, HEIGHT/4);
		basicShape.closePath();
		Area area = new Area(basicShape);
		
		// Get the shape of the promoter and shift it to the appropriate
		// position.
		Shape promoterShape = new LacPromoter(null).getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	basicShape.getBounds2D().getMaxX() - promoterShape.getBounds().getMaxX(), -HEIGHT/2 );
		promoterShape = transform.createTransformedShape(promoterShape);
		
		// Subtract off the shape of the binding region.
		area.subtract(new Area(promoterShape));
		
		// Get the shape of the CAP and shift it to the appropriate location.
		Shape capShape = new Cap(null).getShape();
		transform = new AffineTransform();
		transform.setToTranslation(	promoterShape.getBounds2D().getMinX() - capShape.getBounds2D().getWidth()/2, -2 );
		capShape = transform.createTransformedShape(capShape);
		
		// Subtract off the shape of the camp.
		area.subtract(new Area(capShape));
		
		return area;
	}
	
	public boolean considerProposalFrom(LacPromoter lacPromoter) {
		boolean proposalAccepted = false;
		
		if (lacPromoterAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE){
			assert lacPromoterAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacPromoterAttachmentPartner = lacPromoter;
			proposalAccepted = true;
			lacPromoterAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			
			// Set ourself up to move toward the attaching location.
			double xDest = lacPromoterAttachmentPartner.getAttachmentPointLocation(this).getX() - 
				LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getWidth();
			double yDest = lacPromoterAttachmentPartner.getAttachmentPointLocation(this).getY() -
				LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getHeight();
			setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
			getMotionStrategyRef().setDestination(xDest, yDest);
			targetPositionForLacPromoterAttachment.setLocation(xDest, yDest);
		}
		
		return proposalAccepted;
	}
	
	/**
	 * Return true if we are on the lacI gene.  This is determined by looking
	 * at whether we are directly above it and less than some min distance
	 * away.
	 * @return
	 */
	private boolean isOnLacZGene(){
		LacZGene lacZGene = getModel().getLacZGene();
		if (lacZGene == null){
			return false;
		}
		Rectangle2D lacZGeneBounds = lacZGene.getShape().getBounds2D();
		
		// Create an "extended bounds" region where if our position in within
		// those bounds, we consider ourself to be in contact with the gene.
		// This region is the lac Z region extended upward by some amount.
		Rectangle2D extendedBounds = new Rectangle2D.Double(
				lacZGene.getPositionRef().getX() + lacZGeneBounds.getMinX(), 
				lacZGene.getPositionRef().getY() + lacZGeneBounds.getMinY(),
				lacZGeneBounds.getWidth(),
				lacZGeneBounds.getHeight() * 2);
		
		return extendedBounds.contains(getPositionRef());
	}
}
