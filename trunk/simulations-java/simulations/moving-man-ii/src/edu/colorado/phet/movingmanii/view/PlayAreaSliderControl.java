package edu.colorado.phet.movingmanii.view;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * These sliders should be Piccolo sliders for the following reasons:
 * 1. So we can color the thumbs
 * 2. So we can indicate when the value is out of range
 * 3. So we can easily identify when a change event is caused by the user vs. by a callback from the model.
 *
 * @author Sam Reid
 */
public class PlayAreaSliderControl extends PNode {
    private MovingManSliderNode slider;
    private final JTextField textField;

    public PlayAreaSliderControl(double min, double max, double value, String title, String units, Color color) {
        PText text = new PText(title);
        text.setTextPaint(color);
        text.setFont(new PhetFont(24, true));
        addChild(text);

        textField = new JTextField(4);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        PSwing swing = new PSwing(textField);
        addChild(swing);
        swing.setOffset(200, text.getFullBounds().getCenterY() - swing.getFullBounds().getHeight() / 2);//todo: align with other controls but make sure doesn't overlap text

        slider = new MovingManSliderNode.Horizontal(new MovingManSliderNode.Range(min, max), 0.0, new MovingManSliderNode.Range(0, 300), color);
        addChild(slider);
        slider.setOffset(0, text.getFullBounds().getHeight() + 10);

        PText unitsPText = new PText(units);
        unitsPText.setTextPaint(color);
        unitsPText.setFont(new PhetFont(24, true));
        addChild(unitsPText);
        unitsPText.setOffset(swing.getFullBounds().getMaxX() + 2, 0);


        setValue(value);
    }

    public void setValue(double value) {
        slider.setValue(value);
        textField.setText(new DefaultDecimalFormat("0.00").format(value));
    }

    public void addListener(MovingManSliderNode.Listener listener) {
        slider.addListener(listener);
    }

    public double getValue() {
        return slider.getValue();
    }
}
