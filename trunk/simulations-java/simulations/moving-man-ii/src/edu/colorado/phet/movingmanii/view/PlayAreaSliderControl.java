package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.event.ChangeListener;

/**
 * @author Sam Reid
 */
public class PlayAreaSliderControl extends PNode {
    private final LinearValueControl linearValueControl = new LinearValueControl(-10, 10, "Position", "0.00", "m");

    public PlayAreaSliderControl() {
        addChild(new PSwing(linearValueControl));
    }

    public void setValue(double position) {
        linearValueControl.setValue(position);
    }

    public void addListener(ChangeListener changeListener) {
        linearValueControl.addChangeListener(changeListener);
    }

    public double getValue() {
        return linearValueControl.getValue();
    }
}
