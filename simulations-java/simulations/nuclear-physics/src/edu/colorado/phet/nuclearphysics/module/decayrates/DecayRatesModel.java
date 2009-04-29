/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate decay rates for various types of
 * atomic nuclei.
 *
 * @author John Blanco
 */
public class DecayRatesModel extends MultiNucleusDecayModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	private static final int MAX_NUCLEI = 500;
	private static final int NUCLEUS_TYPE = NuclearPhysicsConstants.NUCLEUS_ID_CARBON_14;
	private static final double MODEL_WIDTH = 800;  // Femtometers
	private static final double MODEL_HEIGHT = MODEL_WIDTH * 0.66;  // Femtometers
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	private static final Random rand = new Random();

    /**
     * @param clock
     */
    public DecayRatesModel(NuclearPhysicsClock clock) {
    	super( clock, MAX_NUCLEI, NUCLEUS_TYPE );
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

	protected void addMaxNuclei() {
		
		AbstractDecayNucleus newNucleus;
		
		for ( int i = 0; i < _maxNuclei; i++ ){
			if ( _currentNucleusType == NuclearPhysicsConstants.NUCLEUS_ID_CARBON_14 ){
				newNucleus = new Carbon14Nucleus( _clock );
			}
			else if ( _currentNucleusType == NuclearPhysicsConstants.NUCLEUS_ID_URANIUM_238 ){
				newNucleus = new Uranium238Nucleus( _clock );
			}
			else{
				newNucleus = new AdjustableHalfLifeNucleus( _clock );
			}
			_atomicNuclei[i] = newNucleus;
			newNucleus.setPosition( (rand.nextDouble() - 0.5) * MODEL_WIDTH, (rand.nextDouble() - 0.5) * MODEL_HEIGHT );
			notifyModelElementAdded( newNucleus );
			_jitterOffsets[i] = new Point2D.Double();
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by alpha decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}
}
