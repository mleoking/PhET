package edu.colorado.phet.reids.admin;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Sam Reid
 */
public class ControlPanel extends JPanel {
    private JLabel totalTimeLabel;
    private JLabel timeTodayLabel;
    private TimesheetModel timesheetModel;
    private JLabel remainingInTarget;
    private JTextField targetTextField;
    private MutableInt targetHours;

    ControlPanel(final TimesheetModel timesheetModel, final ActionListener saveAction, final TimesheetApp.SelectionModel selectionModel, final MutableInt targetHours, final ActionListener savePreferences) {
        this.targetHours = targetHours;
        this.timesheetModel = timesheetModel;
        JButton clockIn = new JButton("Clock In");
        clockIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timesheetModel.startNewTask();
            }
        });
        add(clockIn);

        JButton clockOut = new JButton("Clock Out");
        clockOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timesheetModel.clockOut();
            }
        });
        add(clockOut);

        JButton generateReport = new JButton("Report on Selection");
        generateReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ReportFrame reportFrame = new ReportFrame(selectionModel.getSelection(timesheetModel));
                reportFrame.setVisible(true);
            }
        });
        add(generateReport);

        JButton filteredReport = new JButton("Filtered Report");
        filteredReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ReportFrame reportFrame = new ReportFrame(new MonthlyReportFilter().filter(selectionModel.getSelection(timesheetModel)));
                reportFrame.setVisible(true);
            }
        });
        add(filteredReport);

        final JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAction.actionPerformed(e);
            }
        });
        add(save);
        timesheetModel.addDirtyListener(new TimesheetModel.DirtyListener() {
            public void dirtyChanged() {
                save.setEnabled(timesheetModel.isDirty());
            }
        });
        save.setEnabled(timesheetModel.isDirty());

        totalTimeLabel = new JLabel();
        add(totalTimeLabel);
        updateTimeReadout();
        timesheetModel.addTimeListener(new TimesheetModel.TimeListener() {
            public void timeChanged() {
                updateTimeReadout();
            }
        });

        timeTodayLabel = new JLabel();
        add(timeTodayLabel);
        updateTimeTodayReadout();
        timesheetModel.addTimeListener(new TimesheetModel.TimeListener() {
            public void timeChanged() {
                updateTimeTodayReadout();
            }
        });

        targetTextField = new JTextField(10);
        targetTextField.setText(targetHours.getValue() + "");
        targetTextField.setBorder(BorderFactory.createTitledBorder("target.hours"));
        targetTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                targetHours.setValue(Integer.parseInt(targetTextField.getText()));
                savePreferences.actionPerformed(null);
            }
        });
        targetHours.addObserver(new SimpleObserver() {
            public void update() {
                targetTextField.setText(targetHours.getValue() + "");
                updateRemainingInTarget();
            }
        });
        add(targetTextField);
        timesheetModel.addTimeListener(new TimesheetModel.TimeListener() {
            public void timeChanged() {
                updateRemainingInTarget();
            }
        });

        remainingInTarget = new JLabel();
        add(remainingInTarget);
        updateRemainingInTarget();

        add(new StretchingPanel(timesheetModel));
    }

    private void updateRemainingInTarget() {
        int hours = targetHours.getValue();
        double minutes = hours * 60;
        double sec = minutes * 60;

        double elapsed = timesheetModel.getTotalTimeSeconds();
        double remaining = sec - elapsed;
        remainingInTarget.setText("Remaining: " + Util.secondsToElapsedTimeString((long) remaining));
    }

    private void updateTimeReadout() {
        totalTimeLabel.setText("Total: " + Util.secondsToElapsedTimeString(timesheetModel.getTotalTimeSeconds()));
    }

    private void updateTimeTodayReadout() {
        timeTodayLabel.setText("Today: " + Util.secondsToElapsedTimeString(timesheetModel.getSecondsToday()));
    }
}
