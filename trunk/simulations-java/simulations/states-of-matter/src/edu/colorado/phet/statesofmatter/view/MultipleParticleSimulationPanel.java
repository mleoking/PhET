package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetRootPNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.RectangularParticleContainer;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;

public class MultipleParticleSimulationPanel extends PhetPCanvas {
    private ParticleContainerNode particleContainer;

    public MultipleParticleSimulationPanel() {
        particleContainer = new ParticleContainerNode(new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS));

        addWorldChild(particleContainer);

        addComponentListener(new LayoutAdjustingComponentListener());

        performLayout();
    }

    private void performLayout() {
        PhetRootPNode phetRootPNode = getPhetRootNode();


        phetRootPNode.translateWorld(getBounds().getWidth() / 2.0, getBounds().getHeight() / 2.0);
        phetRootPNode.scaleWorldAboutPoint(35, new Point2D.Double(0, 0));

    }

    public ParticleContainerNode getParticleContainer() {
        return particleContainer;
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
