package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.NeuronStrings;

public class SodiumIon extends Particle {
	
	@Override
	public ParticleType getType() {
		return ParticleType.SODIUM;
	}

	@Override
	public String getLabelText() {
		return NeuronStrings.SODIUM_CHEMICAL_SYMBOL;
	}

	@Override
	public Color getRepresentationColor() {
		return NeuronConstants.SODIUM_COLOR;
	}

	@Override
	public Color getLabelColor() {
		return Color.WHITE;
	}

	@Override
	public int getCharge() {
		return 1;
	}
}
