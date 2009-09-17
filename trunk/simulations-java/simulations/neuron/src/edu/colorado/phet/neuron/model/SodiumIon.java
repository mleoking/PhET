package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.neuron.NeuronStrings;

public class SodiumIon extends Atom {
	
	@Override
	public AtomType getType() {
		return AtomType.SODIUM;
	}

	@Override
	public String getChemicalSymbol() {
		return NeuronStrings.SODIUM_CHEMICAL_SYMBOL;
	}

	@Override
	public Color getRepresentationColor() {
		return new Color(200, 0, 0);
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
