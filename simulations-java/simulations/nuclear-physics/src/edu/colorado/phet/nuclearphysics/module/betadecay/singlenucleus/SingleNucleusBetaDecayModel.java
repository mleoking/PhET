/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay.singlenucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayModelListener;
import edu.colorado.phet.nuclearphysics.common.model.SubatomicParticle;
import edu.colorado.phet.nuclearphysics.model.AdjustableHalfLifeCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.Carbon14CompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.Hydrogen3CompositeNucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Beta Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class SingleNucleusBetaDecayModel implements NucleusTypeControl {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	public NucleusType DEFAULT_NUCLEUS_TYPE = NucleusType.HYDROGEN_3;
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private CompositeAtomicNucleus _atomicNucleus;
    private NuclearPhysicsClock _clock;
    private ArrayList _listeners = new ArrayList();
    private NucleusType _nucleusType;
    private AtomicNucleus.Adapter _atomicNucleusAdapter;
	private ArrayList<SubatomicParticle> _emittedParticles = new ArrayList<SubatomicParticle>();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public SingleNucleusBetaDecayModel(NuclearPhysicsClock clock)
    {
        _clock = clock;
        _nucleusType = DEFAULT_NUCLEUS_TYPE;

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
            	NucleusType oldNucleusType = _nucleusType;
            	_nucleusType = DEFAULT_NUCLEUS_TYPE;
            	addNewNucleus();
            	if (oldNucleusType != DEFAULT_NUCLEUS_TYPE){
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
    public CompositeAtomicNucleus getAtomNucleus()
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

	public void setNucleusType(NucleusType nucleusType){
		
		if (_nucleusType == nucleusType){
			// Current type is already set, so nothing needs to be done.
			return;
		}
		removeCurrentNucleus();
		_nucleusType = nucleusType;
		addNewNucleus();
		notifyNucleusTypeChanged();
	}
	
	public NucleusType getNucleusType() {
		return _nucleusType;
	}
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

	/**
	 * Reset the currently active nucleus.
	 */
	public void resetNucleus(){
        // Reset the nucleus.
		// TODO: Need to implement this.
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
		if (_nucleusType != NucleusType.HEAVY_CUSTOM){
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
		// Move any emitted particles that have been produced by decay events.
		for (SubatomicParticle particle : _emittedParticles){
			particle.translate();
		}
    }
    
    private void removeCurrentNucleus(){

    	if (_atomicNucleus != null){
    		// Remove listener from current nucleus.
    		_atomicNucleus.removeListener(_atomicNucleusAdapter);
    		
    		// Remove the nucleus itself and inform any listeners of its demise.
    		_atomicNucleus.removedFromModel();
    		CompositeAtomicNucleus tempNucleus = _atomicNucleus;
    		_atomicNucleus = null;
    		notifyModelElementRemoved(tempNucleus);
    	}
		
		// Remove any existing emitted particles and also let any listeners know
		// of their demise.
		for (int i = 0; i < _emittedParticles.size(); i++){
			notifyModelElementRemoved( _emittedParticles.get(i) );
		}
		_emittedParticles.clear();
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
		switch (_nucleusType){
		case HYDROGEN_3:
	        _atomicNucleus = new Hydrogen3CompositeNucleus(_clock, new Point2D.Double(0, 0));
	        break;
			
		case CARBON_14:
	        _atomicNucleus = new Carbon14CompositeNucleus(_clock, new Point2D.Double(0, 0));
	        break;
			
		case LIGHT_CUSTOM:
	        _atomicNucleus = new Hydrogen3CompositeNucleus(_clock, new Point2D.Double(0, 0));
	        break;
			
		}
        
        // Register as a listener for the nucleus so we can handle the
        // particles thrown off by beta decay.
        _atomicNucleus.addListener( _atomicNucleusAdapter );
        
        // In this model, the nucleus is activated (so that it is moving
        // towards decay) right away.
        _atomicNucleus.activateDecay();
        
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
