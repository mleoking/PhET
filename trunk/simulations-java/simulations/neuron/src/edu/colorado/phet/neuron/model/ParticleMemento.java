/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Class that is used as a "memento" pattern for particles.  It is similar to
 * a full blown particle but contains less data and implements less
 * capability, and this is faster and easier to create.  This is intended for
 * use as part of the implementation of the record-and-playback feature.
 *
 * @author John Blanco
 */
public class ParticleMemento {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private final Point2D position = new Point2D.Double();
    private final Color representationColor;
    private final double opaqueness;
    private final double radius;
    private final ParticleType particleType;
    
    protected ArrayList<Listener> listeners = new ArrayList<Listener>();
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

	/**
     * Construct a particle memento.
     * 
     * @param particleMementoState - State for this particle memento.
     */
    public ParticleMemento(Particle particle) {
        position.setLocation( particle.getPosition() );
        opaqueness = particle.getOpaqueness();
        representationColor = particle.getRepresentationColor();
        radius = particle.getRadius();
        particleType = particle.getType();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * This is called to remove this particle memento from the model.  It simply
     * sends out a notification of removal, and all listeners (including the
     * view) are expected to act appropriately and to remove all references.
     */
    public void removeFromModel(){
    	notifyRemoved();
    	listeners.clear();
    }
    
    /**
     * Inform all listeners that this element has been removed from the model.
     */
    private void notifyRemoved(){
    	// Copy the list to avoid concurrent modification exceptions.
    	ArrayList<Listener> listenersCopy = new ArrayList<Listener>(listeners); 
    	// Notify all listeners that this particle was removed from the model.
        for (Listener listener : listenersCopy)
        {
            listener.removedFromModel(); 
        }        
    }
    
    public Point2D getPosition() {
        return position;
    }

    
    public Color getRepresentationColor() {
        return representationColor;
    }

    
    public double getOpaqueness() {
        return opaqueness;
    }

    
    public double getRadius() {
        return radius;
    }
    
    public ParticleType getParticleType() {
        return particleType;
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------
    
    public void addListener(Listener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    public void removeListener(Listener listener){
    	listeners.remove(listener);
    }

    public interface Listener {
        void removedFromModel();
    }
}
