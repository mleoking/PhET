/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Polonium211CompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.AlphaDecayNucleusTypeControl;
import edu.colorado.phet.nuclearphysics.module.chainreaction.ChainReactionModel.Listener;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayModel implements AlphaDecayNucleusTypeControl {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	private static final int MAX_NUCLEI = 100;  // Maximum number of nuclei that model will simulate.
	
	// Size and position of the bucket of nuclei which the user uses to add
	// nuclei to the simulation.
	private static final double BUCKET_ORIGIN_X = 43;
	private static final double BUCKET_ORIGIN_Y = 45;
	private static final double BUCKET_WIDTH = 45;
	private static final double BUCKET_HEIGHT = BUCKET_WIDTH * 0.65;
	private static final Rectangle2D BUCKET_RECT = new Rectangle2D.Double(BUCKET_ORIGIN_X, BUCKET_ORIGIN_Y, 
			BUCKET_WIDTH, BUCKET_HEIGHT);
	
	// Possible values for the type of nucleus to be simulated.
	private static final int NUCLEUS_TYPE_POLONIUM = 1;
	private static final int NUCLEUS_TYPE_CUSTOM = 2;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysicsClock _clock;
    private ArrayList _listeners = new ArrayList();
    private AtomicNucleus [] _atomicNuclei;
    private ArrayList _alphaParticles = new ArrayList();
    private int _nucleusType;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public MultiNucleusAlphaDecayModel(NuclearPhysicsClock clock)
    {
        _clock = clock;
        _atomicNuclei = new AtomicNucleus[MAX_NUCLEI];
        _nucleusType = NUCLEUS_TYPE_POLONIUM;

        // Register as a listener to the clock.
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // Reset the model.
            	reset();
            }
        });
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock(){
        return _clock;
    }
    
	public boolean getEnergyChartShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setNucleusType(int nucleusType) {
		_nucleusType = nucleusType;
	}
	
	public int getNucleusType() {
		return _nucleusType;
	}
	
	public Rectangle2D getBucketRectRef(){
		return BUCKET_RECT;
	}

    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

	/**
     * This method allows the caller to register for changes in the overall
     * model, as opposed to changes in the individual model elements.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        assert !_listeners.contains( listener );
        
        _listeners.add( listener );
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){
    	// Move any alpha particles that have been produced by decay events.
    	for (int i = 0; i < _alphaParticles.size(); i++){
    		((AlphaParticle)_alphaParticles.get(i)).moveOut();
    		// TODO: JPB TBD - Remove from the model any alphas that are far enough out to be off the canvas.
    	}
    	
    	// TODO: JPB TBD - Will eventually implement a vibration of the nodes here (I think).
    }
    
	private void reset() {

		// Remove any existing nuclei and let the listeners know of their demise.
		for (int i = 0; i < _atomicNuclei.length; i++){
			if (_atomicNuclei[i] != null){
				notifyModelElementRemoved( _atomicNuclei[i] );
				_atomicNuclei[i] = null;
			}
		}
		
		// Remove any existing alpha particles and also let any listeners know
		// of their demise.
		for (int i = 0; i < _alphaParticles.size(); i++){
    		notifyModelElementRemoved( _alphaParticles.get(i) );
		}
		_alphaParticles.clear();
		
		
		// Create a new set of nuclei, positioning each to be in the nuclei
		// bucket.
		double inBucketPosX = BUCKET_ORIGIN_X + BUCKET_WIDTH / 2;
		double inBucketPosY = BUCKET_ORIGIN_Y + BUCKET_HEIGHT / 2;
		if (_nucleusType == NUCLEUS_TYPE_POLONIUM){
			for (int i = 0; i < MAX_NUCLEI; i++){
				AtomicNucleus newNucleus = new Polonium211Nucleus(_clock);
				_atomicNuclei[i] = newNucleus;
				newNucleus.setPosition(inBucketPosX, inBucketPosY);
				notifyModelElementAdded(newNucleus);
		        
				// Register as a listener for the nucleus so we can handle the
		        // particles thrown off by alpha decay.
		        
		        newNucleus.addListener( new AtomicNucleus.Adapter(){
		            
		            public void atomicWeightChanged(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
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
		        });
			}
		}
	}
	
    /**
     * Notify listeners about the removal of an element from the model.
     * 
     * @param removedElement
     */
    private void notifyModelElementRemoved (Object removedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementRemoved( removedElement );
        }
    }
    
    /**
     * Notify listeners about the addition of an element to the model.
     * @param addedElement
     */
    private void notifyModelElementAdded (Object addedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).modelElementAdded( addedElement );
        }
    }

	//------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    /**
     * This listener interface allows listeners to get notified when an alpha
     * particle is added (i.e. come in to existence by separating from the
     * nucleus) or is removed (i.e. recombines with the nucleus).
     */
    public static interface Listener {
        /**
         * This informs the listener that some new element has been added
         * to the model.
         * 
         * @param modelElement - Element that was added to the model.
         */
        public void modelElementAdded(Object modelElement);
        
        /**
         * This informs the listener that a model element was removed.
         * 
         * @param modelElement - Element that was removed from the model.
         */
        public void modelElementRemoved(Object modelElement);
    }
}
