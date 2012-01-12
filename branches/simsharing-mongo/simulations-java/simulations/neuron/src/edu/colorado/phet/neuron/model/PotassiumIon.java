// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.neuron.NeuronConstants;

public class PotassiumIon extends Particle {

	@Override
	public ParticleType getType() {
		return ParticleType.POTASSIUM_ION;
	}

	@Override
	public Color getRepresentationColor() {
		return NeuronConstants.POTASSIUM_COLOR;
	}
}
