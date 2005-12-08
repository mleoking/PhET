// ClockDialog

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:12:44 PM
 */
package edu.colorado.phet.common.view.components.clockgui;

import edu.colorado.phet.common.model.FixedClock;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ClockControlPanelBackup extends JPanel {

    private FixedClock clock;

    private SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.1, 0.0, 100.0, 0.01);
    private JSpinner dtSpinner = new JSpinner(spinnerModel);
    private JTextField sleepIntervalTF = new JTextField(10);

    public ClockControlPanelBackup(FixedClock clock) {
        super(new FlowLayout());
        init(clock);
    }

    private void init(FixedClock clock) {
        this.clock = clock;
        buildClockControlPanel();
    }

    private void buildClockControlPanel() {
        this.add(new JLabel( SimStrings.get( "ClockControlPanel.DTLabel" ) + ":"));
        dtSpinner.setValue(new Double(clock.getRequestedDT()));
        this.add(dtSpinner);
        dtSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateClock();
            }
        });
        this.add(new JLabel( SimStrings.get( "ClockControlPanel.SleepIntervalLabel" ) + ":"));
        sleepIntervalTF.setText(Integer.toString(clock.getRequestedWaitTime()));
        this.add(sleepIntervalTF);
    }

    public float getDt() {
        return ((Double) this.dtSpinner.getValue()).floatValue();
//        return Double.parseDouble( this.dtSpinner.getContents() );
    }

    public int getSleepInterval() {
        return Integer.parseInt((this.sleepIntervalTF.getText()));
    }

    private void updateClock() {
        this.clock.setRequestedDT(((Double) dtSpinner.getValue()).floatValue());
    }
}
