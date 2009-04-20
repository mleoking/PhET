/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.model.AbstractAlphaDecayNucleus;
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
	
	public static final int MAX_NUCLEI = 99;  // Maximum number of nuclei that model will simulate.
	private static final int DEFAULT_NUCLEUS_TYPE = NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM;
	private static final double MAX_JITTER_LENGTH = 1; // In femtometers.
	private static final int CLOCKS_PER_JITTER = 2; // Number of clock ticks for a single jitter movement.
	
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
    private AbstractAlphaDecayNucleus [] _atomicNuclei;
    private ArrayList _alphaParticles = new ArrayList();
    private int _nucleusType;
    private AtomicNucleus.Adapter _nucleusListener;
    private final Random _rand = new Random();
    private Point2D [] _jitterOffsets;
    private int _jitterOffsetCount = 0;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public MultiNucleusAlphaDecayModel(NuclearPhysicsClock clock)
    {
        _clock = clock;
        _atomicNuclei = new AbstractAlphaDecayNucleus[MAX_NUCLEI];
        _jitterOffsets = new Point2D[MAX_NUCLEI];
        _nucleusType = NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM;

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
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLife(double halfLife){
		
		// Verify that the current nucleus is custom.
		if (_nucleusType != NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
			System.err.println("Warning: Can only set nucleus type for custom nucleus, ignoring request.");
			return;
		}
		
		// Set the new half life value.
		for (int i = 0; i < _atomicNuclei.length; i++){
			AbstractAlphaDecayNucleus nucleus = (AbstractAlphaDecayNucleus)_atomicNuclei[i];
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
    
    public void setPaused(boolean paused){
    	// Pause the decay and movement of all nuclei.
    	for (int i = 0; i < _atomicNuclei.length; i++){
    		_atomicNuclei[i].setPaused(paused);
    	}
    }
    
    /**
     * Reset all nuclei that are either active (meaning that they could decay
     * at any time) or decayed.
     * 
     * @return - The number of nuclei that are reset.
     */
    public int resetActiveAndDecayedNuclei(){
    	int resetCount = 0;
    	for (int i = 0; i < _atomicNuclei.length; i++){
    		if (_atomicNuclei[i].isDecayActive() || _atomicNuclei[i].hasDecayed()){
    			_atomicNuclei[i].reset();
    			_atomicNuclei[i].activateDecay();
    			resetCount++;
    		}
    	}
    	return resetCount;
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
    	
    	// Cause the active nuclei to "jitter".  For efficiency, not every
    	// active nucleus is moved every time.
    	for (int i = _jitterOffsetCount; i < _atomicNuclei.length; i = i + CLOCKS_PER_JITTER){
    		if (_atomicNuclei[i].isDecayActive() && !_atomicNuclei[i].isPaused()){
    			// This nucleus is active, so it should be jittered.
    			Point2D jitterOffset = _jitterOffsets[i];
    			Point2D currentLocation = _atomicNuclei[i].getPositionReference();
    			if (jitterOffset.getX() == 0 && jitterOffset.getY() == 0){
    				// Move this nucleus away from its center location.
    				generateJitterOffset( jitterOffset );
    				_atomicNuclei[i].setPosition( currentLocation.getX() + jitterOffset.getX(),
    						currentLocation.getY() + jitterOffset.getY());
    			}
    			else{
    				// Move back to original location.
    				_atomicNuclei[i].setPosition( currentLocation.getX() - jitterOffset.getX(),
    						currentLocation.getY() - jitterOffset.getY());
    				_jitterOffsets[i].setLocation(0, 0);
    			}
    		}
    	}
    	_jitterOffsetCount = (_jitterOffsetCount + 1) % CLOCKS_PER_JITTER;
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

		assert nucleusTypeID == NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM || nucleusTypeID == NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM;
		_nucleusType = nucleusTypeID;
		
		// Create a new nucleus, positioning it in the bucket.
		double inBucketPosX = BUCKET_ORIGIN_X + BUCKET_WIDTH / 2;
		double inBucketPosY = BUCKET_ORIGIN_Y + BUCKET_HEIGHT / 2;
		AbstractAlphaDecayNucleus newNucleus;
			
		for (int i = 0; i < MAX_NUCLEI; i++){
			if (_nucleusType == NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM){
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
    
    /**
     * Generate a random 2-dimensional offset for use in "jittering" a point
     * according to a Guassian distribution and a predefined length.
     * 
     * @param point
     */
    private void generateJitterOffset(Point2D point){
    	double length = _rand.nextGaussian() * (MAX_JITTER_LENGTH / 2);
    	if (length > MAX_JITTER_LENGTH){
    		length = MAX_JITTER_LENGTH;
    	}
    	double angle = _rand.nextDouble() * Math.PI * 2;
    	point.setLocation(Math.cos(angle) * length, Math.sin(angle) * length);
    }
}
