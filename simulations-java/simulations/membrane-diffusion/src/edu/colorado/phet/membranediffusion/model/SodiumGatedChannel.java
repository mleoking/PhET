/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;
import edu.colorado.phet.membranediffusion.module.MembraneDiffusionDefaults;

/**
 * A gated channel through which sodium passes when the channel is open.
 * 
 * @author John Blanco
 */
public class SodiumGatedChannel extends GatedChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = MembraneDiffusionModel.getMembraneThickness() * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = MembraneDiffusionModel.getMembraneThickness() * 0.50; // In nanometers.
	
	// Constants that control the rate at which this channel will capture ions
	// when it is open.  Smaller numbers here will increase the capture rate
	// and thus make the flow appear to be faster.
	private static final double MIN_INTER_CAPTURE_TIME = 0.00005; // In seconds of sim time.
	private static final double MAX_INTER_CAPTURE_TIME = 0.00020; // In seconds of sim time.
	
	// Constant used when calculating how open this gate should be based on
	// a value that exists within the Hodgkin-Huxley model.  This was
	// empirically determined.
	private static final double M3H_WHEN_FULLY_OPEN = 0.25;
	
	// Delay range - used to make the timing of the instances of this gate
	// vary a little bit in terms of when they open and close.
	private static final double MAX_STAGGER_DELAY = MembraneDiffusionDefaults.MIN_MEMBRANE_DIFFUSION_CLOCK_DT * 100; // In seconds of sim time. 
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	private double staggerDelay = RAND.nextDouble() * MAX_STAGGER_DELAY;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	
	public SodiumGatedChannel(IParticleCapture modelContainingParticles, IHodgkinHuxleyModel hodgkinHuxleyModel) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		this.hodgkinHuxleyModel = hodgkinHuxleyModel;
		setUpperCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, -Math.PI / 2, Math.PI * 0.3));
		setLowerCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, Math.PI / 2, Math.PI * 0.3));
		reset();
	}
	
	public SodiumGatedChannel(){
		this(null, null);
	}

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(MembraneDiffusionConstants.SODIUM_COLOR, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return MembraneDiffusionConstants.SODIUM_COLOR;
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.SODIUM_GATED_CHANNEL;
	}
	
	@Override
	public void reset() {
		super.reset();
		
		// Set up the capture time range, which will be used to control the
		// rate of particle capture when this gate is open.
		setMinInterCaptureTime(MIN_INTER_CAPTURE_TIME);
		setMaxInterCaptureTime(MAX_INTER_CAPTURE_TIME);
	}

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		// Update the openness factor based on the state of the HH model.
		// This is very specific to the model and the type of channel.  Note
		// the non-linear mapping of conductance to the openness factor for
		// the channels.  This is to make the gates appear to snap open and
		// closed more rapidly, which was requested by the IPHY folks after
		// seeing some demos.
		double normalizedConductance =
    		Math.min(Math.abs(hodgkinHuxleyModel.get_delayed_m3h(staggerDelay))/M3H_WHEN_FULLY_OPEN, 1);

		double openness = 1 - Math.pow(normalizedConductance - 1, 2);
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
