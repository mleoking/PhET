/*Copyright, Sam Reid, 2003.*/
package org.reids.anttasks.tests;

//import org.srr.util.O;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: Sam Reid
 * Date: Jun 27, 2003
 * Time: 12:35:29 PM
 * Copyright (c) Jun 27, 2003 by Sam Reid
 */
public class AntScriptTestMain {
    public static void main(String[] args) {
        JFrame test = new JFrame("Test Ant");
        test.setVisible(true);
        test.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
//        O.d("Running.");
    }
}
