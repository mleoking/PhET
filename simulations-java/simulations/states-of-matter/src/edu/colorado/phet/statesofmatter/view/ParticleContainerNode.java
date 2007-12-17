package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.SVGNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class ParticleContainerNode extends PhetPNode {
    private final ParticleContainer container;

    public ParticleContainerNode(PhetPCanvas canvas, ParticleContainer container) throws IOException {
        super();

        this.container = container;
        SVGNode node = new SVGNode(canvas, StatesOfMatterConfig.RESOURCES.getResourceAsStream("images/" + StatesOfMatterConfig.COFFEE_CUP_IMAGE), 1, 1);

        node.translate(-0.35, -0.4);

        addChild(node);

        update();
    }

    public void update() {
        Rectangle2D b = container.getShape().getBounds2D();

        double scaleX = b.getWidth()  * 1.2;
        double scaleY = b.getHeight() * 1.2;

        resetTransformToIdentity();
        transformBy(AffineTransform.getScaleInstance(scaleX, scaleY));
    }
}