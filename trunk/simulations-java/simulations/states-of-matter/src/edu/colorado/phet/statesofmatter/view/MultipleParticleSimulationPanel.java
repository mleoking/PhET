package edu.colorado.phet.statesofmatter.view;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetRootPNode;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

public class MultipleParticleSimulationPanel extends PhetPCanvas {
    public static final MultipleParticleSimulationPanel TEST = new MultipleParticleSimulationPanel(MultipleParticleModel.TEST, ConstantDtClock.TEST);

    private ParticleContainerNode particleContainer;
    private MultipleParticleModel model;
    private boolean layoutPerformed=false;

    public MultipleParticleSimulationPanel(MultipleParticleModel model, IClock clock) {
        this.model = model;
        setZoomEventHandler( new PZoomEventHandler() );
        try {
            particleContainer = new ParticleContainerNode(this, model.getParticleContainer());
        }
        catch (IOException e) {
            throw new RuntimeException();
        }

        clock.addClockListener(new ViewUpdatingClockListener());

        addWorldChild(particleContainer);

        addComponentListener(new LayoutAdjustingComponentListener());

        performLayout();

        for ( int i = 0; i < model.getParticles().size(); i++ ) {
            particleContainer.addParticleNode(new ParticleNode(model.getParticle(i)));
        }
    }

    private void performLayout() {
        PhetRootPNode phetRootPNode = getPhetRootNode();

        phetRootPNode.translateWorld(getBounds().getWidth() / 2.0, getBounds().getHeight() / 2.0);
        phetRootPNode.scaleWorldAboutPoint(35, new Point2D.Double(0, 0));
        layoutPerformed=true;
   }

    public ParticleContainerNode getParticleContainer() {
        return particleContainer;
    }

    public int getNumParticles() {
        return model.getParticles().size();
    }

    public StatesOfMatterParticle getParticle(int i) {
        return model.getParticle(i);
    }

    /**
     * Returns the node associated with the ith particle.
     *
     * @param i The index.
     *
     * @return  The particle node.
     */
    public PNode getParticleNode(int i) {
        return particleContainer.getParticleNode(i);
    }

    public boolean isLayoutPerformed() {
        return layoutPerformed;
    }

    private class LayoutAdjustingComponentListener implements ComponentListener {
        public void componentResized(ComponentEvent componentEvent) {
            performLayout();
        }

        public void componentMoved(ComponentEvent componentEvent) {
        }

        public void componentShown(ComponentEvent componentEvent) {
            performLayout();
        }

        public void componentHidden(ComponentEvent componentEvent) {
        }
    }

    private class ViewUpdatingClockListener extends ClockAdapter {
        public void clockTicked(ClockEvent clockEvent) {
            update();
        }
    }
}
