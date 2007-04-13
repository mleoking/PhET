/**
 * Copyright (C) 2007 - University of Colorado
 *
 * User: New Admin
 * Date: Feb 2, 2007
 * Time: 11:03:32 AM
 */

package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.BaseModel;

import javax.swing.*;

public class TestPhetApplication {
    private final PhetApplication app;

    public TestPhetApplication() {
        app = new PhetApplication(new String[0], "Title", "Description", "1.0");

        MyModule module = new MyModule();

        module.setModel(new BaseModel());

        module.setSimulationPanel(new JLabel());

        app.addModule(module);
    }

    public void start() {
        app.startApplication();
    }

    public static void main(String[] args) {
        (new TestPhetApplication()).start();
    }

    private static class MyModule extends Module {
        public MyModule() {
            super("Name", new SwingClock(10, 0.01));
        }

        public void setSimulationPanel(JComponent panel) {
            super.setSimulationPanel(panel);
        }
    }
}
