package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.Particle;
import edu.umd.cs.piccolo.PNode;

public class ParticleNode extends PNode {
	
	Particle particle;
	
	public ParticleNode( Particle particle ) {
		this.particle = particle;
		
		particle.addListener(new Particle.Listener() {
			public void positionChanged() {
				update();
			}

		});
		
		PhetPPath representation = new PhetPPath(new Ellipse2D.Double(-5, -5, 10, 10), Color.ORANGE);
		addChild(representation);
	}

	private void update() {
		setOffset( particle.getPositionReference() );
	}
}
