/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.utils.MathUtils;

/**
 * A gated channel through which sodium passes when the channel is open.
 * 
 * @author John Blanco
 */
public class SodiumGatedChannel extends GatedChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.4; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.70; // In nanometers.
	
	// Constants that control the rate at which this channel will transport
	// ions through it when open.
	private double MIN_INTER_CAPTURE_TIME = 0; // In seconds of sim time.
	private double MAX_INTER_CAPTURE_TIME = 0.00008; // In seconds of sim time.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private HodgkinHuxleyModel hodgkinHodgkinHuxleyModel;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumGatedChannel(HodgkinHuxleyModel hodgkinHuxleyModel, IParticleCapture modelContainingParticles) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		this.hodgkinHodgkinHuxleyModel = hodgkinHuxleyModel;
		setCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, 0, 0, Math.PI * 0.8));
		setMinInterCaptureTime(MIN_INTER_CAPTURE_TIME);
		setMaxInterCaptureTime(MAX_INTER_CAPTURE_TIME);
	}

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(NeuronConstants.SODIUM_COLOR, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return NeuronConstants.SODIUM_COLOR;
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
	}

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		// Update the openness factor based on the state of the HH model.
		// This is very specific to the model and the type of channel.
		double openness = Math.min(Math.abs(hodgkinHodgkinHuxleyModel.get_na_current())/290, 1);
		if (openness > 0 && openness < 1){
			// Trim off some digits, otherwise we are continuously making
			// tiny changes to this value due to internal gyrations of the
			// HH model.
			openness = MathUtils.round(openness, 2);
		}
		if (openness != getOpenness()){
			setOpenness(openness);
			if (isOpen() && getCaptureCountdownTimer() == Double.POSITIVE_INFINITY){
				// We have just transitioned to the open state, so it is time
				// to start capturing ions.
				restartCaptureCountdownTimer();
			}
		}
	}

	@Override
	protected ParticleType getParticleTypeToCapture() {
		return ParticleType.SODIUM_ION;
	}
}
