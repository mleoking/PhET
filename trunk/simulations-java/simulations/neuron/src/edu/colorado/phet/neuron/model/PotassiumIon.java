package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.NeuronStrings;

public class PotassiumIon extends Particle {

	@Override
	public ParticleType getType() {
		return ParticleType.POTASSIUM;
	}

	@Override
	public String getLabelText() {
		return NeuronStrings.POTASSIUM_CHEMICAL_SYMBOL;
	}

	@Override
	public Color getRepresentationColor() {
		return NeuronConstants.POTASSIUM_COLOR;
	}
	
	@Override
	public Color getLabelColor() {
		return Color.BLACK;
	}

	@Override
	public int getCharge() {
		return 1;
	}
}
