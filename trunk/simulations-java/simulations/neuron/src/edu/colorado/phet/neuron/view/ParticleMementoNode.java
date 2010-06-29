/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.neuron.model.PlaybackParticle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class that represents particle mementos in the view.
 */
public class ParticleMementoNode extends PNode {
	
	private static final float STROKE_WIDTH = 1;
	private static final Stroke PARTICLE_EDGE_STROKE = new BasicStroke(STROKE_WIDTH);
	
	private PlaybackParticle particleMemento;
    private ModelViewTransform2D modelViewTransform;
    private PNode representation;

    public ParticleMementoNode( PlaybackParticle particleMemento, ModelViewTransform2D modelViewTransform ) {
    	
		this.particleMemento = particleMemento;
        this.modelViewTransform = modelViewTransform;

        // Create the shape that represents this particle.
        representation = createRepresentation();
		addChild( representation );
        updateOffset();
        updateOpaqueness();
	}
    
    private void updateOffset() {
        setOffset( modelViewTransform.modelToView( particleMemento.getPosition() ));
    }
    
    private void updateOpaqueness() {
    	setTransparency((float)(particleMemento.getOpaqueness()));
    }
    
	/**
     * Create the shape that will be used to represent this particular
     * particle.  This was created when we realized that many textbooks use
     * different shapes for different chemicals, rather than always a sphere.
     * 
     * @return
     */
    private PNode createRepresentation() {
    	PNode representation;
    	double size;

    	switch (particleMemento.getParticleType()){
    	case SODIUM_ION:
    		SphericalNode sphereRepresentation = new SphericalNode( 
    		        modelViewTransform.modelToViewDifferentialXDouble(particleMemento.getRadius() * 2), 
    				particleMemento.getRepresentationColor(), false);
    		sphereRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		sphereRepresentation.setStrokePaint(Color.BLACK);
    		representation = sphereRepresentation;
    		break;
    		
    	case POTASSIUM_ION:
    		size = modelViewTransform.modelToViewDifferentialXDouble(particleMemento.getRadius()) * 1.7;
    		PPath diamondRepresentation = new PPath( new Rectangle2D.Double(-size/2, -size/2, size, size));
    		diamondRepresentation.setPaint(particleMemento.getRepresentationColor());
    		diamondRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		diamondRepresentation.setStrokePaint(Color.BLACK);
    		diamondRepresentation.rotate(Math.PI / 4);
    		representation = diamondRepresentation;
    		break;
    		
    	default:
    		System.err.println(getClass().getName() + " - Warning: No specific shape for this particle type, defaulting to sphere.");
    		representation = new SphericalNode( 
    		        modelViewTransform.modelToViewDifferentialXDouble(particleMemento.getRadius() * 2), 
    				particleMemento.getRepresentationColor(), true);
    		break;
    	}
    	
    	return representation;
    }
}
