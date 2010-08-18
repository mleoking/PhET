/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;

public class SodiumLeakageChannel extends AbstractLeakChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = MembraneDiffusionModel.getMembraneThickness() * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = MembraneDiffusionModel.getMembraneThickness() * 0.50; // In nanometers.
	
	private static final Color BASE_COLOR = ColorUtils.interpolateRBGA(MembraneDiffusionConstants.SODIUM_COLOR, Color.YELLOW, 0.5);
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 10000; // In nanometers per sec of sim time.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumLeakageChannel(IParticleCapture modelContainingParticles, IHodgkinHuxleyModel hodgkinHuxleyModel) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		
		// Set the speed at which particles will move through the channel.
		setParticleVelocity(DEFAULT_PARTICLE_VELOCITY);
		
		// Set up the capture zones for this channel.
		setLowerCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, Math.PI / 2, Math.PI * 0.3));
		setUpperCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, -Math.PI / 2, Math.PI * 0.3));
	}
	
	public SodiumLeakageChannel(){
		this(null, null);
	}

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(BASE_COLOR, 0.15);
	}

	@Override
	public Color getEdgeColor() {
		return BASE_COLOR;
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
	}
	
	@Override
	protected ParticleType getParticleTypeToCapture() {
		return ParticleType.SODIUM_ION;
	}
}
