/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayModel extends MultiNucleusDecayModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	// Defaults for the Alpha Decay model.  Could parameterize into
	// constructor some day if necessary.
	static final int MAX_NUCLEI = 99;
	static final int NUCLEUS_TYPE = NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM;
	
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
    public MultiNucleusAlphaDecayModel(NuclearPhysicsClock clock) {
    	super( clock, MAX_NUCLEI, NUCLEUS_TYPE );
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public Rectangle2D getBucketRectRef(){
		return BUCKET_RECT;
	}
    
	@Override
	protected void handleClockTicked(ClockEvent clockEvent) {

		super.handleClockTicked(clockEvent);
		
		// Move any alpha particles that have been produced by decay events.
		for (int i = 0; i < _alphaParticles.size(); i++){
			((AlphaParticle)_alphaParticles.get(i)).moveOut();
			// TODO: JPB TBD - Remove from the model any alphas that are far enough out to be off the canvas.
		}
	}

	@Override
	protected void addMaxNuclei() {
		
		// Create a new nucleus, positioning it in the bucket.

		double inBucketPosX = BUCKET_ORIGIN_X + BUCKET_WIDTH / 2;
		double inBucketPosY = BUCKET_ORIGIN_Y + BUCKET_HEIGHT / 2;

		AbstractDecayNucleus newNucleus;
		
		for (int i = 0; i < _maxNuclei; i++){
			if (_currentNucleusType == NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM){
				newNucleus = new Polonium211Nucleus(_clock);
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

	@Override
	protected void initializeNucleusListener() {
		// Create the object that will listen for nucleus events, such as decays.
        _nucleusListener = new AtomicNucleus.Adapter(){
            
            public void nucleusChangeEvent(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
                
                if (byProducts != null){
                    // There are some byproducts of this event that need to be
                    // managed by this object.
                    for (int i = 0; i < byProducts.size(); i++){
                        Object byProduct = byProducts.get( i );
                        if (byProduct instanceof AlphaParticle){
                        	// An alpha particle was produced, so tell
                        	// it to tunnel out of the nucleus, add it
                        	// to the list, and let any listeners know
                        	// about it.
                        	((AlphaParticle)byProduct).tunnelOut(nucleus.getPositionReference(),
                        			AtomicNucleus.DEFAULT_TUNNELING_REGION_RADIUS);
                            _alphaParticles.add(byProduct);
                            notifyModelElementAdded(byProduct);
                        }
                        else {
                            // We should never get here, debug it if it does.
                            System.err.println("Error: Unexpected byproduct of decay event.");
                            assert false;
                        }
                    }
                }
            }
        };
	}

	@Override
	protected void removeAllNuclei() {

		super.removeAllNuclei();
		
		// Remove any existing alpha particles and also let any listeners know
		// of their demise.
		for (int i = 0; i < _alphaParticles.size(); i++){
			notifyModelElementRemoved( _alphaParticles.get(i) );
		}
		_alphaParticles.clear();
	}
}
