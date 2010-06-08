package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.event.ChangeListener;

/**
 * These sliders should be Piccolo sliders for the following reasons:
 * 1. So we can color the thumbs
 * 2. So we can indicate when the value is out of range
 * 3. So we can easily identify when a change event is caused by the user vs. by a callback from the model.
 *
 * @author Sam Reid
 */
public class PlayAreaSliderControl extends PNode {
    private final LinearValueControl linearValueControl;

    public PlayAreaSliderControl(double min, double max, String title, String units) {
        linearValueControl = new LinearValueControl(-10, 10, title, "0.00", units);
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
