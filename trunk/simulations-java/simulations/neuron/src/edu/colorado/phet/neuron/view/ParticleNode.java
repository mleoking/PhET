/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.neuron.model.IViewableParticle;
import edu.colorado.phet.neuron.model.ParticleListenerAdapter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class that represents particles (generally ions) in the view.
 */
public class ParticleNode extends PNode {
	
	private static final float STROKE_WIDTH = 1;
	private static final Stroke PARTICLE_EDGE_STROKE = new BasicStroke(STROKE_WIDTH);
	
	private IViewableParticle particle;
    private ModelViewTransform2D modelViewTransform;
    private PPath representation;

    public ParticleNode( IViewableParticle particle, ModelViewTransform2D modelViewTransform ) {
    	
		this.particle = particle;
        this.modelViewTransform = modelViewTransform;
        
        // Listen to the particle for things that we care about.
        particle.addListener(new ParticleListenerAdapter() {
			public void positionChanged() {
				updateOffset();
			}
			public void appearanceChanged() {
			    updateRepresentation();
			}
		});

        // Create the initial representation with the aspects that don't change.
        representation = new PhetPPath(PARTICLE_EDGE_STROKE, Color.BLACK);
        addChild( representation );

        updateOffset();
        updateRepresentation();
	}
    
    private void updateOffset() {
        setOffset( modelViewTransform.modelToView( particle.getPosition() ));
    }
    
    private void updateRepresentation(){
        double size;
        Shape representationShape;

        switch (particle.getType()){
        case SODIUM_ION:
            double transformedRadius = modelViewTransform.modelToViewDifferentialXDouble(particle.getRadius());
            representationShape = new Ellipse2D.Double(-transformedRadius, -transformedRadius, transformedRadius * 2,
                    transformedRadius * 2);
            break;
            
        case POTASSIUM_ION:
            size = modelViewTransform.modelToViewDifferentialXDouble(particle.getRadius() * 2) * 0.85;
            representationShape = new Rectangle2D.Double(-size/2, -size/2, size, size);
            representationShape = 
                AffineTransform.getRotateInstance( Math.PI / 4 ).createTransformedShape( representationShape );
            break;
            
        default:
            System.err.println(getClass().getName() + " - Warning: No specific shape for this particle type, defaulting to sphere.");
            double defaultSphereRadius = modelViewTransform.modelToViewDifferentialXDouble(particle.getRadius());
            representationShape = new Ellipse2D.Double(-defaultSphereRadius, -defaultSphereRadius,
                    defaultSphereRadius * 2, defaultSphereRadius * 2);
            break;
        }

        representation.setPathTo( representationShape );
        representation.setPaint( particle.getRepresentationColor() );
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
}
