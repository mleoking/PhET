/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.utils.MathUtils;

public class PotassiumLeakageChannel extends AbstractLeakChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.50; // In nanometers.
	
	private static final Color BASE_COLOR = ColorUtils.interpolateRBGA(NeuronConstants.POTASSIUM_COLOR, new Color(00, 200, 255), 0.6);
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 10000; // In nanometers per sec of sim time.
	
	// A scaling factor that is used to normalize the amount of leak channel
	// current to a value between 0 and 1.
	private static final double PEAK_NEGATIVE_CURRENT = 3.44;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	private double previousNormalizedLeakCurrent = 0;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public PotassiumLeakageChannel(IParticleCapture modelContainingParticles, IHodgkinHuxleyModel hodgkinHuxleyModel) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		this.hodgkinHuxleyModel = hodgkinHuxleyModel;
		
		// Set the speed at which particles will move through the channel.
		setParticleVelocity(DEFAULT_PARTICLE_VELOCITY);
		
		// Set up the capture zone for this channel.
		setCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, 0, 0, Math.PI * 0.8));
		
		// Start the capture timer now, since leak channels are always
		// capturing particles.
		restartCaptureCountdownTimer();
	}
	
	public PotassiumLeakageChannel(){
		this(null, null);
	}

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	@Override
	public Color getChannelColor() {
		return ColorUtils.darkerColor(BASE_COLOR, 0.2);
	}

	@Override
	public Color getEdgeColor() {
		return BASE_COLOR;
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		return MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
	}
	
	@Override
	protected ParticleType getParticleTypeToCapture() {
		return ParticleType.POTASSIUM_ION;
	}

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		
		// Since this is a leak channel, it is always open, so the openness
		// is not updated as it is for the gated channels.  However, we DO
		// want more potassium to flow through when the leak current in the
		// HH model goes up, so the following code accomplishes that goal.

		double normalizedLeakCurrent = MathUtils.round(hodgkinHuxleyModel.get_l_current() / PEAK_NEGATIVE_CURRENT, 2);
		if (normalizedLeakCurrent <= 0){
			// Only pay attention to negative values for the current, which
			// we will map to potassium flow back into the cell.  This is a
			// bit of hollywooding.
			normalizedLeakCurrent = Math.max(normalizedLeakCurrent, -1);
			if (normalizedLeakCurrent != previousNormalizedLeakCurrent){
				previousNormalizedLeakCurrent = normalizedLeakCurrent;
				updateParticleCaptureRate(Math.abs(normalizedLeakCurrent));
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
		if (normalizedRate <= 0.01){
			// No captures at this rate.
			setMinInterCaptureTime(Double.POSITIVE_INFINITY);
			setMaxInterCaptureTime(Double.POSITIVE_INFINITY);
			restartCaptureCountdownTimer();
		}
		else{
			// Tweak the following values for different behavior.  Lower
			// values will yeild more captures per unit time.
			double smallestAllowableMin = 0.0002;
			double largestAllowableMin = 0.002;
			double smallestAllowableMax = smallestAllowableMin * 3;
			double largestAllowableMax = largestAllowableMin * 3;
			
			setMinInterCaptureTime(smallestAllowableMin + (1 - normalizedRate) * (largestAllowableMin - smallestAllowableMin));
			setMaxInterCaptureTime(smallestAllowableMax + (1 - normalizedRate) * (largestAllowableMax - smallestAllowableMax));
			
			if (getCaptureCountdownTimer() > getMaxInterCaptureTime()){
				// Only restart the capture countdown if the current values is
				// higher than the max.
				restartCaptureCountdownTimer();
			}
		}
	}
}
