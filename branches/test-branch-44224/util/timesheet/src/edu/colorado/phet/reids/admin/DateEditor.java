package edu.colorado.phet.reids.admin;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Date;

//see http://java.sun.com/docs/books/tutorial/uiswing/examples/components/TableFTFEditDemoProject/src/components/IntegerEditor.java
public class DateEditor extends DefaultCellEditor {
    private JFormattedTextField textField;

    public DateEditor() {
        super(new JFormattedTextField());
        textField = (JFormattedTextField) getComponent();

        textField.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(Entry.LOAD_FORMAT)));
        textField.setFocusLostBehavior(JFormattedTextField.PERSIST);

        textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        textField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.isEditValid()) { //The text is invalid.
                    if (userSaysRevert()) { //reverted
                        textField.postActionEvent(); //inform the editor
                    }
                } else try {              //The text is valid,
                    textField.commitEdit();     //so use it.
                    textField.postActionEvent(); //stop editing
                } catch (java.text.ParseException exc) {
                }
            }
        });
    }

    //Override to invoke setValue on the formatted text field.

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected,
                                                 int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        ftf.setValue(value);
        return ftf;
    }

//    Override to ensure that the value remains an Integer.

    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        Object o = ftf.getValue();
        System.out.println("trying to parse date: " + o.getClass());
        if (o instanceof Date) {
            return o;
        } else if (o instanceof String) {
            try {
                return Entry.LOAD_FORMAT.parse(o.toString());
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return new Date();
            }
        } else {
            new RuntimeException("What type: " + o.getClass()).printStackTrace();
            return new Date();
        }
    }

    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version
    //of this method so that everything gets cleaned up.

    public boolean stopCellEditing() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        if (ftf.isEditValid()) {
            try {
                ftf.commitEdit();
            } catch (java.text.ParseException exc) {
            }

        } else { //text is invalid
            if (!userSaysRevert()) { //user wants to edit
                return false; //don't let the editor go away
            }
        }
        return super.stopCellEditing();
    }

    /**
     * Lets the user know that the text they entered is
     * bad. Returns true if the user elects to revert to
     * the last good value.  Otherwise, returns false,
     * indicating that the user wants to continue editing.
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        textField.selectAll();
        Object[] options = {"Edit",
                "Revert"};
        int answer = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(textField),
                "The value must be an integer between "
                        + 0 + " and "
                        + -0 + ".\n"
                        + "You can either continue editing "
                        + "or revert to the last valid value.",
                "Invalid Text Entered",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[1]);

        if (answer == 1) { //Revert!
            textField.setValue(textField.getValue());
            return true;
        }
        return false;
    }
}
