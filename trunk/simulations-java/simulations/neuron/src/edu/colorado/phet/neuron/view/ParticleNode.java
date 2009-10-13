package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.neuron.model.Particle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that represents atoms in the view.
 */
public class ParticleNode extends PNode {
	
	private static final float STROKE_WIDTH = 1;
	private static final Stroke PARTICLE_EDGE_STROKE = new BasicStroke(STROKE_WIDTH);
	
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
        // TODO: On 10/12/2009, I (jblanco) talked with Noah P and we decided
        // to try removing the labels, since they are small and hard to see
        // already and we want to try to put more smaller particles in place.
        // If this decision sticks, the label should be permanently removed.
        //addChild(label);
        
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
    	double size;

    	switch (particle.getType()){
    	case SODIUM_ION:
    		/* The following code is what the code SHOULD be, but it ends up
    		 * getting the edges cut off because of, I think, some bug in
    		 * piccolo where the 'toImage' call doesn't properly account for
    		 * the stroke.  So, below this is a workaround.
    		SphericalNode sphereRepresentation = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter()), 
    				particle.getRepresentationColor(), false);
    		sphereRepresentation.setStroke(PARTICLE_EDGE_STROKE);
    		sphereRepresentation.setStrokePaint(Color.BLACK);
    		representation = sphereRepresentation;
    		 */
    		// Workaround code - instead of having a stroke, have a larger
    		// black image behind the main one.
    		double diameter = modelViewTransform.modelToViewDifferentialXDouble(particle.getDiameter());
    		PNode compositeCircleNode = new PNode();
    		PhetPPath sphereRepresentation = new PhetPPath( 
    				new Ellipse2D.Double(-diameter/2 + STROKE_WIDTH, -diameter/2 + STROKE_WIDTH, 
    						diameter - STROKE_WIDTH * 2, diameter - STROKE_WIDTH * 2), 
    				particle.getRepresentationColor());
    				
    		PhetPPath sphereBackground = new PhetPPath( 
    				new Ellipse2D.Double(-diameter/2, -diameter/2, diameter, diameter), Color.BLACK);
    		compositeCircleNode.addChild(sphereBackground);
    		compositeCircleNode.addChild(sphereRepresentation);
    		representation = compositeCircleNode;
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
    }
}
