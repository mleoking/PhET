package edu.colorado.phet.movingman.common;

/*
import com.sun.java.swing.JComponent;
import com.sun.java.swing.JButton;
import com.sun.java.swing.JRadioButton;
import com.sun.java.swing.ButtonGroup;
import com.sun.java.swing.JLabel;
import com.sun.java.swing.ImageIcon;
import com.sun.java.swing.BoxLayout;
import com.sun.java.swing.Box;
import com.sun.java.swing.BorderFactory;
import com.sun.java.swing.JTabbedPane;
import com.sun.java.swing.JPanel;
import com.sun.java.swing.JFrame;
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class CustomDialog extends javax.swing.JDialog {
    private String typedText = null;

    private String magicWord;
    private javax.swing.JOptionPane optionPane;

    public String getValidatedText() {
        return typedText;
    }

    public CustomDialog(Frame aFrame, String aWord, DialogTest parent) {
        super(aFrame, true);
        final DialogTest dd = parent;

        magicWord = aWord.toUpperCase();
        setTitle("Quiz");

        final String msgString1 = "What was Dr. SEUSS's real last name?";
        final String msgString2 = "(The answer is \"" + magicWord
                + "\".)";
        final JTextField textField = new JTextField(10);
        Object[] array = {msgString1, msgString2, textField};

        final String btnString1 = "Enter";
        final String btnString2 = "Cancel";
        Object[] options = {btnString1, btnString2};

        optionPane = new JOptionPane(array,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);
        setContentPane(optionPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(
                        JOptionPane.CLOSED_OPTION));
            }
        });

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                optionPane.setValue(btnString1);
            }
        });

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();

                if (isVisible()
                        && (e.getSource() == optionPane)
                        && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                        prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                    Object value = optionPane.getValue();

                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
                        //ignore reset
                        return;
                    }

                    if (value.equals(btnString1)) {
                        typedText = textField.getText();
                        String ucText = typedText.toUpperCase();
                        if (ucText.equals(magicWord)) {
                            // we're done; dismiss the dialog
                            setVisible(false);
                        } else {
                            // text was invalid
                            textField.selectAll();
                            JOptionPane.showMessageDialog(
                                    CustomDialog.this,
                                    "Sorry, \"" + typedText + "\" "
                                    + "isn't a valid response.\n"
                                    + "Please enter "
                                    + magicWord + ".",
                                    "Try again",
                                    JOptionPane.ERROR_MESSAGE);
                            typedText = null;
                            // reset the JOptionPane's value
                            optionPane.setValue(
                                    JOptionPane.UNINITIALIZED_VALUE);
                        }
                    } else { // user closed dialog or clicked cancel
                        dd.setLabel("It's OK.  "
                                + "We won't force you to type "
                                + magicWord + ".");
                        setVisible(false);
                    }
                }
            }
        });
    }
}


