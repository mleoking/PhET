// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * Class that represents lac promoter, which is the binding region on the DNA
 * strand for the RNA polymerase.
 * 
 * @author John Blanco
 */
public class LacPromoter extends Promoter {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Paint ELEMENT_PAINT = new Color(0, 153, 255);
	private static final double ATTACHMENT_RECOVERY_TIME = 8;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public LacPromoter(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
		setAttachmentRecoveryTime(ATTACHMENT_RECOVERY_TIME);
	}
	
	public LacPromoter(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
	public LacPromoter() {
		this(null, new Point2D.Double());
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	/**
	 * Returns true if an RNA polymerase is on this promoter, meaning that
	 * any part of it is over the promoter and very close by.  Note that this
	 * is distinct from being attached - this will return true when RNA
	 * polymerase is attached, but also if it is traversing it.
	 */
	public boolean isInContactWithRnaPolymerase(){
		boolean isInContactWithRnaPolymerase = false;
		Rectangle2D myBounds = getCompensatedBounds();
		for (RnaPolymerase rnaPoly : getModel().getRnaPolymeraseList()){
			if (rnaPoly.getCompensatedBounds().intersects(myBounds)){
				isInContactWithRnaPolymerase = true;
				break;
			}
		}
		return isInContactWithRnaPolymerase;
	}
	
	@Override
	protected boolean isInAllowableLocation() {
		// Find out if we are within range of our location on the DNA strand.
		return getPositionRef().distance(getModel().getDnaStrand().getLacPromoterLocation()) < LOCK_TO_DNA_DISTANCE;
	}

	@Override
	protected Point2D getDefaultLocation() {
		return getModel().getDnaStrand().getLacPromoterLocation();
	}

	/**
	 * In some situations, we don't want the lac promoter to allow the RNA
	 * polymerase to attach to it, since otherwise it can look like the
	 * RNA poly goes right over or through the LacI.  This method embodies
	 * the logic of such situations.  This is a bit of "hollywooding".
	 */
	@Override
	protected boolean isOkayToAttachToRnaPoly() {
		return ( getModel().getGlucoseList().size() > 0 ) || 
		       ( getModel().getLacIList().size() == 0 ) ||
		       ( getModel().getLacOperator() == null ) ||
			   ( getModel().getLacOperator().isLacIAttached() );
	}
}
