package edu.colorado.phet.workenergy;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.colorado.phet.workenergy.view.WorkEnergyObjectNode;

import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class IntroModule extends Module {
    public IntroModule(PhetFrame phetFrame) {
        super("Work Energy", new ConstantDtClock(30, 1.0));
        final WorkEnergyObject workEnergyObject = new WorkEnergyObject();
        ModelViewTransform2D transform = new ModelViewTransform2D(new Rectangle2D.Double(0, 0, 1, 1), new Rectangle2D.Double(0, 0, 1, 1));
        WorkEnergyObjectNode node = new WorkEnergyObjectNode(workEnergyObject, transform);
        final PhetPCanvas panel = new PhetPCanvas();
        panel.addScreenChild(node);
        setSimulationPanel(panel);
        setControlPanel(new WorkEnergyControlPanel());

        getClock().addClockListener(new ClockAdapter() {
            @Override
            public void simulationTimeChanged(ClockEvent clockEvent) {
                super.simulationTimeChanged(clockEvent);
                workEnergyObject.stepInTime(clockEvent.getSimulationTimeChange());
            }
        });
    }
}
