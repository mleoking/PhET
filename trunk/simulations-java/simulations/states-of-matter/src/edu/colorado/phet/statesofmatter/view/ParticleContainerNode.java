package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class ParticleContainerNode extends PNode {
    private final ParticleContainer container;
    private final PPath path = new PPath();

    public ParticleContainerNode(ParticleContainer container) {
        super();

        this.container = container;

        addChild(path);

        update();
    }

    public void update() {
        path.setPathTo(container.getShape());
    }
}
