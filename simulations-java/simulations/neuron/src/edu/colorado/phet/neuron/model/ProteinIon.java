package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.NeuronStrings;

public class ProteinIon extends Particle {
	
	@Override
	public ParticleType getType() {
		return ParticleType.PROTEIN_ION;
	}

	@Override
	public String getLabelText() {
		return NeuronStrings.PROTEIN_LABEL;
	}

	@Override
	public Color getRepresentationColor() {
		return NeuronConstants.PROTEIN_COLOR;
	}

	@Override
	public Color getLabelColor() {
		return Color.WHITE;
	}

	@Override
	public int getCharge() {
		return -1;
	}

	@Override
	public double getDiameter() {
		return 5;
	}
}
