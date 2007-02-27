/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*        Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.text.InternationalFormatter;


public class LiquidSpinnerUI extends BasicSpinnerUI {
    /**
     * The mouse/action listeners that are added to the spinner's
     * arrow buttons.  These listeners are shared by all
     * spinner arrow buttons.
     *
     * @see #createNextButton
     * @see #createPreviousButton
     */
    private static final ArrowButtonHandler nextButtonHandler = new ArrowButtonHandler("increment",
            true);
    private static final ArrowButtonHandler previousButtonHandler = new ArrowButtonHandler("decrement",
            false);
    private static final Dimension zeroSize = new Dimension(0, 0);

    public static ComponentUI createUI(JComponent c) {
        return new LiquidSpinnerUI();
    }

    protected Component createPreviousButton() {
        JButton b = new SpecialUIButton(new LiquidSpinnerButtonUI(
                    SwingConstants.SOUTH));
        b.setFocusable(false);
        b.addActionListener(previousButtonHandler);
        b.addMouseListener(previousButtonHandler);

        return b;
    }

    protected Component createNextButton() {
        JButton b = new SpecialUIButton(new LiquidSpinnerButtonUI(
                    SwingConstants.NORTH));
        b.setFocusable(false);
        b.addActionListener(nextButtonHandler);
        b.addMouseListener(nextButtonHandler);

        return b;
    }

    /**
     * @see javax.swing.plaf.basic.BasicSpinnerUI#createEditor()
     */
    protected JComponent createEditor() {
        JComponent editor = super.createEditor();

        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor de = (JSpinner.DefaultEditor) editor;

            // This is pretty silly: But it's the only way I've found (so far)
            // to find the real size of the editor
            de.getTextField().setBorder(new EmptyBorder(0, 0, 0, 0)); //half,0,diff-half,0));

            Dimension prefSize = de.getPreferredSize();
            int compHeight = prefSize.height;

            int height = LiquidSpinnerButtonUI.getSkin().getVsize() * 2;
            int diff = height - compHeight;

            if (diff > 0) {
                int half = diff / 2;
                de.getTextField().setBorder(new EmptyBorder(half, 0,
                        diff - half, 0));
            }
        }

        return editor;
    }

    /**
     * Copy and paste from BasicSpinnerUI - sigh !
     *
     *
     * A handler for spinner arrow button mouse and action events.  When
     * a left mouse pressed event occurs we look up the (enabled) spinner
     * that's the source of the event and start the autorepeat timer.  The
     * timer fires action events until any button is released at which
     * point the timer is stopped and the reference to the spinner cleared.
     * The timer doesn't start until after a 300ms delay, so often the
     * source of the initial (and final) action event is just the button
     * logic for mouse released - which means that we're relying on the fact
     * that our mouse listener runs after the buttons mouse listener.
     * <p>
     * Note that one instance of this handler is shared by all slider previous
     * arrow buttons and likewise for all of the next buttons,
     * so it doesn't have any state that persists beyond the limits
     * of a single button pressed/released gesture.
     */
    private static class ArrowButtonHandler extends AbstractAction
        implements MouseListener {
        final javax.swing.Timer autoRepeatTimer;
        final boolean isNext;
        JSpinner spinner = null;

        ArrowButtonHandler(String name, boolean isNext) {
            super(name);
            this.isNext = isNext;
            autoRepeatTimer = new javax.swing.Timer(60, this);
            autoRepeatTimer.setInitialDelay(300);
        }

        private JSpinner eventToSpinner(AWTEvent e) {
            Object src = e.getSource();

            while ((src instanceof Component) && !(src instanceof JSpinner)) {
                src = ((Component) src).getParent();
            }

            return (src instanceof JSpinner) ? (JSpinner) src : null;
        }

        public void actionPerformed(ActionEvent e) {
            JSpinner spinner = this.spinner;

            if (!(e.getSource() instanceof javax.swing.Timer)) {
                // Most likely resulting from being in ActionMap.
                spinner = eventToSpinner(e);
            }

            if (spinner != null) {
                try {
                    int calendarField = getCalendarField(spinner);
                    spinner.commitEdit();

                    if (calendarField != -1) {
                        ((SpinnerDateModel) spinner.getModel()).setCalendarField(calendarField);
                    }

                    Object value = (isNext) ? spinner.getNextValue()
                                            : spinner.getPreviousValue();

                    if (value != null) {
                        spinner.setValue(value);
                        select(spinner);
                    }
                } catch (IllegalArgumentException iae) {
                    UIManager.getLookAndFeel().provideErrorFeedback(spinner);
                } catch (ParseException pe) {
                    UIManager.getLookAndFeel().provideErrorFeedback(spinner);
                }
            }
        }

        /**
         * If the spinner's editor is a DateEditor, this selects the field
         * associated with the value that is being incremented.
         */
        private void select(JSpinner spinner) {
            JComponent editor = spinner.getEditor();

            if (editor instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) editor;
                JFormattedTextField ftf = dateEditor.getTextField();
                Format format = dateEditor.getFormat();
                Object value;

                if ((format != null) && ((value = spinner.getValue()) != null)) {
                    SpinnerDateModel model = dateEditor.getModel();
                    DateFormat.Field field = DateFormat.Field.ofCalendarField(model.getCalendarField());

                    if (field != null) {
                        try {
                            AttributedCharacterIterator iterator = format.formatToCharacterIterator(value);

                            if (!select(ftf, iterator, field) &&
                                    (field == DateFormat.Field.HOUR0)) {
                                select(ftf, iterator, DateFormat.Field.HOUR1);
                            }
                        } catch (IllegalArgumentException iae) {
                        }
                    }
                }
            }
        }

        /**
         * Selects the passed in field, returning true if it is found,
         * false otherwise.
         */
        private boolean select(JFormattedTextField ftf,
            AttributedCharacterIterator iterator, DateFormat.Field field) {
            int max = ftf.getDocument().getLength();

            iterator.first();

            do {
                Map attrs = iterator.getAttributes();

                if ((attrs != null) && attrs.containsKey(field)) {
                    int start = iterator.getRunStart(field);
                    int end = iterator.getRunLimit(field);

                    if ((start != -1) && (end != -1) && (start <= max) &&
                            (end <= max)) {
                        ftf.select(start, end);
                    }

                    return true;
                }
            } while (iterator.next() != CharacterIterator.DONE);

            return false;
        }

        /**
         * Returns the calendarField under the start of the selection, or
         * -1 if there is no valid calendar field under the selection (or
         * the spinner isn't editing dates.
         */
        private int getCalendarField(JSpinner spinner) {
            JComponent editor = spinner.getEditor();

            if (editor instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) editor;
                JFormattedTextField ftf = dateEditor.getTextField();
                int start = ftf.getSelectionStart();
                JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();

                if (formatter instanceof InternationalFormatter) {
                    Format.Field[] fields = ((InternationalFormatter) formatter).getFields(start);

                    for (int counter = 0; counter < fields.length; counter++) {
                        if (fields[counter] instanceof DateFormat.Field) {
                            int calendarField;

                            if (fields[counter] == DateFormat.Field.HOUR1) {
                                calendarField = Calendar.HOUR;
                            } else {
                                calendarField = ((DateFormat.Field) fields[counter]).getCalendarField();
                            }

                            if (calendarField != -1) {
                                return calendarField;
                            }
                        }
                    }
                }
            }

            return -1;
        }

        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) &&
                    e.getComponent().isEnabled()) {
                spinner = eventToSpinner(e);
                autoRepeatTimer.start();

                focusSpinnerIfNecessary();
            }
        }

        public void mouseReleased(MouseEvent e) {
            autoRepeatTimer.stop();
            spinner = null;
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        /**
         * Requests focus on a child of the spinner if the spinner doesn't
         * have focus.
         */
        private void focusSpinnerIfNecessary() {
            Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                               .getFocusOwner();

            if (spinner.isRequestFocusEnabled() &&
                    ((fo == null) ||
                    !SwingUtilities.isDescendingFrom(fo, spinner))) {
                Container root = spinner;

                if (!root.isFocusCycleRoot()) {
                    root = root.getFocusCycleRootAncestor();
                }

                if (root != null) {
                    FocusTraversalPolicy ftp = root.getFocusTraversalPolicy();
                    Component child = ftp.getComponentAfter(root, spinner);

                    if ((child != null) &&
                            SwingUtilities.isDescendingFrom(child, spinner)) {
                        child.requestFocus();
                    }
                }
            }
        }
    }
}
