// ClockDialog

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:12:44 PM
 */
package edu.colorado.phet.common.view.components.clockgui;

import edu.colorado.phet.common.model.FixedClock;
import edu.colorado.phet.common.model.ClockStateListener;
import edu.colorado.phet.common.model.ThreadPriority;
import edu.colorado.phet.common.model.IClock;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockControlPanel extends JPanel implements ClockStateListener {

    private IClock clock;
    private SpinnerNumberModel spinnerModelDT;
    private JSpinner dtSpinner;
    private SpinnerNumberModel spinnerModelWT;
    JSpinner waitTimeSpinner = new JSpinner();
    private ThreadPriorityPanel tpp;

    private static void setFont(JSpinner j, Font f) {
        Component[] c = j.getEditor().getComponents();
        for (int i = 0; i < c.length; i++) {
            c[i].setFont(f);
        }
    }

    Font bigfont = new Font("dialog", 0, 20);

    public ClockControlPanel(IClock clock) {
        super(new FlowLayout());

        spinnerModelDT = new SpinnerNumberModel(clock.getRequestedDT(), 0.0, 100.0, 0.05);
        spinnerModelWT = new SpinnerNumberModel(clock.getRequestedWaitTime(), 0, 1000, 5);
        dtSpinner = new JSpinner(spinnerModelDT);
        dtSpinner.setFont(new Font("dialog", 0, 24));
        waitTimeSpinner = new JSpinner(spinnerModelWT);
        setFont(dtSpinner, bigfont);
        setFont(waitTimeSpinner, bigfont);

        this.clock = clock;
        buildClockControlPanel();
//        init(clock);
    }

    private void syncPanelToClock() {
        double dt = clock.getRequestedDT();
        dtSpinner.setValue(new Double(dt));
        waitTimeSpinner.setValue(new Integer(clock.getRequestedWaitTime()));
    }

    private static class ThreadPriorityPanel extends JPanel {
        private IClock c;
        private JRadioButton min;
        private JRadioButton norm;
        private JRadioButton max;

        public ThreadPriorityPanel(final IClock c) {
            this.c = c;

            min = new JRadioButton("Minimum");
            min.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    c.setThreadPriority(ThreadPriority.MIN);
                }
            });

            norm = new JRadioButton("Normal");
            norm.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    c.setThreadPriority(ThreadPriority.NORMAL);
                }
            });
            max = new JRadioButton("Maximum");
            max.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    c.setThreadPriority(ThreadPriority.MAX);
                }
            });
            ThreadPriority prior = c.getThreadPriority();

            if (prior == ThreadPriority.MIN)
                min.setSelected(true);
            else if (prior == ThreadPriority.MAX)
                max.setSelected(true);
            else if (prior == ThreadPriority.NORMAL)
                norm.setSelected(true);
            ButtonGroup bg = new ButtonGroup();
            bg.add(min);
            bg.add(norm);
            bg.add(max);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue, 2), "Thread Priority"));
            add(max);
            add(norm);
            add(min);
        }
    }

    private void buildClockControlPanel() {
        JLabel dtlabel = new JLabel("dt (sec):");
        dtlabel.setFont(bigfont);
        this.add(dtlabel);
        dtSpinner.setValue(new Double(clock.getRequestedDT() ));
        this.add(dtSpinner);
        dtSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateClockDT();
            }
        });

        JLabel sleeplabel = new JLabel("sleep interval (msec):");
        sleeplabel.setFont(bigfont);
        this.add(sleeplabel);
        waitTimeSpinner.setValue(new Integer(clock.getRequestedWaitTime()));
        this.add(waitTimeSpinner);
        waitTimeSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateClockWaitTime();
            }
        });
        tpp = new ThreadPriorityPanel(clock);
        add(tpp);
    }

    public float getDt() {
        return ((Double) this.dtSpinner.getValue()).floatValue();
    }

    private void updateClockDT() {
        this.clock.setRequestedDT(getDt());
    }

    private void updateClockWaitTime() {
        this.clock.setRequestedWaitTime(getSleepInterval());
    }

    public int getSleepInterval() {
        return ((Integer) waitTimeSpinner.getValue()).intValue();
    }

    public void setClock(FixedClock c) {
        this.clock = c;
        syncPanelToClock();
    }

    public void waitTimeChanged(int waitTime) {
        this.waitTimeSpinner.setValue(new Integer(waitTime));
    }

    public void dtChanged(double dt) {
        this.dtSpinner.setValue(new Double(dt));
    }

    public void threadPriorityChanged(ThreadPriority prior) {
        if (prior == ThreadPriority.MIN)
            tpp.min.setSelected(true);
        else if (prior == ThreadPriority.MAX)
            tpp.max.setSelected(true);
        else if (prior == ThreadPriority.NORMAL)
            tpp.norm.setSelected(true);
    }
}
