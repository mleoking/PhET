/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Class that is used in the model to represent particles during playback.  It
 * is similar to a full blown particle but contains less data and implements
 * less capability, and this is faster and easier to create.  This is intended
 * for use as part of the implementation of the record-and-playback feature.
 *
 * @author John Blanco
 */
public class PlaybackParticle implements IViewableParticle {
    
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
    
    protected ArrayList<IParticleListener> listeners = new ArrayList<IParticleListener>();
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

	/**
     * Construct a playback particle.
     * 
     * @param particle - Real particle from which this playback particle
     * should be constructed.
     */
    public PlaybackParticle(Particle particle) {
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
     * This is called to remove this particle from the model.  It simply sends
     * out a notification of removal, and all listeners (including the view)
     * are expected to act appropriately and to remove all references.
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
    	ArrayList<IParticleListener> listenersCopy = new ArrayList<IParticleListener>(listeners); 
    	// Notify all listeners that this particle was removed from the model.
        for (IParticleListener listener : listenersCopy)
        {
            listener.removedFromModel(); 
        }        
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( position.getX(), position.getY() );
    }

    public Point2D getPositionReference() {
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
    
    public ParticleType getType() {
        return particleType;
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------
    
    public void addListener(IParticleListener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    public void removeListener(IParticleListener listener){
    	listeners.remove(listener);
    }

    public interface Listener {
        void removedFromModel();
    }
}
