/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.membranediffusion.model.Particle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class that represents particles (generally ions) in the view.
 */
public class ParticleNode extends PNode {
	
	private static final float STROKE_WIDTH = 1;
	private static final Stroke PARTICLE_EDGE_STROKE = new BasicStroke(STROKE_WIDTH);
	
	private Particle particle;
    private ModelViewTransform2D modelViewTransform;
    private PNode representation;

    public ParticleNode( Particle particle, ModelViewTransform2D modelViewTransform ) {
    	
		this.particle = particle;
        this.modelViewTransform = modelViewTransform;

        particle.addListener(new Particle.Adapter() {
			public void positionChanged() {
				updateOffset();
			}
			public void opaquenessChanged() {
				updateOpaqueness();
			}
		});

        // Create the shape that represents this particle.
        representation = createRepresentation();
		addChild( representation );
        updateOffset();
        updateOpaqueness();
	}
    
    private void updateOffset() {
        setOffset( modelViewTransform.modelToView( particle.getPosition() ));
    }
    
    private void updateOpaqueness() {
    	setTransparency((float)(particle.getOpaqueness()));
    }
    
    /**
     * This override is here as a workaround for an issue where the edges of
     * the representation were being cut off when converted to an image.  This
     * is a known bug with PPath.getBounds.  This might need to be removed if
     * the bug in PPath is ever fixed.
     */
    @Override
	public Image toImage() {
   		PPath parentNode = new PPath();
   		parentNode.addChild(this);
   		parentNode.setPaint(new Color(0, 0, 0, 0));
   		parentNode.setStroke(null);
   		double pad = 2;
   		parentNode.setPathTo(new Rectangle2D.Double(this.getFullBoundsReference().x - pad,
   				this.getFullBoundsReference().y - pad,
   				this.getFullBoundsReference().width + pad * 2 + STROKE_WIDTH / 2,
   				this.getFullBoundsReference().height + pad * 2 + STROKE_WIDTH / 2));
   		
   		return parentNode.toImage();
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

    	switch (particle.getType()){
    	case SODIUM_ION:
    		SphericalNode sphereRepresentation = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter()), 
    				particle.getRepresentationColor(), false);
    		sphereRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		sphereRepresentation.setStrokePaint(Color.BLACK);
    		representation = sphereRepresentation;
    		break;
    		
    	case POTASSIUM_ION:
    		size = modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter()) * 0.85;
    		PPath diamondRepresentation = new PPath( new Rectangle2D.Double(-size/2, -size/2, size, size));
    		diamondRepresentation.setPaint(particle.getRepresentationColor());
    		diamondRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		diamondRepresentation.setStrokePaint(Color.BLACK);
    		diamondRepresentation.rotate(Math.PI / 4);
    		representation = diamondRepresentation;
    		break;
    		
    	case PROTEIN_ION:
    		size = modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter());
    		double ovalWidth = size * 1.5;
    		double ovalHeight = size * 0.8;
    		PPath ovalRepresentation = 
    			new PPath( new Ellipse2D.Double(-ovalWidth/2, -ovalHeight/2, ovalWidth, ovalHeight));
    		ovalRepresentation.setPaint(particle.getRepresentationColor());
    		ovalRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		ovalRepresentation.setStrokePaint(Color.BLACK);
    		representation = ovalRepresentation;
    		break;
    		
    	default:
    		System.err.println(getClass().getName() + " - Warning: No specific shape for this particle type, defaulting to sphere.");
    		representation = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter()), 
    				particle.getRepresentationColor(), true);
    		break;
    	}
    	
    	return representation;
    	// TODO: For testing - comment out other return and use this to
    	// convert to an image.
//    	return new PImage(representation.toImage());
    }
}
