package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.Particle;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;

public class ParticleNode extends PNode {
	
	Particle particle;
    private ModelViewTransform2D transform;
    private PhetPPath representation;

    public ParticleNode( Particle particle, ModelViewTransform2D transform ) {
		this.particle = particle;
        this.transform = transform;

        particle.addListener(new Particle.Listener() {
			public void positionChanged() {
				updateOffset();
			}

		});

        representation = new PhetPPath( getShape(), Color.ORANGE);
		addChild( representation );
        update();
	}

    private Shape getShape() {
        double r = NeuronDefaults.CROSS_SECTION_RADIUS ;
        Ellipse2D.Double modelShape = new Ellipse2D.Double( 0,0,
                                                            2 * r, 2 * r );
        Shape transformedShape = transform.createTransformedShape( modelShape );
        System.out.println( "transformedShape.getBounds = " + transformedShape.getBounds() );
        return AffineTransform.getTranslateInstance( -transformedShape.getBounds2D().getWidth()/2,-transformedShape.getBounds2D().getHeight()/2 ).createTransformedShape( transformedShape );
    }

    private void update() {
        updateShape();
        updateOffset();
	}

    private void updateOffset() {
        representation.setOffset( transform.modelToView( particle.getPosition() ));
//        representation.setOffset( particle.getPosition());
    }

    private void updateShape() {
        representation.setPathTo( getShape() );
    }
}
