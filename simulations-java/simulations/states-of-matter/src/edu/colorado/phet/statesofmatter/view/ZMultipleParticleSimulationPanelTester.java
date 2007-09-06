package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.PiccoloTestingUtils;
import junit.framework.TestCase;

public class ZMultipleParticleSimulationPanelTester extends TestCase {
    private volatile MultipleParticleSimulationPanel panel;

    public void setUp() {
        panel = new MultipleParticleSimulationPanel();

        panel.setBounds(0, 0, 600, 600);
    }

    public void testThatParticleContainerIsCentered() {
        PiccoloTestingUtils.testIsRoughlyCentered(panel.getParticleContainer(), panel);
    }

    public void testThatParticleContainerIsMediumSized() {
        PiccoloTestingUtils.testIsMediumSized(panel.getParticleContainer(), panel);
    }
}
