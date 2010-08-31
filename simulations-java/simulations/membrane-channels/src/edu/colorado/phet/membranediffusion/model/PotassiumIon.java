package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;

import edu.colorado.phet.membranediffusion.MembraneChannelsConstants;


public class PotassiumIon extends Particle {

	@Override
	public ParticleType getType() {
		return ParticleType.POTASSIUM_ION;
	}

	@Override
	public Color getRepresentationColor() {
		return MembraneChannelsConstants.POTASSIUM_COLOR;
	}
}
