/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * A gated channel through which sodium passes when the channel is open.
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
		setUpperCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, -Math.PI / 2, Math.PI * 0.3));
		setLowerCaptureZone(new PieSliceShapedCaptureZone(getCenterLocation(), CHANNEL_WIDTH * 3.5, Math.PI / 2, Math.PI * 0.3));
		
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

	@Override
	public Color getChannelColor() {
		return channelColor;
	}

	@Override
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        opennessStrategy.stepInTime( dt );
    }

    @Override
	public Color getEdgeColor() {
		return edgeColor;
	}

	@Override
	protected ParticleType getParticleTypeToCapture() {
		return particleTypeToCapture;
	}
}
