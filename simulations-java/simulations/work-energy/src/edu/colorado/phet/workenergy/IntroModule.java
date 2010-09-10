package edu.colorado.phet.workenergy;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author Sam Reid
 */
public class IntroModule extends Module {
    public IntroModule(PhetFrame phetFrame) {
        super("Work Energy", new ConstantDtClock(30, 1.0));
        setSimulationPanel(new PhetPCanvas());
        setControlPanel(new WorkEnergyControlPanel());
    }
}
