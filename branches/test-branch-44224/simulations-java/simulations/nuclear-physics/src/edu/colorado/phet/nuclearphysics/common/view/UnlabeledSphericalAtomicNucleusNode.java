/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;

/**
 * This class represents the view of an Atomic Nucleus from the model.  This
 * particular view presents the nucleus as a simple sphere with no label, no
 * constituent nucleons, and presents no explosion if and when the nucleus
 * decays.  It is intended for use in situations where there is a desire to
 * conserve CPU cycles, such as when a lot of nuclei are being simulated at
 * the same time.
 *
 * @author John Blanco
 */
public class UnlabeledSphericalAtomicNucleusNode extends AbstractAtomicNucleusNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private int _currentNumProtons;
    private int _currentNumNeutrons;
    private SphericalNode _nucleusRepresentation;

    // Adapter for registering to get nucleus events.
    AtomicNucleus.Adapter _atomicNucleusAdapter = new AtomicNucleus.Adapter(){
        
        public void positionChanged(){
            updatePosition();
        }
        
        public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                ArrayList byProducts){
            
            handleNucleusChangedEvent( atomicNucleus, numProtons, numNeutrons, byProducts );
        }
    };
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public UnlabeledSphericalAtomicNucleusNode(AtomicNucleus atomicNucleus)
    {
        super(atomicNucleus);
        _currentNumProtons = _atomicNucleus.getNumProtons();
        _currentNumNeutrons = _atomicNucleus.getNumNeutrons();

        // Register as a listener with the model representation.
        _atomicNucleus.addListener(_atomicNucleusAdapter);
        
        // Create and add the representation of the nucleus.
        updateRepresentation2();
        addChild(_nucleusRepresentation);
        
        // Make sure nothing is pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        // Put the nucleus in the right place.
        updatePosition();
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Get a reference to the nucleus within the model that is being monitored
     * by this node.
     */
    public AtomicNucleus getNucleusRef(){
    	return _atomicNucleus;
    }
    
    /**
     * Perform any cleanup necessary before being garbage collected.
     */
    public void cleanup(){
        // Remove ourself as a listener from any place that we have registered
        // in order to avoid memory leaks.
        _atomicNucleus.removeListener(_atomicNucleusAdapter);
    }

    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------
    
    /**
     * Handle the notification that says that the atomic nucleus has undergone
     * some sort of change event, such as a decay.
     * 
     * @param atomicNucleus
     * @param numProtons
     * @param numNeutrons
     * @param byProducts
     */
    protected void handleNucleusChangedEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
        
    	if ((numProtons != _currentNumProtons) || (numNeutrons != _currentNumNeutrons)){
    		// Some sort of decay has occurred.  Update the representation.
    		_currentNumProtons = numProtons;
    		_currentNumNeutrons = numNeutrons;
    		updateRepresentation2();
    	}
    	else{
    		// At the time of this writing, I can't think of any circumstance
    		// when we would get this notification with no change to the
    		// nucleus configuration, so cause an assert.
    		System.err.println(getClass().getName() + " - Error: Received notification of nucleus change but nothing appears to have changed.");
    		assert false;
    	}
    }
    
    private void updateRepresentation2(){
    	
    	// Create the paint that will be used.
    	Color baseColor = NucleusDisplayInfo.getDisplayInfoForNucleusConfig(_currentNumProtons,
        		_currentNumNeutrons).getDisplayColor();
		double radius = _atomicNucleus.getDiameter() / 2;
		Paint spherePaint = new RoundGradientPaint( radius, -radius, Color.WHITE,
                new Point2D.Double( -radius, radius ), baseColor );
		
    	if (_nucleusRepresentation == null){
    		// Representation doesn't exist yet, so create it.
    		_nucleusRepresentation = new SphericalNode(radius * 2, spherePaint, true);
    		addChild(_nucleusRepresentation);
    	}
    	else{
    		// Update the existing representation.
    		_nucleusRepresentation.setDiameter(2 * radius);
    		_nucleusRepresentation.setPaint(spherePaint);
    	}
    }
}
