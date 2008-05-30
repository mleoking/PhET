package edu.colorado.phet.statesofmatter.view;

import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import junit.framework.TestCase;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;

public class ZMultipleParticleSimulationPanelTester extends TestCase {
    private volatile MultipleParticleSimulationPanel panel;
    private volatile ConstantDtClock clock;

    public void setUp() throws InterruptedException, InvocationTargetException {
        clock = new ConstantDtClock(1, 1);

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                panel = new MultipleParticleSimulationPanel(new MultipleParticleModel(ConstantDtClock.TEST), clock);

                panel.setBounds(0, 0, 600, 600);
            }
        });
    }

    public void testLayoutPerformed() throws InvocationTargetException, InterruptedException {
        assertTrue("layout should have been performed by now", panel.isLayoutPerformed());
    }

    public void testThatParticleContainerIsCentered() throws InvocationTargetException, InterruptedException {
        ParticleContainerNode particleContainer = panel.getParticleContainer();

        PiccoloTestingUtils.testIsRoughlyCentered(particleContainer, panel);
    }

    public void testThatParticleContainerIsMediumSized() throws InvocationTargetException, InterruptedException {
        PiccoloTestingUtils.testIsMediumSized(panel.getParticleContainer(), panel);
    }

    public void testThatNumParticlesIsNonZero() {
        assertTrue(panel.getNumParticles() > 0);
    }

    public void testParticleNodesAreNotNull() {
        for (int i = 0; i < panel.getNumParticles(); i++ ) {
            assertNotNull(panel.getParticleNode(i));
        }
    }

    public void testParticlesVisible() throws InvocationTargetException, InterruptedException {
        for (int i = 0; i < panel.getNumParticles(); i++ ) {
            PiccoloTestingUtils.testIsVisible(panel.getParticleNode(i), panel);
        }
    }

    public void testParticleIdentityIsUnique() {
        Map identities = new IdentityHashMap();

        for (int i = 0; i < panel.getNumParticles(); i++) {
            PNode node = panel.getParticleNode(i);

            identities.put(node, node);
        }

        assertEquals(panel.getNumParticles(), identities.size());
    }

    public void testParticleIdentityIsConsistent() {
        PNode[] particles = new PNode[panel.getNumParticles()];

        for (int i = 0; i < panel.getNumParticles(); i++) {
            PNode node = panel.getParticleNode(i);

            particles[i] = node;
        }

        for (int i = 0; i < panel.getNumParticles(); i++) {
            PNode node = panel.getParticleNode(i);

            assertSame(particles[i], node);
        }
    }

    public void testParticlesAreSmallNonEmpty() throws InvocationTargetException, InterruptedException {
        for (int i = 0; i < panel.getNumParticles(); i++) {
            PiccoloTestingUtils.testIsSmallSized(panel.getParticleNode(i), panel);
            PiccoloTestingUtils.testBoundsAreNonZero(panel.getParticleNode(i));
        }
    }

    public void testParticleViewSynchedWithModel() {
        clock.start();

        StatesOfMatterParticle p = panel.getParticle(0);
        PNode pNode = panel.getParticleNode(0);

        PiccoloTestingUtils.testViewAutomaticallySyncsWithModel(p, pNode);
    }
}
