package edu.colorado.phet.reids.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.reids.admin.TimesheetApp.SelectionModel;

/**
 * @author Sam Reid
 */
public class ControlPanel extends JPanel {
    private JLabel timeTodayLabel;
    private TimesheetModel timesheetModel;
    private MutableInt previousSeconds;
    private JLabel remainingInTarget;
    private JTextField targetTextField;
    private MutableInt targetHours;

    ControlPanel( final TimesheetModel timesheetModel, final ActionListener saveAction, final SelectionModel selectionModel, final MutableInt targetHours,
                  MutableInt previousSeconds, final ActionListener savePreferences, final File trunk ) {
        this.targetHours = targetHours;
        this.timesheetModel = timesheetModel;
        this.previousSeconds = previousSeconds;
        JButton clockIn = new JButton( "Clock In" );
        clockIn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timesheetModel.startNewTask();
            }
        } );
        add( clockIn );

        JButton clockOut = new JButton( "Clock Out" );
        clockOut.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timesheetModel.clockOut();
            }
        } );
        add( clockOut );

        JButton generateReport = new JButton( "Basic Report" );
        generateReport.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ReportFrame reportFrame = new ReportFrame( selectionModel.getSelection( timesheetModel ) );
                reportFrame.setVisible( true );
            }
        } );
        add( generateReport );

        JButton filteredReport = new JButton( "Monthly Report" );
        filteredReport.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                OrderedReportFrame reportFrame = new OrderedReportFrame( new MonthlyReportFilter( trunk ).filter( selectionModel.getSelection( timesheetModel ) ), null );
                reportFrame.setVisible( true );
            }
        } );
        add( filteredReport );

        final JButton save = new JButton( "Save" );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                saveAction.actionPerformed( e );
            }
        } );
        add( save );
        timesheetModel.addDirtyListener( new TimesheetModel.DirtyListener() {
            public void dirtyChanged() {
                save.setEnabled( timesheetModel.isDirty() );
            }
        } );
        save.setEnabled( timesheetModel.isDirty() );

        timeTodayLabel = new JLabel();
        add( timeTodayLabel );
        updateTimeTodayReadout();
        timesheetModel.addTimeListener( new TimesheetModel.TimeListener() {
            public void timeChanged() {
                updateTimeTodayReadout();
            }
        } );

        targetTextField = new JTextField( 10 );
        targetTextField.setText( targetHours.getValue() + "" );
        targetTextField.setBorder( BorderFactory.createTitledBorder( "target.hours" ) );
        targetTextField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                targetHours.setValue( Integer.parseInt( targetTextField.getText() ) );
                savePreferences.actionPerformed( null );
            }
        } );
        targetHours.addObserver( new SimpleObserver() {
            public void update() {
                targetTextField.setText( targetHours.getValue() + "" );
                updateRemainingInTarget();
            }
        } );
        add( targetTextField );
        timesheetModel.addTimeListener( new TimesheetModel.TimeListener() {
            public void timeChanged() {
                updateRemainingInTarget();
            }
        } );

        remainingInTarget = new JLabel();
        add( remainingInTarget );
        updateRemainingInTarget();

        add( new StretchingPanel( timesheetModel, trunk ) );
    }


    public static double toSeconds( double hours ) {
        return hours * 60 * 60;
    }

    private void updateRemainingInTarget() {
        int hours = targetHours.getValue();
        double minutes = hours * 60;
        double targetSeconds = minutes * 60;

        double elapsedSeconds = timesheetModel.getTotalTimeSeconds();
        double remaining = targetSeconds - elapsedSeconds - previousSeconds.getValue();
        remainingInTarget.setText( "Remaining: " + Util.secondsToElapsedTimeString( (long) remaining ) );
    }

    private void updateTimeTodayReadout() {
        timeTodayLabel.setText( "Today: " + Util.secondsToElapsedTimeString( timesheetModel.getSecondsToday() ) );
    }
}
