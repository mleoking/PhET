/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;
import edu.colorado.phet.membranediffusion.module.MembraneDiffusionDefaults;

/**
 * A gated channel through which sodium passes when the channel is open.  This
 * implementation has two different gates, which is apparently closer to real-
 * life voltage-gated sodium channels.
 * 
 * @author John Blanco
 */
public class SodiumDualGatedChannel extends GatedChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = MembraneDiffusionModel.getMembraneThickness() * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = MembraneDiffusionModel.getMembraneThickness() * 0.50; // In nanometers.
	
	// Constants that control the rate at which this channel will capture ions
	// when it is open.  Smaller numbers here will increase the capture rate
	// and thus make the flow appear to be faster.
	private static final double MIN_INTER_CAPTURE_TIME = 0.00002; // In seconds of sim time.
	private static final double MAX_INTER_CAPTURE_TIME = 0.00010; // In seconds of sim time.
	
	// Constant used when calculating how open this gate should be based on
	// a value that exists within the Hodgkin-Huxley model.  This was
	// empirically determined.
	private static final double M3H_WHEN_FULLY_OPEN = 0.25;
	
	// Possible values for internal state.
	private enum GateState {IDLE, OPENING, BECOMING_INACTIVE, INACTIVATED, RESETTING};
	
	// Values used for deciding on state transitions.
	private static final double ACTIVATION_DECISION_THRESHOLD = 0.1;
	private static final double FULLY_INACTIVE_DECISION_THRESHOLD = 0.98;
	
	// Values used for timed state transitions.
	private static final double INACTIVE_TO_RESETTING_TIME = 0.001; // In seconds of sim time. 
	private static final double RESETTING_TO_IDLE_TIME = 0.001; // In seconds of sim time.
	
	// Delay range - used to make the timing of the instances of this gate
	// vary a little bit in terms of when they open and close.
	private static final double MAX_STAGGER_DELAY = MembraneDiffusionDefaults.MIN_MEMBRANE_DIFFUSION_CLOCK_DT * 15; // In seconds of sim time. 
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	private GateState gateState = GateState.IDLE;
	private double previousNormalizedConductance;
	private double stateTransitionTimer = 0;
	private double staggerDelay;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	
	public SodiumDualGatedChannel(IParticleCapture modelContainingParticles, IHodgkinHuxleyModel hodgkinHuxleyModel) {
		
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		this.hodgkinHuxleyModel = hodgkinHuxleyModel;
		setExteriorCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, 0, Math.PI * 0.7));
		reset();
	}

	public SodiumDualGatedChannel(){
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
		return MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
	}

	@Override
	public void stepInTime(double dt) {
		
		super.stepInTime(dt);
		
		// Get the conductance and normalize it from 0 to 1.
		double normalizedConductance = calculateNormalizedConductance();
		
		if (normalizedConductance >= 0 && normalizedConductance <= 1){
			// Trim off some digits to limit very small changes.
			normalizedConductance = MathUtils.round(normalizedConductance, 2);
		}
		else{
			// This shouldn't happen, debug it if it does.
			System.err.println(getClass().getName() + "Nomalized conductance out of range, = " + normalizedConductance);
			assert false;
		}
		
		// Update the state.
		switch ( gateState ) {
		case IDLE:
			if (normalizedConductance > ACTIVATION_DECISION_THRESHOLD){
				// We are opening, change to the appropriate state.
				gateState = GateState.OPENING;
			}
			break;
			
		case OPENING:
			if (isOpen() && getCaptureCountdownTimer() == Double.POSITIVE_INFINITY){
				// We are open enough to start capturing particles.
				restartCaptureCountdownTimer();
			}
			if (previousNormalizedConductance  > normalizedConductance){
				// We are on the way down, so set a new state.
				gateState = GateState.BECOMING_INACTIVE;
				// Should be fully open at this point.
				setOpenness(1);
			}
			else{
				// Set the openness based on the normalized conductance value.
				// Note the non-linear mapping.  This was done to make them
				// appear to be fully open earlier in the action potential,
				// which was requested by the IPHY folks.
				setOpenness(1 - Math.pow(normalizedConductance - 1, 8));
			}
			break;
			
		case BECOMING_INACTIVE:
			if (getInactivationAmt() < FULLY_INACTIVE_DECISION_THRESHOLD){
				// Not yet fully inactive - update the level.  Note the non-
				// linear mapping to the conductance amount.
				setInactivationAmt(1 - Math.pow(normalizedConductance, 5));
			}
			else{
				// Fully inactive, move to next state.
				setInactivationAmt(1);
				gateState = GateState.INACTIVATED;
				stateTransitionTimer = INACTIVE_TO_RESETTING_TIME;
			}
			break;
			
		case INACTIVATED:
			stateTransitionTimer -= dt;
			if (stateTransitionTimer < 0){
				// Time to start resetting.
				gateState = GateState.RESETTING;
				stateTransitionTimer = RESETTING_TO_IDLE_TIME;
			}
			break;
			
		case RESETTING:
			stateTransitionTimer -= dt;
			if (stateTransitionTimer >= 0){
				// Move the values of openness and activation back towards
				// their idle (i.e. resting) states.  The mapping of the
				// inactivation amount as a function of time is very non-
				// linear.  This is because the IPHY people requested that
				// the "little ball doesn't pop out" until the the gate has
				// closed up.
				setOpenness(1 - Math.pow(stateTransitionTimer/RESETTING_TO_IDLE_TIME - 1, 10));
				setInactivationAmt(1 - Math.pow(stateTransitionTimer/RESETTING_TO_IDLE_TIME - 1, 20));
			}
			else{
				// Go back to the idle, or resting, state.
				setOpenness(0);
				setInactivationAmt(0);
				updateStaggerDelay();
				gateState = GateState.IDLE;
			}
			break;
		}
		
		// Save values for the next time through.
		previousNormalizedConductance = normalizedConductance;
	}
	
	@Override
	public void moveParticleThroughNeuronMembrane(Particle particle, double maxVelocity) {
		// TODO: Need to set the bounds correctly if we end up keeping this class.
		particle.setMotionStrategy(new TraverseChannelAndWalkStrategy(this, particle.getPositionReference(), 
				MembraneDiffusionModel.getParticleChamberRect()));
	}

	/**
	 * This membrane channel has an inactivation gate.
	 */
	@Override
	public boolean getHasInactivationGate() {
		return true;
	}

	private double calculateNormalizedConductance(){
		return Math.min(Math.abs(hodgkinHuxleyModel.get_delayed_m3h(staggerDelay))/M3H_WHEN_FULLY_OPEN, 1);
	}

	@Override
	protected ParticleType getParticleTypeToCapture() {
		return ParticleType.SODIUM_ION;
	}
	
	@Override
	public void reset() {
		super.reset();
		
		// Set up the capture time range, which will be used to control the
		// rate of particle capture when this gate is open.
		setMinInterCaptureTime(MIN_INTER_CAPTURE_TIME);
		setMaxInterCaptureTime(MAX_INTER_CAPTURE_TIME);
		
		// Initialize some internal state.
		gateState = GateState.IDLE;
		stateTransitionTimer = 0;
		if (hodgkinHuxleyModel != null){
			previousNormalizedConductance = calculateNormalizedConductance();
		}
		
		// Initialize the stagger delay.
		updateStaggerDelay();
	}

	private void updateStaggerDelay(){
		staggerDelay = RAND.nextDouble() * MAX_STAGGER_DELAY;
	}

	@Override
	protected MembraneCrossingDirection chooseCrossingDirection() {
		return MembraneCrossingDirection.OUT_TO_IN;
	}
}
