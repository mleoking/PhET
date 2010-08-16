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
	
	// Controls the rate of leakage when no action potential is occurring.
	// Higher values mean more leakage, with 1 as the max.
	private static final double NOMINAL_LEAK_LEVEL = 0.005;
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 10000; // In nanometers per sec of sim time.
	
	// A scaling factor that is used to normalize the amount of leak channel
	// current to a value between 0 and 1.  This value was determined by
	// testing the Hodgkin-Huxley model.
	private static final double PEAK_NEGATIVE_CURRENT = 3.44;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	private double previousNormalizedLeakCurrent = 0;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumLeakageChannel(IParticleCapture modelContainingParticles, IHodgkinHuxleyModel hodgkinHuxleyModel) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		this.hodgkinHuxleyModel = hodgkinHuxleyModel;
		
		// Set the speed at which particles will move through the channel.
		setParticleVelocity(DEFAULT_PARTICLE_VELOCITY);
		
		// Set up the capture zones for this channel.
		setLowerCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, Math.PI / 2, Math.PI * 0.3));
		setUpperCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, -Math.PI / 2, Math.PI * 0.3));

		// Update the capture times.
		updateParticleCaptureRate(NOMINAL_LEAK_LEVEL);
		
		// Start the capture timer now, since leak channels are always
		// capturing particles.
		restartCaptureCountdownTimer();
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

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		
		// Since this is a leak channel, it is always open, so the openness
		// is not updated as it is for the gated channels.  However, we DO
		// want more sodium to flow through when the leak current in the
		// HH model goes up, so the following code accomplishes that goal.

		double normalizedLeakCurrent = MathUtils.round(hodgkinHuxleyModel.get_l_current() / PEAK_NEGATIVE_CURRENT, 2);
		if (normalizedLeakCurrent <= 0.01){
			// Only pay attention to negative values for the current, which
			// we will map to sodium flow back into the cell.  This is a
			// bit of hollywooding.
			normalizedLeakCurrent = Math.max(normalizedLeakCurrent, -1);
			if (normalizedLeakCurrent != previousNormalizedLeakCurrent){
				previousNormalizedLeakCurrent = normalizedLeakCurrent;
				updateParticleCaptureRate(Math.max(Math.abs(normalizedLeakCurrent), NOMINAL_LEAK_LEVEL));
			}
		}
	}
	
	/**
	 * Update the rate of particle capture based on the supplied normalized
	 * value.
	 * 
	 * @param normalizedRate - A value between 0 and 1 where 0 represents the
	 * minimum capture rate for particles and 1 represents the max.
	 */
	private void updateParticleCaptureRate(double normalizedRate){
		if (normalizedRate <= 0.001){
			// No captures at this rate.
			setMinInterCaptureTime(Double.POSITIVE_INFINITY);
			setMaxInterCaptureTime(Double.POSITIVE_INFINITY);
			restartCaptureCountdownTimer();
		}
		else{
			// Tweak the following values for different behavior.
			double absoluteMinInterCaptureTime = 0.0002;
			double variableMinInterCaptureTime = 0.002;
			double captureTimeRange = 0.005;
			double minInterCaptureTime = absoluteMinInterCaptureTime + (1 - normalizedRate) * (variableMinInterCaptureTime);
			setMinInterCaptureTime(minInterCaptureTime);
			setMaxInterCaptureTime(minInterCaptureTime + (1 - normalizedRate) * captureTimeRange);
			
			if (getCaptureCountdownTimer() > getMaxInterCaptureTime()){
				// Only restart the capture countdown if the current values is
				// higher than the max.
				restartCaptureCountdownTimer();
			}
		}
	}
}
