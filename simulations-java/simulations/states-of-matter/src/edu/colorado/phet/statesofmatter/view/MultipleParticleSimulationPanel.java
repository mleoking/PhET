package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetRootPNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.RectangularParticleContainer;
import edu.umd.cs.piccolo.PNode;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;

public class MultipleParticleSimulationPanel extends PhetPCanvas {
    private ParticleContainerNode particleContainer;
    private MultipleParticleModel model;
    public static final MultipleParticleSimulationPanel TEST = new MultipleParticleSimulationPanel(MultipleParticleModel.TEST);
    private boolean layoutPerformed=false;

    public MultipleParticleSimulationPanel(MultipleParticleModel model) {
        this.model = model;
        particleContainer = new ParticleContainerNode(new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS));

        addWorldChild(particleContainer);

        addComponentListener(new LayoutAdjustingComponentListener());

        performLayout();
    }

    private void performLayout() {
        PhetRootPNode phetRootPNode = getPhetRootNode();

        phetRootPNode.translateWorld(getBounds().getWidth() / 2.0, getBounds().getHeight() / 2.0);
        phetRootPNode.scaleWorldAboutPoint(35, new Point2D.Double(0, 0));
        layoutPerformed=true;
        System.out.println("particleContainer.getGlobalFullBounds() = " + particleContainer.getGlobalFullBounds());
    }

    public ParticleContainerNode getParticleContainer() {
        return particleContainer;
    }

    public int getNumParticles() {
        return model.getParticles().size();
    }

    /**
     * Returns the node associated with the ith particle.
     * @param i
     * @return
     */
    public PNode getParticleNode(int i) {
        return new PNode();
    }

    public boolean isLayoutPerformed() {
        return layoutPerformed;
    }

    private class LayoutAdjustingComponentListener implements ComponentListener {
        public void componentResized(ComponentEvent componentEvent) {
            performLayout();
            System.out.println("componentEvent.getComponent().getBounds() = " + componentEvent.getComponent().getBounds());
        }

        public void componentMoved(ComponentEvent componentEvent) {
        }

        public void componentShown(ComponentEvent componentEvent) {
            performLayout();
        }

        public void componentHidden(ComponentEvent componentEvent) {
        }
    }
}
