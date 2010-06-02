/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;

public class PotassiumLeakageChannel extends AbstractLeakChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = MembraneDiffusionModel.getMembraneThickness() * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = MembraneDiffusionModel.getMembraneThickness() * 0.50; // In nanometers.
	
	private static final Color BASE_COLOR = ColorUtils.interpolateRBGA(MembraneDiffusionConstants.POTASSIUM_COLOR,
			new Color(00, 200, 255), 0.6);
	
	private static final double DEFAULT_PARTICLE_VELOCITY = 5000; // In nanometers per sec of sim time.
	
	// Constants that define the rate and variability of particle capture.
	private static final double MIN_INTER_PARTICLE_CAPTURE_TIME = 0.002; // In seconds of sim time.
	private static final double MAX_INTER_PARTICLE_CAPTURE_TIME = 0.004; // In seconds of sim time.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public PotassiumLeakageChannel(IParticleCapture modelContainingParticles, IHodgkinHuxleyModel hodgkinHuxleyModel) {
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);

		// Set the speed at which particles will move through the channel.
		setParticleVelocity(DEFAULT_PARTICLE_VELOCITY);
		
		// Set up the capture zones for this channel.
		setInteriorCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, Math.PI, Math.PI * 0.5));
		setExteriorCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, 0, Math.PI * 0.5));
		
		// Set the rate of particle capture for leakage.
		setMinInterCaptureTime(MIN_INTER_PARTICLE_CAPTURE_TIME);
		setMaxInterCaptureTime(MAX_INTER_PARTICLE_CAPTURE_TIME);
		
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
	protected MembraneCrossingDirection chooseCrossingDirection() {
		// Generally, this channel leaks from in to out, since the
		// concentration of potassium is greater on the inside of the cell.
		// However, the IPHY people requested that there should occasionally
		// be some leakage in the other direction for greater realism, hence
		// the random choice below.
		MembraneCrossingDirection direction = MembraneCrossingDirection.IN_TO_OUT;
		if (RAND.nextDouble() < 0.2){
			direction = MembraneCrossingDirection.OUT_TO_IN;
		}
		return direction;
	}
}
