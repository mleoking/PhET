package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.controls.StopwatchDecorator;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:58:49 AM
 */

public class MeasurementToolSetNode extends PhetPNode {
    private VoltmeterNode voltmeterNode;
    private VirtualAmmeterNode virtualAmmeterNode;
    private PNode stopwatchNode;

    public MeasurementToolSetNode(CCKModel model, PSwingCanvas pSwingCanvas, CCKModule module, VoltmeterModel voltmeterModel, IClock clock) {
        voltmeterNode = new VoltmeterNode(voltmeterModel);
        addChild(voltmeterNode);
        this.virtualAmmeterNode = new VirtualAmmeterNode(model.getCircuit(), pSwingCanvas, module);
        virtualAmmeterNode.setVisible(false);
        addChild(virtualAmmeterNode);

        stopwatchNode = new PhetPNode(new PSwing(new StopwatchDecorator(clock, 1, "s")));
        stopwatchNode.addInputEventListener(new CursorHandler(Cursor.HAND_CURSOR));
        stopwatchNode.addInputEventListener(new PDragEventHandler());
        stopwatchNode.setVisible(false);
        stopwatchNode.scale(1.0 / 60.0);
        stopwatchNode.translate(2, 2);
        addChild(stopwatchNode);
    }

    public void setVoltmeterVisible(boolean visible) {
        voltmeterNode.setVisible(visible);
    }

    public void setVirtualAmmeterVisible(boolean selected) {
        virtualAmmeterNode.setVisible(selected);
    }

    public void setStopwatchVisible(boolean selected) {
        stopwatchNode.setVisible(selected);
    }
}
