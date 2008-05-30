package edu.colorado.phet.statesofmatter;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.MultipleParticleSimulationPanel;

public class SolidLiquidGasModule extends Module {
    private MultipleParticleModel model;

    protected SolidLiquidGasModule( Frame parentFrame ) {
        super(StatesOfMatterStrings.TITLE_SOLID_LIQUID_GAS_MODULE, 
                new ConstantDtClock(10, StatesOfMatterConstants.DELTA_T));

        this.model = new MultipleParticleModel(getClock());

        setSimulationPanel(new MultipleParticleSimulationPanel(model, getClock()));
    }
}
