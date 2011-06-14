// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.model;

/**
 * Interface used to command the capturing of particles.  This is intended to
 * be used by membrane channels to tell the model that a particle should be
 * captured for movement through the channel.
 * 
 * @author John Blanco
 */
public interface IParticleCapture {

	void requestParticleThroughChannel(ParticleType particleType, MembraneChannel membraneChannel, 
			double maxVelocity, MembraneCrossingDirection direction);
}
