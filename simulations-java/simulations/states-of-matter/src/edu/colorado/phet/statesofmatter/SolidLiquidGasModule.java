package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.MultipleParticleSimulationPanel;

public class SolidLiquidGasModule extends Module {
    private MultipleParticleModel model;

    protected SolidLiquidGasModule() {
        super("Solid, Liquid, Gas", new ConstantDtClock(10, StatesOfMatterConfig.DELTA_T));

        this.model = new MultipleParticleModel(getClock());

        setSimulationPanel(new MultipleParticleSimulationPanel(model, getClock()));
    }
}
