/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.utils.MathUtils;

public class SodiumGatedChannel extends AbstractGatedChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.4; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.70; // In nanometers.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private HodgkinHuxleyModel hodgekinHodgkinHuxleyModel;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumGatedChannel(HodgkinHuxleyModel hodgekinHuxleyModel) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, ParticleType.SODIUM_ION);
		this.hodgekinHodgkinHuxleyModel = hodgekinHuxleyModel;
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
		double openness = Math.min(Math.abs(hodgekinHodgkinHuxleyModel.get_na_current())/290, 1);
		if (openness > 0 && openness < 1){
			// Trim off some digits, otherwise we are continuously making
			// tiny changes to this value due to internal gyrations of the
			// HH model.
			openness = MathUtils.round(openness, 2);
		}
		if (openness != getOpenness()){
			System.out.println("Current = " + hodgekinHodgkinHuxleyModel.get_na_current() + ", openness = " + openness);
			setOpenness(openness);
		}
	}
}
