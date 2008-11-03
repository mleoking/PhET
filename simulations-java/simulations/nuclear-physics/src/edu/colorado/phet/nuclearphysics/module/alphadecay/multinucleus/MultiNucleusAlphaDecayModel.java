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
	
	private static final int MAX_NUCLEI = 2;  // Maximum number of nuclei that model will simulate.
	
	// Size and position of the bucket of nuclei which the user uses to add
	// nuclei to the simulation.
	private static final double BUCKET_ORIGIN_X = 24;
	private static final double BUCKET_ORIGIN_Y = 24;
	private static final double BUCKET_WIDTH = 18;
	private static final double BUCKET_HEIGHT = BUCKET_WIDTH * 0.8;
	private static final Rectangle2D BUCKET_RECT = new Rectangle2D.Double(BUCKET_ORIGIN_X, BUCKET_ORIGIN_Y, 
			BUCKET_WIDTH, BUCKET_HEIGHT);
	
	// Possible values for the type of nucleus to be simulated.
	private static final int NUCLEUS_TYPE_POLONIUM = 1;
	private static final int NUCLEUS_TYPE_CUSTOM = 2;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private ConstantDtClock _clock;
    private ArrayList _listeners = new ArrayList();
    private AtomicNucleus [] _atomicNuclei;
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
		
		// Create a new set of nuclei.
		// TODO: JPB TBD.
		
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
