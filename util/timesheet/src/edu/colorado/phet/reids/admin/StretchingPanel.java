package edu.colorado.phet.reids.admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Sam Reid
 */
public class StretchingPanel extends JPanel {
    public StretchingPanel(final TimesheetModel timesheetModel) {
        add(new JLabel("Time since maintenance: stretching & exercise"));
        final JTextField textField = new JTextField(8);
        {
            textField.setEditable(false);
        }
        add(textField);
        TimesheetModel.TimeListener timeListener = new TimesheetModel.TimeListener() {
            public void timeChanged() {
                long time = getTimeSinceBeginningOfLast(timesheetModel);
                textField.setText(Util.secondsToElapsedTimeString(time));
            }
        };
        timesheetModel.addTimeListener(timeListener);
        timeListener.timeChanged();
        JButton button = new JButton("S&E");
        {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timesheetModel.startNewEntry("maintenance", "stretching & exercise");
                }
            });
        }
        add(button);
        setBorder(BorderFactory.createEtchedBorder());
    }

    private long getTimeSinceBeginningOfLast(TimesheetModel timesheetModel) {
        long elapsed = 0;
        for (int i = timesheetModel.getEntryCount() - 1; i >= 0; i--) {
            Entry entry = timesheetModel.getEntry(i);
            elapsed += entry.getElapsedSeconds();
            if (entry.getCategory().equals("maintenance") && entry.getNotes().equals("stretching & exercise")) {
                break;
            }
        }
        return elapsed;
    }
}
