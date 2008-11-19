/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayControl;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayModelListener;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Polonium211Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.AlphaDecayNucleusTypeControl;

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
	
	private static final int MAX_NUCLEI = 99;  // Maximum number of nuclei that model will simulate.
	private static final int DEFAULT_NUCLEUS_TYPE = AlphaDecayNucleusTypeControl.NUCLEUS_TYPE_POLONIUM;
	
	// Size and position of the bucket of nuclei which the user uses to add
	// nuclei to the simulation.
	private static final double BUCKET_ORIGIN_X = 43;
	private static final double BUCKET_ORIGIN_Y = 45;
	private static final double BUCKET_WIDTH = 45;
	private static final double BUCKET_HEIGHT = BUCKET_WIDTH * 0.65;
	private static final Rectangle2D BUCKET_RECT = new Rectangle2D.Double(BUCKET_ORIGIN_X, BUCKET_ORIGIN_Y, 
			BUCKET_WIDTH, BUCKET_HEIGHT);
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private NuclearPhysicsClock _clock;
    private ArrayList _listeners = new ArrayList();
    private AtomicNucleus [] _atomicNuclei;
    private ArrayList _alphaParticles = new ArrayList();
    private int _nucleusType;
    private AtomicNucleus.Adapter _nucleusListener;
    
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
        
        // Create the object that will listen for nucleus events, such as decays.
        _nucleusListener = new AtomicNucleus.Adapter(){
            
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
        };
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
		if (nucleusType != _nucleusType){
			// The caller is setting a new nucleus type, so we need to
			// remove all existing nuclei and add a full set of the new type.
			removeAllNuclei();
			addNuclei(nucleusType);
			notifyNucleusTypeChanged();
		}
	}
	
	public int getNucleusType() {
		return _nucleusType;
	}
	
	public Rectangle2D getBucketRectRef(){
		return BUCKET_RECT;
	}
	
	/**
	 * Set the half life for all nuclei in the model.
	 * 
	 * @param halfLife - Half life in seconds.
	 */
	public void setHalfLife(double halfLife){
		
		// Verify that the current nucleus is custom.
		if (_nucleusType != NUCLEUS_TYPE_CUSTOM){
			System.err.println("Warning: Can only set nucleus type for custom nucleus, ignoring request.");
			return;
		}
		
		// Set the new half life value.
		for (int i = 0; i < _atomicNuclei.length; i++){
			AdjustableHalfLifeNucleus nucleus = (AdjustableHalfLifeNucleus)_atomicNuclei[i];
			if (nucleus != null){
				nucleus.setHalfLife(halfLife);
			}
		}
		
		// Inform any listeners of the change.
		notifyHalfLifeChanged();
	}
	
	public double getHalfLife(){
		
		double halfLife = 0;
		
		if (_atomicNuclei.length == 0){
			return 0;
		}
		
		// Get the first nucleus in the list, and assume that all nuclei are
		// the same in terms of half life.
		AtomicNucleus nucleus = _atomicNuclei[0];
		
		if (nucleus instanceof AlphaDecayControl){
			halfLife = ((AlphaDecayControl)nucleus).getHalfLife();
		}
		
		return halfLife;
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
    public void addListener(AlphaDecayModelListener listener)
    {
        if ( !_listeners.contains( listener )){
            _listeners.add( listener );
        }
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
		removeAllNuclei();
		_nucleusType = DEFAULT_NUCLEUS_TYPE;
		addNuclei( _nucleusType );
		notifyNucleusTypeChanged();
	}

	/**
	 * Remove all the existing nuclei and alpha particles from the model and
	 * send out the appropriate notifications.
	 */
	private void removeAllNuclei() {
		
		// Remove any existing nuclei and let the listeners know of their demise.
		for (int i = 0; i < _atomicNuclei.length; i++){
			if (_atomicNuclei[i] != null){
				_atomicNuclei[i].removeListener(_nucleusListener);
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
	}
	
	/**
	 * Add the nuclei to the model.  This adds all the nuclei that the model
	 * can handle all at once.
	 * 
	 * @param nucleusTypeID - Type of nucleus to add.
	 */
	private void addNuclei( int nucleusTypeID ){

		assert nucleusTypeID == NUCLEUS_TYPE_POLONIUM || nucleusTypeID == NUCLEUS_TYPE_CUSTOM;
		_nucleusType = nucleusTypeID;
		
		// Create a new nucleus, positioning it in the bucket.
		double inBucketPosX = BUCKET_ORIGIN_X + BUCKET_WIDTH / 2;
		double inBucketPosY = BUCKET_ORIGIN_Y + BUCKET_HEIGHT / 2;
		AtomicNucleus newNucleus;
			
		for (int i = 0; i < MAX_NUCLEI; i++){
			if (_nucleusType == NUCLEUS_TYPE_POLONIUM){
				newNucleus = new Polonium211Nucleus(_clock);
			}
			else{
				newNucleus = new AdjustableHalfLifeNucleus(_clock);
			}
			_atomicNuclei[i] = newNucleus;
			newNucleus.setPosition(inBucketPosX, inBucketPosY);
			notifyModelElementAdded(newNucleus);
	        
			// Register as a listener for the nucleus so we can handle the
	        // particles thrown off by alpha decay.
	        
	        newNucleus.addListener( _nucleusListener );
		}
	}
	
    /**
     * Notify listeners about the removal of an element from the model.
     * 
     * @param removedElement
     */
    private void notifyModelElementRemoved(Object removedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).modelElementRemoved( removedElement );
        }
    }
    
    /**
     * Notify listeners about the addition of an element to the model.
     * @param addedElement
     */
    private void notifyModelElementAdded(Object addedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).modelElementAdded( addedElement );
        }
    }

    /**
     * Notify listeners about the change of nucleus type.
     * @param addedElement
     */
    private void notifyNucleusTypeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).nucleusTypeChanged();
        }
    }

    /**
     * Notify listeners about the change of nucleus type.
     * @param addedElement
     */
    private void notifyHalfLifeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((AlphaDecayModelListener)_listeners.get(i)).halfLifeChanged();
        }
    }
}
