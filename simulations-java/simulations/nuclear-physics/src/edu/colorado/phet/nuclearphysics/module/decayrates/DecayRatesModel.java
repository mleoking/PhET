/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class DecayRatesModel extends MultiNucleusDecayModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	// Defaults for the Alpha Decay model.  Could parameterize into
	// constructor some day if necessary.
	static final int MAX_NUCLEI = 99;
	static final int NUCLEUS_TYPE = NuclearPhysicsConstants.NUCLEUS_ID_CARBON_14;
	
	// Size and position of the bucket of nuclei which the user uses to add
	// nuclei to the simulation.
	static final double BUCKET_ORIGIN_X = 43;
	static final double BUCKET_ORIGIN_Y = 45;
	static final double BUCKET_WIDTH = 45;
	static final double BUCKET_HEIGHT = BUCKET_WIDTH * 0.65;
	private static final Rectangle2D BUCKET_RECT = new Rectangle2D.Double(BUCKET_ORIGIN_X, BUCKET_ORIGIN_Y, 
			BUCKET_WIDTH, BUCKET_HEIGHT);
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	protected ArrayList _alphaParticles = new ArrayList();

    /**
     * @param clock
     */
    public DecayRatesModel(NuclearPhysicsClock clock) {
    	super( clock, MAX_NUCLEI, NUCLEUS_TYPE );
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public Rectangle2D getBucketRectRef(){
		return BUCKET_RECT;
	}
    
	protected void addMaxNuclei() {
		
		// Create a new nucleus, positioning it in the bucket.

		double inBucketPosX = BUCKET_ORIGIN_X + BUCKET_WIDTH / 2;
		double inBucketPosY = BUCKET_ORIGIN_Y + BUCKET_HEIGHT / 2;

		AbstractDecayNucleus newNucleus;
		
		for (int i = 0; i < _maxNuclei; i++){
			if (_currentNucleusType == NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM){
				newNucleus = new Carbon14Nucleus(_clock);
			}
			else{
				newNucleus = new AdjustableHalfLifeNucleus(_clock);
			}
			_atomicNuclei[i] = newNucleus;
			newNucleus.setPosition(inBucketPosX, inBucketPosY);
			notifyModelElementAdded(newNucleus);
			_jitterOffsets[i] = new Point2D.Double();
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by alpha decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}
}
