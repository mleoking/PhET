/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.neuron.NeuronConstants;

public class PotassiumLeakageChannel extends AbstractLeakChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.4; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.70; // In nanometers.
	
	// Constants that control the rate at which this channel will capture ions
	// when it is open.  Smaller numbers here will increase the capture rate
	// and thus make the flow appear to be faster.
	private static final double MIN_INTER_CAPTURE_TIME = 0.0025; // In seconds of sim time.
	private static final double MAX_INTER_CAPTURE_TIME = 0.0100; // In seconds of sim time.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public PotassiumLeakageChannel(IParticleCapture modelContainingParticles) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		
		// Set the rate at which particles are captured for moving through
		// this channel.
		setMinInterCaptureTime(MIN_INTER_CAPTURE_TIME);
		setMaxInterCaptureTime(MAX_INTER_CAPTURE_TIME);
		
		// Set up the capture zone for this channel.
		setCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, Math.PI, 0, Math.PI * 0.6));
		
		// Start the capture timer now, since leak channels are always
		// capturing particles.
		restartCaptureCountdownTimer();
	}

	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(NeuronConstants.POTASSIUM_COLOR, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return NeuronConstants.POTASSIUM_COLOR;
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
	}
	
	@Override
	protected ParticleType getParticleTypeToCapture() {
		return ParticleType.POTASSIUM_ION;
	}
}
