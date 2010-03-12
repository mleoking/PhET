/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

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
	private static final double MIN_INTER_CAPTURE_TIME = 0.00005; // In seconds of sim time.
	private static final double MAX_INTER_CAPTURE_TIME = 0.00020; // In seconds of sim time.
	
	// Constant used when calculating how open this gate should be based on
	// a value that exists within the Hodgkin-Huxley model.  This was
	// empirically determined.
	private static final double M3H_WHEN_FULLY_OPEN = 0.25; 
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private IHodgkinHuxleyModel hodgkinHuxleyModel;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	public SodiumDualGatedChannel(IHodgkinHuxleyModel hodgkinHuxleyModel, IParticleCapture modelContainingParticles) {
		
		super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
		this.hodgkinHuxleyModel = hodgkinHuxleyModel;
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
		double openness = Math.min(Math.abs(hodgkinHuxleyModel.get_m3h())/M3H_WHEN_FULLY_OPEN, 1);
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

	// For testing, can be removed with main routine when it goes.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );

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
        SodiumDualGatedChannel sodiumDualGatedChannel = new SodiumDualGatedChannel(null, null);
        sodiumDualGatedChannel.setRotationalAngle(Math.PI / 2);
        MembraneChannelNode channelNode = new MembraneChannelNode(sodiumDualGatedChannel, mvt);
        
        // Add node(s) to the canvas.
        canvas.addWorldChild(nonMvtTestNode);
        canvas.addWorldChild(channelNode);

        // Create the frame and put the canvas in it.
		JFrame frame = new JFrame();
		frame.setSize(INITIAL_INTERMEDIATE_DIMENSION);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setContentPane(canvas);
		frame.setVisible(true);
		
		// Put the channel through its paces.
		sodiumDualGatedChannel.setOpenness(0.5);
	}
}
