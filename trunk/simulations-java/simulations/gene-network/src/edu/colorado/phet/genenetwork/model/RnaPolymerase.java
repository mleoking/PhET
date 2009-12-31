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
import java.util.Random;

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
			new Color(0, 153, 210), new Point2D.Double(WIDTH * 5, 0), Color.WHITE);
	private static Dimension2D LAC_PROMOTER_ATTACHMENT_POINT_OFFSET = new PDimension(WIDTH * 0.15, -HEIGHT * 0.3);
	private static Dimension2D MESSENGER_RNA_OUTPUT_OFFSET = new PDimension(-WIDTH * 0.2, -HEIGHT * 0.05);
	private static double RECOVERY_TIME = 7;                  // Seconds.
	private static double MAX_TRAVERSAL_TIME = 10; // In seconds.
	private static double TRAVERSAL_SPEED = 5; // In nanometers/second.
	private static Random RAND = new Random();
	private static final int MAX_REATTACH_ATTEMPTS = 6;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private Promoter promoterAttachmentPartner = null;
	private Promoter previousPromoterAttachmentPartner = null;
	private AttachmentState promoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private Point2D targetPositionForLacPromoterAttachment = new Point2D.Double();
	private double recoveryCountdownTimer;
	private boolean traversing = false;
	private boolean transcribing = false;
	private Point2D traversalStartPt = new Point2D.Double();
	private MessengerRna mRna = null;
	private boolean clearedToCrossLacOperator;
	private int reattachCount;
	
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
		if (!isUserControlled()){
			if (promoterAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE){
				if (traversing){
					// This polymerase is currently traversing the DNA strand.
					DnaStrand dnaStrand = getModel().getDnaStrand();
					if (transcribing){
						// This polymerase is currently transcribing a portion
						// of the DNA strand. Continue growing the messenger
						// RNA until we run off the end of the gene.
						mRna.grow(getVelocityRef().getMagnitude() * dt);
						if (!dnaStrand.isOnLacIGeneSpace(getPositionRef()) && !dnaStrand.isOnLacZGeneSpace(getPositionRef())){
							// We have fully traversed the gene.  Time to
							// detach the mRNA from us and ourself from the
							// DNA.
							freeMessengerRna(true);
							detachFromDna(2);
						}
						else if ((dnaStrand.isOnLacIGeneSpace(getPositionRef()) && !dnaStrand.isLacIGeneInPlace()) ||
								 (dnaStrand.isOnLacZGeneSpace(getPositionRef()) && !dnaStrand.isLacZGeneInPlace())){
							// This polymerase was traversing a gene but the
							// gene is no longer there, most likely because it
							// was removed by the user.  This is a rare
							// situation, but handling for it was explicitly
							// requested.  Detach the mRNA, but mark it as
							// incomplete, and then detach ourself from the
							// DNA strand.
							freeMessengerRna(false);
							detachFromDna(0);
						}
					}
					else{
						// This polymerase is traversing the DNA but not yet
						// transcribing it.
						
						Point2D myNose = new Point2D.Double( getPositionRef().getX() + getShape().getBounds2D().getMaxX(),
								getPositionRef().getY());
						if (!clearedToCrossLacOperator && dnaStrand.isOnLacOperatorSpace(myNose)){
							// We are starting to cross the part of the DNA
							// where lac I could be attached.  Determine if it
							// is attached, which would block our way.
							if (getModel().isLacIAttachedToDna()){
								// Our path is blocked.  Decide whether to try
								// to reattach or to float away.
								if (RAND.nextDouble() > (double)reattachCount / (double)MAX_REATTACH_ATTEMPTS){
									promoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
									if (!previousPromoterAttachmentPartner.requestReattach(this)){
										// Can't reattach, so wander off.
										detachFromDna(0);
										reattachCount = 0;
									}
									else{
										reattachCount++;
									}
								}
								else{
									// Don't even try to reattach - just wander off.
									detachFromDna(0);
									reattachCount = 0;
								}
							}
							else{
								// No lac I is attached, so we are clear to
								// cross this part of the DNA strand.
								clearedToCrossLacOperator = true;
								reattachCount = 0;
							}
						}
						else if (dnaStrand.isOnLacZGeneSpace(getPositionRef())){
							if (dnaStrand.isLacZGeneInPlace()){
								// We have moved into contact with the LacZ gene, so
								// it is time to start transcribing.
								mRna = new LacZMessengerRna(getModel(), 0);
								mRna.setPosition(getPositionRef().getX() + MESSENGER_RNA_OUTPUT_OFFSET.getWidth(), 
										getPositionRef().getY() + MESSENGER_RNA_OUTPUT_OFFSET.getHeight());
								getModel().addMessengerRna(mRna);					
								transcribing = true;
							}
							else{
								// We are over the lac Z gene location, but
								// the gene isn't there, so float away.
								detachFromDna(0);
							}
						}
						else if (dnaStrand.isOnLacIGeneSpace(getPositionRef())){
							if (dnaStrand.isLacIGeneInPlace()){
								// We have moved into contact with the LacI gene, so
								// it is time to start transcribing.
								mRna = new LacIMessengerRna(getModel(), 0);
								mRna.setPosition(getPositionRef().getX() + MESSENGER_RNA_OUTPUT_OFFSET.getWidth(), 
										getPositionRef().getY() + MESSENGER_RNA_OUTPUT_OFFSET.getHeight());
								getModel().addMessengerRna(mRna);					
								transcribing = true;
							}
							else{
								// We are over the lac I gene location, but
								// the gene isn't there, so float away.
								detachFromDna(0);
							}
						}
					}
				}
				else{
					recoveryCountdownTimer -= dt;
					if (recoveryCountdownTimer <= 0){
						// This has been unattached long enough and is ready to attach
						// again.
						promoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
					}
				}
			}
		}
		super.stepInTime(dt);
	}

	/**
	 * Free the messenger RNA that has been transcribed.
	 */
	private void freeMessengerRna(boolean fullyFormed) {
		mRna.setFullyFormed(fullyFormed);
		mRna.setMotionStrategy(new LinearMotionStrategy(mRna, LacOperonModel.getMotionBounds(),
				new Vector2D.Double(0, 4), 20));
		mRna = null;
	}
	
	@Override
	public void setDragging(boolean dragging) {
		if (dragging == true){
			// The user has grabbed this node.  See if we need to do anything
			// special in response.
			if (promoterAttachmentPartner != null){
				// This polymerase was either attached to a promoter or moving
				// towards attachment.  This relationship must be terminated.
				promoterAttachmentPartner.detach(this);
				promoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
				promoterAttachmentPartner = null;
				setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBoundsAboveDna()));
			}
			if (traversing){
				// This polymerase was traversing the DNA strand, so the
				// motion strategy must be changed.
				setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBoundsAboveDna()));
				if (transcribing){
					// This polymerase was transcribing the DNA into mRNA, so
					// the mRNA needs to be detached and should be marked as
					// not being fully formed.
					freeMessengerRna(false);
				}
				traversing = false;
				transcribing = false;
				promoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			}
		}
		super.setDragging(dragging);
	}

	public Point2D getLacPromoterAttachmentPointLocation(){
		return new Point2D.Double(getPositionRef().getX() + LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public void attach(Promoter promoter){
		if (promoter != promoterAttachmentPartner){
			System.err.println(getClass().getName() + " - Error: Attachment request from non-partner.");
			assert false;
			return;
		}
		setMotionStrategy(new StillnessMotionStrategy(this));
		setPosition(targetPositionForLacPromoterAttachment);
		promoterAttachmentState = AttachmentState.ATTACHED;
		clearedToCrossLacOperator = false;
	}
	
	public void detach(Promoter promoter){
		// Error checking.
		if (promoter != promoterAttachmentPartner){
			System.err.println(getClass().getName() + " - Error: Attachment request from non-partner.");
			assert false;
			return;
		}
		
		if (promoterAttachmentState == AttachmentState.ATTACHED){
			// We were attached, so now start traversing.
			traversing = true;
			traversalStartPt.setLocation(getPositionRef());
			setMotionStrategy(new LinearMotionStrategy(this, LacOperonModel.getMotionBounds(), 
					new Vector2D.Double(TRAVERSAL_SPEED, 0), MAX_TRAVERSAL_TIME));
			promoterAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		}
		else if (promoterAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
			// We are being asked to terminate the engagement, which can
			// happen in cases such as when our potential partner gets removed
			// from the model.
			promoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBoundsAboveDna()));
		}
		previousPromoterAttachmentPartner = promoterAttachmentPartner;
		promoterAttachmentPartner = null;
	}
	
	private void detachFromDna(double delay){
		transcribing = false;
		traversing = false;
		setMotionStrategy(new DetachFromDnaThenRandomMotionWalkStrategy(this, 
				LacOperonModel.getMotionBoundsAboveDna(), delay));
		recoveryCountdownTimer = RECOVERY_TIME;
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
	
	public boolean considerProposalFrom(Promoter promoter) {
		boolean proposalAccepted = false;
		
		if (promoterAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE){
			assert promoterAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			promoterAttachmentPartner = promoter;
			proposalAccepted = true;
			promoterAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			
			// Set ourself up to move toward the attaching location.
			double xDest = promoterAttachmentPartner.getAttachmentPointLocation(this).getX() - 
				LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getWidth();
			double yDest = promoterAttachmentPartner.getAttachmentPointLocation(this).getY() -
				LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getHeight();
			targetPositionForLacPromoterAttachment.setLocation(xDest, yDest);
			if (getPositionRef().distance(targetPositionForLacPromoterAttachment) > 10){
				// We are a ways away, so move toward the destination in a
				// somewhat random fashion.
				setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
				getMotionStrategyRef().setDestination(xDest, yDest);
			}
			else{
				// Head straight to the destination.
				setMotionStrategy(new LinearMotionStrategy(this, LacOperonModel.getMotionBounds(),
						targetPositionForLacPromoterAttachment, 3));
			}
		}
		
		return proposalAccepted;
	}

	public boolean isAvailableForAttaching() {
		return promoterAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE;
	}	
}
