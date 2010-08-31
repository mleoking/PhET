package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;

import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;


public class PotassiumIon extends Particle {

	@Override
	public ParticleType getType() {
		return ParticleType.POTASSIUM_ION;
	}

	@Override
	public Color getRepresentationColor() {
		return MembraneDiffusionConstants.POTASSIUM_COLOR;
	}
}
