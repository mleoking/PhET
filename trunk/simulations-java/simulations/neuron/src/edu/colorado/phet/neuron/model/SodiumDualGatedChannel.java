/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.colorado.phet.neuron.utils.MathUtils;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

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
	
	private static final double CHANNEL_HEIGHT = AxonMembrane.MEMBRANE_THICKNESS * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = AxonMembrane.MEMBRANE_THICKNESS * 0.50; // In nanometers.
	
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
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	private GateState gateState = GateState.IDLE;
	private double previousNormalizedConductance;
	private double stateTransitionTimer = 0;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	
	public SodiumDualGatedChannel(IHodgkinHuxleyModel hodgkinHuxleyModel, IParticleCapture modelContainingParticles) {
		
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		this.hodgkinHuxleyModel = hodgkinHuxleyModel;
		setCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 5, 0, 0, Math.PI * 0.8));
		setMinInterCaptureTime(MIN_INTER_CAPTURE_TIME);
		setMaxInterCaptureTime(MAX_INTER_CAPTURE_TIME);
		
		// Initialize some internal state.
		if (hodgkinHuxleyModel != null){
			previousNormalizedConductance = calculateNormalizedConductance();
			assert previousNormalizedConductance < ACTIVATION_DECISION_THRESHOLD;
		}
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
				setOpenness(normalizedConductance);
			}
			break;
			
		case OPENING:
			if (isOpen() && getCaptureCountdownTimer() == Double.POSITIVE_INFINITY){
				// We are open enought to start capturing particles.
				restartCaptureCountdownTimer();
			}
			if (previousNormalizedConductance  > normalizedConductance){
				// We are on the way down, so set a new state.
				gateState = GateState.BECOMING_INACTIVE;
				// Should be fully open at this point.
				setOpenness(1);
			}
			else{
				setOpenness(normalizedConductance);
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
				// their idle (i.e. resting) states.
				setOpenness(stateTransitionTimer/RESETTING_TO_IDLE_TIME);
				setInactivationAmt(stateTransitionTimer/RESETTING_TO_IDLE_TIME);
			}
			else{
				// Go back to the idle, or resting, state.
				setOpenness(0);
				setInactivationAmt(0);
				gateState = GateState.IDLE;
			}
			break;
		}
		
		// Save values for the next time through.
		previousNormalizedConductance = normalizedConductance;
	}
	
	@Override
	public void createAndSetTraversalMotionStrategy(Particle particle, double maxVelocity) {
		particle.setMotionStrategy(new DualGateChannelTraversalMotionStrategy(this, particle.getPositionReference()));
	}

	/**
	 * This membrane channel has an inactivation gate.
	 */
	@Override
	public boolean getHasInactivationGate() {
		return true;
	}

	private double calculateNormalizedConductance(){
		return Math.min(Math.abs(hodgkinHuxleyModel.get_m3h())/M3H_WHEN_FULLY_OPEN, 1);
	}

	@Override
	protected ParticleType getParticleTypeToCapture() {
		return ParticleType.SODIUM_ION;
	}
	
	@Override
	public void reset() {
		gateState = GateState.IDLE;
		stateTransitionTimer = 0;
		previousNormalizedConductance = calculateNormalizedConductance();
		setOpenness(0);
		setInactivationAmt(0);
	}

    //----------------------------------------------------------------------------
    // Test Harness
    //----------------------------------------------------------------------------

	// For testing, can be removed with main routine when it goes.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );

    
    /**
     * Test harness.
     * 
     * @param args
     */
	public static void main(String[] args) {
		
    	ConstantDtClock clock = new ConstantDtClock( NeuronDefaults.CLOCK_FRAME_RATE, NeuronDefaults.CLOCK_DT );
    	
    	// Set up the model-canvas transform.
        ModelViewTransform2D mvt = new ModelViewTransform2D( 
        		new Point2D.Double(0, 0),
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.5 )),
        		20,  // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        		true);

        // Create the canvas.
        PhetPCanvas canvas = new PhetPCanvas();
    	canvas.setWorldTransformStrategy(new PhetPCanvas.CenteringBoxStrategy(canvas, INITIAL_INTERMEDIATE_DIMENSION));
    	canvas.setBackground( NeuronConstants.CANVAS_BACKGROUND );
        
        // Create a non-transformed test node.
        PNode nonMvtTestNode = new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.yellow);

        // Create the channel node.
        IParticleCapture particleCapture = new IParticleCapture() {
			public void requestParticleThroughChannel(ParticleType particleType,
					MembraneChannel membraneChannel, double maxVelocity) {
				// Do nothing.
			}
		};
		final IHodgkinHuxleyModel hhModel = new ModifiedHodgkinHuxleyModel();
        final SodiumDualGatedChannel sodiumDualGatedChannel = new SodiumDualGatedChannel(hhModel, particleCapture);
        sodiumDualGatedChannel.setRotationalAngle(Math.PI / 2);
        MembraneChannelNode channelNode = new MembraneChannelNode(sodiumDualGatedChannel, mvt);
        
        // Add node(s) to the canvas.
        canvas.addWorldChild(nonMvtTestNode);
        canvas.addWorldChild(channelNode);
        
        // Create and add a node for initiating a stimulation.
        JButton stimButton = new JButton("Stimulate");
        stimButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				hhModel.stimulate();
			}
		});
        PSwing stimButtonPSwing = new PSwing(stimButton);
        stimButtonPSwing.setOffset(20, 0);
        canvas.addScreenChild(stimButtonPSwing);

        // Create the frame and put the canvas in it.
		JFrame frame = new JFrame();
		frame.setSize(INITIAL_INTERMEDIATE_DIMENSION);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setContentPane(canvas);
		frame.setVisible(true);
		
		// Put the channel through its paces.
		clock.addClockListener(new ClockAdapter(){
		    public void clockTicked( ClockEvent clockEvent ) {
		    	hhModel.stepInTime(clockEvent.getSimulationTimeChange());
		    	sodiumDualGatedChannel.stepInTime(clockEvent.getSimulationTimeChange());
		    }
		});
		
		clock.start();
	}
}
