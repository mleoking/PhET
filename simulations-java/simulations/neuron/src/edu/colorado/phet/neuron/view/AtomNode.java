package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.Atom;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;

public class AtomNode extends PNode {
	
	Atom particle;
    private ModelViewTransform2D modelViewTransform;
    private PhetPPath representation;

    public AtomNode( Atom particle, ModelViewTransform2D modelViewTransform ) {
		this.particle = particle;
        this.modelViewTransform = modelViewTransform;

        particle.addListener(new Atom.Listener() {
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
        Shape transformedShape = modelViewTransform.createTransformedShape( modelShape );
        System.out.println( "transformedShape.getBounds = " + transformedShape.getBounds() );
        return AffineTransform.getTranslateInstance( -transformedShape.getBounds2D().getWidth()/2,-transformedShape.getBounds2D().getHeight()/2 ).createTransformedShape( transformedShape );
    }

    private void update() {
        updateShape();
        updateOffset();
	}

    private void updateOffset() {
        representation.setOffset( modelViewTransform.modelToView( particle.getPosition() ));
//        representation.setOffset( particle.getPosition());
    }

    private void updateShape() {
        representation.setPathTo( getShape() );
    }
}
