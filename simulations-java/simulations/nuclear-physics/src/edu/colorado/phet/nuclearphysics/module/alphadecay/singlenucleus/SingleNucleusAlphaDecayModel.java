/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayModelListener;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Polonium211CompositeNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class SingleNucleusAlphaDecayModel implements NucleusTypeControl {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	public int DEFAULT_NUCLEUS_TYPE_ID = NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private AlphaDecayCompositeNucleus _atomicNucleus;
    private AlphaParticle _tunneledAlpha;
    private NuclearPhysicsClock _clock;
    private ArrayList _listeners = new ArrayList();
    private int _nucleusID;
    private AtomicNucleus.Adapter _atomicNucleusAdapter;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public SingleNucleusAlphaDecayModel(NuclearPhysicsClock clock)
    {
        _clock = clock;
        _nucleusID = DEFAULT_NUCLEUS_TYPE_ID;

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
            	removeCurrentNucleus();
            	int oldNucleusID = _nucleusID;
            	_nucleusID = DEFAULT_NUCLEUS_TYPE_ID;
            	addNewNucleus();
            	if (oldNucleusID != DEFAULT_NUCLEUS_TYPE_ID){
            		notifyNucleusTypeChanged();
            	}
            }
        });

        // Create the listener that will be registered for model events.
        _atomicNucleusAdapter = new AtomicNucleus.Adapter(){
            
            public void nucleusChangeEvent(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
                
                if (byProducts != null){
                    // There are some byproducts of this event that need to be
                    // managed by this object.
                    for (int i = 0; i < byProducts.size(); i++){
                        Object byProduct = byProducts.get( i );
                        if (byProduct instanceof AlphaParticle){
                            _tunneledAlpha = (AlphaParticle)byProduct;
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
        
        // No nucleus is added during construction - we count on being reset
        // as a part of the overall init process for that.
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    /**
     * Get a reference to the nucleus, of which there is only one in this
     * model.
     * 
     * @return - Reference to the nucleus model element.
     */
    public AlphaDecayCompositeNucleus getAtomNucleus()
    {
        return _atomicNucleus;
    }
    
    public ConstantDtClock getClock(){
        return _clock;
    }
    
	public boolean getEnergyChartShowing() {
		// The energy chart is always showing for this class.
		return true;
	}

	public void setNucleusType(int nucleusID) {
		if (nucleusID == _nucleusID){
			// Current type is already set, so nothing needs to be done.
			return;
		}
		removeCurrentNucleus();
		_nucleusID = nucleusID;
		addNewNucleus();
		notifyNucleusTypeChanged();
	}
	
	public int getNucleusType(){
		return _nucleusID;
	}
	
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

	/**
	 * Reset the currently active nucleus.
	 */
	public void resetNucleus(){
        // Reset the nucleus, including passing the alpha particle back to it.
        _atomicNucleus.reset( _tunneledAlpha );
        if (_tunneledAlpha != null){
        	notifyModelElementRemoved(_tunneledAlpha);
        }
        _tunneledAlpha = null;
	}
	
	/**
	 * Pause or unpause the model.
	 * 
	 * @param paused
	 */
	public void setPaused(boolean paused){
		_atomicNucleus.setPaused(paused);
	}
	
    /**
     * This method allows the caller to register for changes in the overall
     * model, as opposed to changes in the individual model elements.
     * 
     * @param listener
     */
    public void addListener(NuclearDecayModelListener listener)
    {
        assert !_listeners.contains( listener );
        
        if (!_listeners.contains( listener )){
            _listeners.add( listener );
        }
    }
    
    /**
     * Get the half life of the current nucleus.
     */
    public double getHalfLife(){
    	if (_atomicNucleus != null){
    		return _atomicNucleus.getHalfLife();
    	}
    	else{
        	return 0;
    	}
    }
    
	/**
	 * Set the half life for all nuclei in the model.
	 * 
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLife(double halfLife){
		
		// Verify that the current nucleus is custom.
		if (_nucleusID != NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM){
			System.err.println("Warning: Can only set nucleus type for custom nucleus, ignoring request.");
			return;
		}
		
		// Set the new half life value.
		AdjustableHalfLifeCompositeNucleus nucleus = (AdjustableHalfLifeCompositeNucleus)_atomicNucleus;
		nucleus.setHalfLife(halfLife);
		resetNucleus();
		
		// Inform any listeners of the change.
		notifyHalfLifeChanged();
	}
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){
        if (_tunneledAlpha != null){
            // We have a particle that has tunneled and needs to be moved.
            _tunneledAlpha.moveOut();
        }
    }
    
    private void removeCurrentNucleus(){

    	if (_atomicNucleus == null){
    		// Nothing to do.
    		return;
    	}
    	
	    // Remove listener from current nucleus.
		_atomicNucleus.removeListener(_atomicNucleusAdapter);
		
		// If decay had occurred and there is an alpha around as a result,
		// remove it.
		_tunneledAlpha = null;
		
		// Remove the nucleus itself and inform any listeners of its demise.
		_atomicNucleus.removedFromModel();
		AlphaDecayCompositeNucleus tempNucleus = _atomicNucleus;
		_atomicNucleus = null;
		notifyModelElementRemoved(tempNucleus);
    }

    /**
     * Add a new nucleus of the currently configured type.  This should be
     * called only when there isn't an existing nucleus.
     */
    private void addNewNucleus(){
    	if (_atomicNucleus != null){
    		// Since this model supports only one nucleus at a time, adding a
    		// new nucleus means that any existing one must go away.
    		System.err.println("Warning: Removing existing nucleus before adding new one.");
    		removeCurrentNucleus();
    	}
		switch (_nucleusID){
		case NuclearPhysicsConstants.NUCLEUS_ID_POLONIUM:
	        _atomicNucleus = new Polonium211CompositeNucleus(_clock, new Point2D.Double(0, 0));
	        break;
			
		case NuclearPhysicsConstants.NUCLEUS_ID_CUSTOM:
	        _atomicNucleus = new AdjustableHalfLifeCompositeNucleus(_clock, new Point2D.Double(0, 0));
	        break;
		}
        
        // Register as a listener for the nucleus so we can handle the
        // particles thrown off by alpha decay.
        _atomicNucleus.addListener( _atomicNucleusAdapter );
        
        // Inform any listeners of the changes.
        notifyModelElementAdded(_atomicNucleus);
    }
    
    /**
     * Notify listeners about the removal of an element from the model.
     * 
     * @param removedElement
     */
    private void notifyModelElementRemoved(Object removedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((NuclearDecayModelListener)_listeners.get(i)).modelElementRemoved( removedElement );
        }
    }
    
    /**
     * Notify listeners about the addition of an element to the model.
     * @param addedElement
     */
    private void notifyModelElementAdded(Object addedElement){
        for (int i = 0; i < _listeners.size(); i++){
            ((NuclearDecayModelListener)_listeners.get(i)).modelElementAdded( addedElement );
        }
    }

    /**
     * Notify listeners about the change of nucleus type.
     * @param addedElement
     */
    private void notifyNucleusTypeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((NuclearDecayModelListener)_listeners.get(i)).nucleusTypeChanged();
        }
    }

    /**
     * Notify listeners about the change of nucleus type.
     * @param addedElement
     */
    private void notifyHalfLifeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((NuclearDecayModelListener)_listeners.get(i)).halfLifeChanged();
        }
    }
    

}
