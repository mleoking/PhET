// ClockDialog

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:12:44 PM
 */
package edu.colorado.phet.common.view.components.clockgui;

import edu.colorado.phet.common.model.Clock;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ClockControlPanelBackup extends JPanel {

    private Clock clock;

    private SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.1, 0.0, 100.0, 0.01);
    private JSpinner dtSpinner = new JSpinner(spinnerModel);
    private JTextField sleepIntervalTF = new JTextField(10);

    public ClockControlPanelBackup(Clock clock) {
        super(new FlowLayout());
        init(clock);
    }

    private void init(Clock clock) {
        this.clock = clock;
        buildClockControlPanel();
    }

    private void buildClockControlPanel() {
        this.add(new JLabel("dt (sec):"));
        dtSpinner.setValue(new Double(clock.getDt()));
        this.add(dtSpinner);
        dtSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateClock();
            }
        });
        this.add(new JLabel("sleep interval (msec):"));
        sleepIntervalTF.setText(Integer.toString(clock.getWaitTime()));
        this.add(sleepIntervalTF);
    }

    public float getDt() {
        return ((Double) this.dtSpinner.getValue()).floatValue();
//        return Double.parseDouble( this.dtSpinner.getText() );
    }

    public int getSleepInterval() {
        return Integer.parseInt((this.sleepIntervalTF.getText()));
    }

    private void updateClock() {
        this.clock.setDt(((Double) dtSpinner.getValue()).floatValue());
    }
}
