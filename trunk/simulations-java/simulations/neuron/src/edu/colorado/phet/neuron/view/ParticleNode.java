package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.neuron.model.Particle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that represents atoms in the view.
 */
public class ParticleNode extends PNode {
	
	private static final Stroke PARTICLE_EDGE_STROKE = new BasicStroke(1);
	
	private Particle particle;
    private ModelViewTransform2D modelViewTransform;
    private PNode representation;
    private PText label;

    public ParticleNode( Particle particle, ModelViewTransform2D modelViewTransform ) {
    	
		this.particle = particle;
        this.modelViewTransform = modelViewTransform;

        particle.addListener(new Particle.Listener() {
			public void positionChanged() {
				updateOffset();
			}
		});

        // Create the shape that represents this particle.
        representation = createRepresentation();
		addChild( representation );
        updateOffset();
        
        // Create the label.
        String labelText = MessageFormat.format("{0}{1}", particle.getLabelText(), particle.getChargeString());
        label = new PText(labelText);
        label.setFont(new PhetFont(12, true));
        label.setTextPaint(particle.getLabelColor());
        addChild(label);
        
        // Scale the label to fit within the sphere.
        double maxLabelDimension = Math.max(label.getFullBoundsReference().width, label.getFullBoundsReference().height);
        PNode tempRepresentation = createRepresentation();
        tempRepresentation.rotate(Math.PI / 4);
        double minShapeDimension = Math.min(representation.getFullBoundsReference().width,
        		tempRepresentation.getFullBoundsReference().height);
        double scale = ( minShapeDimension * 1.1 / maxLabelDimension ) * 0.9;
        label.setScale(scale);
        
        // Center the label both vertically and horizontally.  This assumes
        // that the sphere is centered at 0,0.
        label.setOffset(-label.getFullBoundsReference().width / 2, -label.getFullBoundsReference().height / 2);
	}
    
    /**
     * Turn on/off the use of a stroke to draw the outline of the particle.  This
     * function was implemented as part of a workaround for an issue where the
     * stroke was being cut off when this node was used on the control panel,
     * resulting in an odd look.  If that problem is resolved (it seemed to be
     * deep in the bowels of Piccolo), then it may be possible to remove this
     * method.
     * 
     * @param strokeOn
     */
    public void setStrokeOn(boolean strokeOn){
		if (representation instanceof SphericalNode){
			if (strokeOn){
				((SphericalNode)representation).setStroke(PARTICLE_EDGE_STROKE);
			}
			else{
				((SphericalNode)representation).setStroke(null);
			}
		}
		else if (representation instanceof PPath){
			if (strokeOn){
				((PPath)representation).setStroke(PARTICLE_EDGE_STROKE);
			}
			else{
				((PPath)representation).setStroke(null);
			}
		}
    }

    private void updateOffset() {
        setOffset( modelViewTransform.modelToView( particle.getPosition() ));
    }
    
    /**
     * Create the shape that will be used to represent this particular .
     * This was created when we realized that many textbooks use different
     * shapes for different s, rather than always a sphere.
     * 
     * @return
     */
    private PNode createRepresentation() {
    	PNode representation;

    	switch (particle.getType()){
    	case SODIUM_ION:
    		SphericalNode sphereRepresentation = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter()), 
    				particle.getRepresentationColor(), true);
    		sphereRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		sphereRepresentation.setStrokePaint(Color.BLACK);
    		representation = sphereRepresentation;
    		break;
    		
    	case POTASSIUM_ION:
    		double size = modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter());
    		size = size * 0.85; // Scale down a bit so it is close to fitting within the diameter.
    		PPath diamondRepresentation = new PPath( new Rectangle2D.Double(-size/2, -size/2, size, size));
    		diamondRepresentation.setPaint(particle.getRepresentationColor());
    		diamondRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		diamondRepresentation.setStrokePaint(Color.BLACK);
    		diamondRepresentation.rotate(Math.PI / 4);
    		representation = diamondRepresentation;
    		break;
    		
    	default:
    		System.err.println(getClass().getName() + " - Warning: No specific shape for this particle type, defaulting to sphere.");
    		representation = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter()), 
    				particle.getRepresentationColor(), true);
    		break;
    	}
    	
    	return representation;
    }
}
