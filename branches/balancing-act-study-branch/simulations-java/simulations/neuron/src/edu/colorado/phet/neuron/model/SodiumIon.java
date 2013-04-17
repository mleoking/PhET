// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.neuron.NeuronConstants;

public class SodiumIon extends Particle {
	
	@Override
	public ParticleType getType() {
		return ParticleType.SODIUM_ION;
	}

	@Override
	public Color getRepresentationColor() {
		return NeuronConstants.SODIUM_COLOR;
	}
}
