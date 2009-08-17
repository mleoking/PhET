/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.halflife;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.HeavyAdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate the decay of nuclei that
 * are used in radiometric dating.
 *
 * @author John Blanco
 */
public class RadiometricElementDecayModel extends MultiNucleusDecayModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	// Defaults for the Alpha Decay model.  Could parameterize into
	// constructor some day if necessary.
	static final int MAX_NUCLEI = 99;
	static final NucleusType DEFAULT_NUCLEUS_TYPE = NucleusType.CARBON_14;
	
	// Size and position of the bucket of nuclei which the user uses to add
	// nuclei to the simulation.
	static final double BUCKET_ORIGIN_X = 40;
	static final double BUCKET_ORIGIN_Y = 40;
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
    public RadiometricElementDecayModel(NuclearPhysicsClock clock) {
    	super( clock, MAX_NUCLEI, DEFAULT_NUCLEUS_TYPE, true );
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

		AtomicNucleus newNucleus;
		
		for ( int i = 0; i < _maxNuclei; i++ ){
			if ( _currentNucleusType == NucleusType.CARBON_14 ){
				newNucleus = new Carbon14Nucleus( _clock, new Point2D.Double(0, 0), true );
			}
			else if ( _currentNucleusType == NucleusType.URANIUM_238 ){
				newNucleus = new Uranium238Nucleus( _clock );
			}
			else{
				newNucleus = new HeavyAdjustableHalfLifeNucleus( _clock );
			}
			_atomicNuclei.add( newNucleus );
			newNucleus.setPosition( inBucketPosX, inBucketPosY );
			notifyModelElementAdded( newNucleus );
			_jitterOffsets[i] = new Point2D.Double();
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by alpha decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}
}
