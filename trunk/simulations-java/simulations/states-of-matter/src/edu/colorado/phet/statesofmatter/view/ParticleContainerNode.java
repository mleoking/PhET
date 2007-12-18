package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.SVGNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ParticleContainerNode extends PhetPNode {
    private final ParticleContainer container;
    private PNode particleLayer = new PNode();
    private SVGNode svgNode;
    private double containerWidth;
    private double containerHeight;

    private static final double CUP_OVERSIZE = 1.2;
    private static final double CUP_X_OFFSET = 1.6;
    private static final double CUP_Y_OFFSET = 0.6;

    public ParticleContainerNode(PhetPCanvas canvas, ParticleContainer container) throws IOException {
        super();

        this.container       = container;
        this.containerWidth  = StatesOfMatterConfig.CONTAINER_BOUNDS.getWidth()  * CUP_OVERSIZE;
        this.containerHeight = StatesOfMatterConfig.CONTAINER_BOUNDS.getHeight() * CUP_OVERSIZE;
        this.svgNode         = new SVGNode(canvas, StatesOfMatterConfig.RESOURCES.getResourceAsStream("images/" + StatesOfMatterConfig.COFFEE_CUP_IMAGE), containerWidth, containerHeight);

        addChild(svgNode);
        addChild(particleLayer);

        update();
    }

    public void update() {
        Rectangle2D b = container.getShape().getBounds2D();

        svgNode.resetTransformToIdentity();
        svgNode.translate(-containerWidth / 2.0 + b.getCenterX() + CUP_X_OFFSET, -containerHeight / 2.0 + b.getCenterY() + CUP_Y_OFFSET);
    }

    public List getParticleNodes() {
        return Collections.unmodifiableList(particleLayer.getChildrenReference());
    }

    public ParticleNode getParticleNode(int i) {
        return (ParticleNode)particleLayer.getChild(i);
    }

    public void addParticleNode(ParticleNode particleNode) {
        particleLayer.addChild(particleNode);
    }
}