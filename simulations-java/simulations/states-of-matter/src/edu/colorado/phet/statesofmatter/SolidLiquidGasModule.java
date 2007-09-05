package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

import javax.swing.*;

public class SolidLiquidGasModule extends Module {
    protected SolidLiquidGasModule() {
        super("Solid, Liquid, Gas", new ConstantDtClock(30, 1.0));

        setSimulationPanel(new JLabel());
    }
}
