/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;


/**
 * Interface for listening to particles.
 * 
 * @author John Blanco
 */
public interface IParticleListener {
    void positionChanged();
    void opaquenessChanged();
    void removedFromModel();
}
