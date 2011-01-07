// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.betadecay.multinucleus;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.MultiNucleusDecayModel;
import edu.colorado.phet.nuclearphysics.common.model.SubatomicParticle;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.HeavyAdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.Hydrogen3Nucleus;
import edu.colorado.phet.nuclearphysics.model.LightAdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.module.betadecay.LabelVisibilityModel;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Beta Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class MultiNucleusBetaDecayModel extends MultiNucleusDecayModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	// Defaults for the Beta Decay model.  Could parameterize into
	// constructor some day if necessary.
	static final int MAX_NUCLEI = 99;
	static final NucleusType DEFAULT_NUCLEUS_TYPE = NucleusType.HYDROGEN_3;
	
	// Size and position of the bucket of nuclei which the user uses to add
	// nuclei to the simulation.
	static final double BUCKET_ORIGIN_X = 16;
	static final double BUCKET_ORIGIN_Y = 18;
	static final double BUCKET_WIDTH = 18;
	static final double BUCKET_HEIGHT = BUCKET_WIDTH * 0.65;
	private static final Rectangle2D BUCKET_RECT = new Rectangle2D.Double(BUCKET_ORIGIN_X, BUCKET_ORIGIN_Y, 
			BUCKET_WIDTH, BUCKET_HEIGHT);
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
	protected ArrayList<SubatomicParticle> _emittedParticles = new ArrayList<SubatomicParticle>();

    /**
     * @param clock
     */
    public MultiNucleusBetaDecayModel(NuclearPhysicsClock clock) {
    	super( clock, MAX_NUCLEI, DEFAULT_NUCLEUS_TYPE, true, new LabelVisibilityModel() );
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public Rectangle2D getBucketRectRef(){
		return BUCKET_RECT;
	}
    
	protected void handleClockTicked(ClockEvent clockEvent) {

		super.handleClockTicked(clockEvent);
		
		// Move any emitted particles that have been produced by decay events.
		for (SubatomicParticle particle : _emittedParticles){
			particle.translate();
		}
	}

	protected void addMaxNuclei() {
		
		// Create a new nucleus, positioning it in the bucket.

		double inBucketPosX = BUCKET_ORIGIN_X + BUCKET_WIDTH / 2;
		double inBucketPosY = BUCKET_ORIGIN_Y + BUCKET_HEIGHT / 2;

		AtomicNucleus newNucleus;
		
		for (int i = 0; i < _maxNuclei; i++){
			if (_currentNucleusType == NucleusType.HYDROGEN_3){
				newNucleus = new Hydrogen3Nucleus(_clock);
			}
			else if (_currentNucleusType == NucleusType.CARBON_14){
				newNucleus = new Carbon14Nucleus(_clock);
			}
			else if (_currentNucleusType == NucleusType.LIGHT_CUSTOM){
				newNucleus = new LightAdjustableHalfLifeNucleus(_clock);
			}
			else{
				System.err.println(getClass().getName() + "Other nuclei not yet implemented.");
				newNucleus = new HeavyAdjustableHalfLifeNucleus(_clock);
			}
			_atomicNuclei.add( newNucleus );
			newNucleus.setPosition(inBucketPosX, inBucketPosY);
			notifyModelElementAdded(newNucleus);
			_jitterOffsets[i] = new Point2D.Double();
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by beta decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}

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
                        if (byProduct instanceof SubatomicParticle){
                            _emittedParticles.add((SubatomicParticle)byProduct);
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

	protected void removeAllNuclei() {

		super.removeAllNuclei();
		
		// Remove any existing emitted particles and also let any listeners know
		// of their demise.
		for (int i = 0; i < _emittedParticles.size(); i++){
			notifyModelElementRemoved( _emittedParticles.get(i) );
		}
		_emittedParticles.clear();
	}
}
