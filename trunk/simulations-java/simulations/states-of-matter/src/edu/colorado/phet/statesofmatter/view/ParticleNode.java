package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.statesofmatter.model.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ParticleNode extends PNode {
    private StatesOfMatterParticle particle;
    private PhetPPath path;

    public ParticleNode(StatesOfMatterParticle particle) {
        this.particle = particle;
        this.path=new PhetPPath(Color.blue);
        addChild(path);
        update();
    }

    private void update() {
        path.setPathTo(new Ellipse2D.Double(particle.getX(), particle.getY(), 1,1));
    }
}
