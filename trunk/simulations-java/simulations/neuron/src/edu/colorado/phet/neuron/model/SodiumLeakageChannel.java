/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.neuron.NeuronConstants;

public class SodiumLeakageChannel extends AbstractLeakChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.50; // In nanometers.
	
	private static final Color BASE_COLOR = ColorUtils.interpolateRBGA(NeuronConstants.SODIUM_COLOR, Color.ORANGE, 0.6);
	
	// Constants that control the rate at which this channel will capture ions
	// when it is open.  Smaller numbers here will increase the capture rate
	// and thus make the flow appear to be faster.
	private static final double MIN_INTER_CAPTURE_TIME = 0.0025; // In seconds of sim time.
	private static final double MAX_INTER_CAPTURE_TIME = 0.0100; // In seconds of sim time.
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 5000; // In nanometers per sec of sim time.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumLeakageChannel(IParticleCapture modelContainingParticles) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		
		// Set the rate at which particles are captured for moving through
		// this channel.
		setMinInterCaptureTime(MIN_INTER_CAPTURE_TIME);
		setMaxInterCaptureTime(MAX_INTER_CAPTURE_TIME);
		
		// Set the speed at which particles will move through the channel.
		setParticleVelocity(DEFAULT_PARTICLE_VELOCITY);
		
		// Set up the capture zone for this channel.
		setCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, 0, 0, Math.PI * 0.3));
		
		// Start the capture timer now, since leak channels are always
		// capturing particles.
		restartCaptureCountdownTimer();
	}
	
	public SodiumLeakageChannel(){
		this(null);
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
