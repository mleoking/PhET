/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.application.modules.messagemodule;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Jun 9, 2003
 * Time: 11:04:27 PM
 * Copyright (c) Jun 9, 2003 by Sam Reid
 */
public class MessageMonitorPanel extends JPanel implements Observer {
    private MessageModule module;
    private JTextArea monitor;

    public MessageMonitorPanel(MessageModule module) {
        this.module = module;
        monitor = new JTextArea("This is the MonitorPanel.");
        monitor.setBackground(Color.yellow);
        monitor.setFont(new Font("dialog", 0, 32));
        add(monitor);
        module.getModel().addObserver(this);
    }

    public void update(Observable o, Object arg) {
        monitor.setText("Num model Elements=" + module.getModel().numModelElements());
    }
}
