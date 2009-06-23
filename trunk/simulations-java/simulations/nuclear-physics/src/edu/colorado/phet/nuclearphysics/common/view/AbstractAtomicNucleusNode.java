/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.view;

import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is the starting point for a hierarchy of classes that are used
 * to represent an atomic nucleus in the view (i.e. on the canvas).  Since the
 * simulation represents nuclei in so many different ways, this class defines
 * no actual representation - it counts on the sub-classes to do that.
 *
 * @author John Blanco
 */
public class AbstractAtomicNucleusNode extends PNode {

	// Reference to the atomic nucleus that is represented.
	protected AtomicNucleus _atomicNucleus;
	
	// Current atomic weight - used for comparison when decay events occur.
	protected int _currentAtomicWeight;
	
	// Adapter for routing events that come from the nucleus.
	protected AtomicNucleus.Adapter _atomicNucleusAdapter = new AtomicNucleus.Adapter(){
	        
	        public void positionChanged(){
	            updatePosition();
	        }
	        
	        public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
	                ArrayList byProducts){
	            
	            handleNucleusChangedEvent( atomicNucleus, numProtons, numNeutrons, byProducts );
	        }
	    };

	public AbstractAtomicNucleusNode( AtomicNucleus atomicNucleus ) {
		_atomicNucleus = atomicNucleus;
	}

	/**
	 * Get a reference to the nucleus that this node is representing.
	 */
	public AtomicNucleus getNucleusRef() {
		return _atomicNucleus;
	}

	/**
	 * Perform any cleanup necessary before being garbage collected.
	 */
	public void cleanup() {
	    // Remove ourself as a listener from any place that we have registered
	    // in order to avoid memory leaks.
	    _atomicNucleus.removeListener(_atomicNucleusAdapter);
	}

	/**
	 * Updates the position of the node based on the position of the
	 * corresponding nucleus in the model.
	 */
	protected void updatePosition() {
		setOffset(_atomicNucleus.getPositionReference());
	}

	/**
	 * Handle the notification that says that the atomic nucleus has undergone
	 * some sort of change event, such as a decay.
	 * 
	 * @param atomicNucleus
	 * @param numProtons
	 * @param numNeutrons
	 * @param byProducts
	 */
	protected void handleNucleusChangedEvent(AtomicNucleus atomicNucleus, int numProtons,
			int numNeutrons, ArrayList byProducts) {
		
		// Update the current weight.
		_currentAtomicWeight = atomicNucleus.getAtomicWeight();
			    
	}
}