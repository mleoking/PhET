package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.statesofmatter.view.MultipleParticleSimulationPanel;
import junit.framework.TestCase;

public class ZSolidLiquidGasModuleTester extends TestCase {
    private volatile SolidLiquidGasModule module;

    public void setUp() {
        module = new SolidLiquidGasModule();
    }

    public void testThatSimulationPanelIsMultipleParticle() {
        assertEquals(MultipleParticleSimulationPanel.class, module.getSimulationPanel().getClass());
    }
}
