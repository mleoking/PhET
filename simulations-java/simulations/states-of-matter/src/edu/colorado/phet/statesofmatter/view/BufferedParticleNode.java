package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.umd.cs.piccolo.nodes.PImage;

public class BufferedParticleNode extends ParticleNode {

	private PImage m_sphereImage;
	
	public BufferedParticleNode(StatesOfMatterAtom particle,
			ModelViewTransform mvt) {
		super(particle, mvt);
	}

	protected Paint choosePaint(StatesOfMatterAtom atom) {
		
		Color baseColor = super.chooseColor(atom);
		
        double atomRadius = atom.getRadius();
        
        return ( new RoundGradientPaint( atomRadius, -atomRadius, Color.WHITE,
                new Point2D.Double( -atomRadius, atomRadius ), baseColor ) );
	}

	protected void handleParticleRadiusChanged() {
		super.handleParticleRadiusChanged();
    	m_sphereImage.setImage( getSphere().toImage() );
    	m_sphereImage.setOffset(-getParticle().getRadius(), -getParticle().getRadius());
	}

	protected void initGraphics() {
    	// Since the gradient is so computationally intensive to draw, use
    	// an image.
    	m_sphereImage = new PImage( getSphere().toImage() );
    	m_sphereImage.setOffset(-getParticle().getRadius(), -getParticle().getRadius());
    	addChild( m_sphereImage );
	}
}
