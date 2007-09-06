package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.model.AbstractParticleContainer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class ParticleContainerNode extends PNode {
    private final AbstractParticleContainer container;
    private final PPath path = new PPath();

    public ParticleContainerNode(AbstractParticleContainer container) {
        super();

        this.container = container;

        addChild(path);

        update();
    }

    public void update() {
        path.setPathTo(container.getShape());
    }
}
