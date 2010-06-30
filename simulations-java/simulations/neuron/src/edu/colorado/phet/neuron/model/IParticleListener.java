/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;


/**
 * Interface for listening to particles.
 * 
 * @author John Blanco
 */
public interface IParticleListener {
    
    /**
     * Notification that the particle's position has changed.
     */
    void positionChanged();
    
    /**
     * Notification that one or more of the properties that are expected to
     * affect the appearance of the particle (e.g. its opaqueness) has
     * changed.  These are consolidated into a single call since the view
     * will generally re-render it anyway, so multiple calls would be less
     * efficient. 
     */
    void appearanceChanged();
    
    /**
     * Notification that the particle has been removed from the model.  In
     * most cases, any references to the particle should be removed in order
     * to avoid memory leaks.
     */
    void removedFromModel();
}
