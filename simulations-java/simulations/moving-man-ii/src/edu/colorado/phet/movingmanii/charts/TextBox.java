package edu.colorado.phet.movingmanii.charts;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This component allows the user to enter data for a series, and it reads out values for series.
 *
 * @author Sam Reid
 */
public class TextBox extends PNode {
    private JTextField swingTextField;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public TextBox(String title, Color color) {
        PText titleNode = new PText(title);
        titleNode.setFont(new PhetFont(12, true));
        titleNode.setTextPaint(color);
        addChild(titleNode);
        swingTextField = new JTextField(4);
        PSwing textField = new PSwing(swingTextField);
        textField.setOffset(titleNode.getFullBounds().getX(), titleNode.getFullBounds().getMaxY());
        addChild(textField);
        swingTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (Listener listener : listeners) {
                    listener.changed();
                }
            }
        });
    }

    public void setText(String s) {
        if (!s.equals(swingTextField.getText()) && !swingTextField.hasFocus()) {
            swingTextField.setText(s);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public String getText() {
        return swingTextField.getText();
    }

    public static interface Listener {
        void changed();
    }
}
