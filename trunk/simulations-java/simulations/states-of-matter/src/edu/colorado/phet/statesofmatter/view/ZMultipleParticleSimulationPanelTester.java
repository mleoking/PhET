package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import junit.framework.TestCase;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ZMultipleParticleSimulationPanelTester extends TestCase {
    private volatile MultipleParticleSimulationPanel panel;

    public void setUp() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {

                //panel = MultipleParticleSimulationPanel.TEST;

                panel = new MultipleParticleSimulationPanel(new MultipleParticleModel());

                //panel.setBounds(0, 0, 600, 600);
                //panel.setBounds(0, 0, 600, 600);
                panel.setBounds(0, 0, 600, 600);
            }
        });
    }

    public void testLayoutPerformed() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                assertTrue("layout should have been performed by now", panel.isLayoutPerformed());
            }
        });

    }

    public void testThatParticleContainerIsCentered() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                PiccoloTestingUtils.testIsRoughlyCentered(panel.getParticleContainer(), panel);
            }
        });

    }

    public void testThatParticleContainerIsMediumSized() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                PiccoloTestingUtils.testIsMediumSized(panel.getParticleContainer(), panel);
            }
        });

    }

    public void testParticlesVisible() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {

        for (int i = 0; i < panel.getNumParticles(); i++) {
            PiccoloTestingUtils.testBoundsAreFullyVisible(panel.getParticleNode(i), panel);
        }
            }
        });
    }
}
