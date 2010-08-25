/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;

/**
 * A membrane channel that can be configured at construction with respect
 * to its appearance, its open/closed behavior, and the particle type that may
 * pass through it.
 * 
 * @author John Blanco
 */
public class GenericMembraneChannel extends MembraneChannel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CHANNEL_HEIGHT = MembraneDiffusionModel.getMembraneThickness() * 1.2; // In nanometers.
	private static final double CHANNEL_WIDTH = MembraneDiffusionModel.getMembraneThickness() * 0.50; // In nanometers.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private final Color channelColor;
	private final Color edgeColor;
	private final ParticleType particleTypeToCapture;
	private final MembraneChannelOpennessStrategy opennessStrategy;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
    public GenericMembraneChannel(IParticleCapture modelContainingParticles, ParticleType particleTypeToCapture,
	        Color channelColor, Color edgeColor, MembraneChannelOpennessStrategy opennessStrategy,
	        Point2D initialPositon) {
	    
	    super(CHANNEL_WIDTH, CHANNEL_HEIGHT, modelContainingParticles);
	    
	    this.particleTypeToCapture = particleTypeToCapture;
	    this.channelColor = channelColor;
	    this.edgeColor = edgeColor;
	    this.opennessStrategy = opennessStrategy;
	    
	    // Set up the capture zones that define where particles may be
	    // captured in order to move them across the membrane.
		setUpperCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 4, -Math.PI / 2, Math.PI * 0.3));
		setLowerCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 4, Math.PI / 2, Math.PI * 0.3));
		
		// Initialize the openness level.
		setOpenness( opennessStrategy.getOpenness() );
		
		// Initialize the position.
		setCenterLocation( initialPositon );
		
		// Listen for updates to the openness.
		opennessStrategy.addListener( new MembraneChannelOpennessStrategy.Listener() {
            public void opennessChanged() {
                setOpenness( GenericMembraneChannel.this.opennessStrategy.getOpenness() );
            }
        });
	}
	
	public GenericMembraneChannel(IParticleCapture modelContainingParticles, ParticleType particleTypeToCapture,
	        Color channelColor, Color edgeColor, MembraneChannelOpennessStrategy opennessStrategy) {

	    this(modelContainingParticles, particleTypeToCapture, channelColor, edgeColor, opennessStrategy, 
	            new Point2D.Double(0, 0));
	}

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	/**
	 * Static factory method for creating a channel when given a channel type ID.
	 */
	public static MembraneChannel createChannel( MembraneChannelTypes channelType, IParticleCapture modelContainingParticles ){

	    Color channelColor, edgeColor;
	    ParticleType particleType;
	    MembraneChannelOpennessStrategy opennessStrategy;

	    switch ( channelType ){
	    case POTASSIUM_GATED_CHANNEL:
	        channelColor = MembraneDiffusionConstants.POTASSIUM_GATED_CHANNEL_COLOR;
	        edgeColor = MembraneDiffusionConstants.POTASSIUM_GATED_EDGE_COLOR;
	        particleType = ParticleType.POTASSIUM_ION;
	        opennessStrategy = new ChannelAlwaysClosedStrategy();
	        break;

	    case POTASSIUM_LEAKAGE_CHANNEL:
	        channelColor = MembraneDiffusionConstants.POTASSIUM_LEAKAGE_CHANNEL_COLOR;
	        edgeColor = MembraneDiffusionConstants.POTASSIUM_LEAKAGE_EDGE_COLOR;
	        particleType = ParticleType.POTASSIUM_ION;
	        opennessStrategy = new ChannelAlwaysOpenStrategy();
	        break;

	    case SODIUM_GATED_CHANNEL:
	        channelColor = MembraneDiffusionConstants.SODIUM_GATED_CHANNEL_COLOR;
	        edgeColor = MembraneDiffusionConstants.SODIUM_GATED_EDGE_COLOR;
	        particleType = ParticleType.SODIUM_ION;
	        opennessStrategy = new ChannelAlwaysClosedStrategy();
	        break;

	    case SODIUM_LEAKAGE_CHANNEL:
	        channelColor = MembraneDiffusionConstants.SODIUM_LEAKAGE_CHANNEL_COLOR;
	        edgeColor = MembraneDiffusionConstants.SODIUM_LEAKAGE_EDGE_COLOR;
	        particleType = ParticleType.SODIUM_ION;
	        opennessStrategy = new ChannelAlwaysOpenStrategy();
	        break;

	    default:
	        System.err.println( GenericMembraneChannel.class.getCanonicalName() + " - Error: Unknown channel type." );
	        assert false;
	        channelColor = Color.white;;
	        edgeColor = Color.pink;
	        particleType = ParticleType.POTASSIUM_ION;
	        opennessStrategy = new ChannelAlwaysOpenStrategy();
	        break;

	    }
	    
	    return new GenericMembraneChannel( modelContainingParticles, particleType, channelColor, edgeColor,
	            opennessStrategy );
	}

	@Override
	public Color getChannelColor() {
		return channelColor;
	}

    @Override
	public Color getEdgeColor() {
		return edgeColor;
	}

	@Override
	protected ParticleType getParticleTypeToCapture() {
		return particleTypeToCapture;
	}
	
    public MembraneChannelOpennessStrategy getOpennessStrategy() {
        return opennessStrategy;
    }
}
