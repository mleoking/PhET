/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.branch.components.Wire;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 3, 2003
 * Time: 8:55:30 AM
 * Copyright (c) Dec 3, 2003 by Sam Reid
 */
public class DeveloperPanel {
    JPanel panel;
    JFrame frame;
    CCK2Module module;

    public DeveloperPanel(final CCK2Module module) {
        this.module = module;
        panel = new JPanel();
        JButton debugKirkhoff = new JButton("Apply Kirkhoff");
        debugKirkhoff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.applyKirkhoffsLaws();
            }
        });
        final JCheckBox jcb = new JCheckBox("Kirkhoff Logging", module.isKirkhoffLoggingEnabled());
        jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setKirkhoffLogging(jcb.isSelected());
            }
        });

        JPanel alphaPanel = new JPanel();
        alphaPanel.setBorder(BorderFactory.createTitledBorder("Wire Resistance"));
        alphaPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

//        double sca=Wire.getResistanceScale();
//        double d=.1;
        int numSteps = 400;
        double max = 1.0 / Wire.DEFAULT_RESISTANCE_SCALE;
        double min = 0;
        double stepSize = (max - min) / numSteps;
        SpinnerNumberModel snm = new SpinnerNumberModel(Wire.getResistanceScale(), 0.0, max, stepSize);
        final JSpinner js = new JSpinner(snm);
        js.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Double val = (Double) js.getValue();
                double v = val.doubleValue();

                Wire.setResistanceScale(v * Wire.DEFAULT_RESISTANCE_SCALE);
                module.applyKirkhoffsLaws();
                module.updateDVMAndAmmeter();
                module.getApparatusPanel().repaint();
            }
        });

        alphaPanel.add(js);
        JButton def = new JButton("Restore Default");
        def.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                js.setValue(new Double(1));

                Wire.setResistanceScale(1 * Wire.DEFAULT_RESISTANCE_SCALE);
                module.applyKirkhoffsLaws();
                module.updateDVMAndAmmeter();
                module.getApparatusPanel().repaint();
            }
        });
        alphaPanel.add(def);
//        js.setBorder(BorderFactory.createTitledBorder("Wire Resistance Scale"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(jcb);
        panel.add(debugKirkhoff);
        panel.add(alphaPanel);
    }

    public void show() {
        if (frame == null) {
            frame = new JFrame("DeveloperPanel");
            frame.setContentPane(panel);
            frame.pack();
        }
        frame.setVisible(true);
//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    Thread.sleep(500);
//                    frame.setVisible(true);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//                }
//            }
//        });
//        t.start();
    }
}
