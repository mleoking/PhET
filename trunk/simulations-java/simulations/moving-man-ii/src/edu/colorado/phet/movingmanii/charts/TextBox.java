package edu.colorado.phet.movingmanii.charts;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * This component allows the user to enter data for a series, and it reads out values for series.
 *
 * @author Sam Reid
 */
public class TextBox extends PNode {
    private JTextField swingTextField;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    protected final PSwing textField;

    public TextBox() {
        swingTextField = new JTextField(4);
        swingTextField.setHorizontalAlignment(JTextField.RIGHT);
        textField = new PSwing(swingTextField);
        addChild(textField);
        swingTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                notifyListeners();
            }
        });
        swingTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                notifyListeners();//Accept value on focus lost.
            }

            public void focusGained(FocusEvent e) {
                swingTextField.selectAll();//Automatically select all text so the user doesn't have to delete it to type new values.
            }
        });
    }

    private void notifyListeners() {
        for (Listener listener : listeners) {
            listener.changed();
        }
    }

    //TODO: need a way for model-propagated values to still update the text field, e.g. for collisions with wall

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

    public void addFocusListener(FocusListener focusListener) {
        swingTextField.addFocusListener(focusListener);
    }

    public static interface Listener {
        void changed();
    }
}
