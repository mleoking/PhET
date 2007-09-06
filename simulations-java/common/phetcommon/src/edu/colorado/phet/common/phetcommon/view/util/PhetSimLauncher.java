package edu.colorado.phet.common.phetcommon.view.util;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;

import javax.swing.*;
import java.lang.reflect.Constructor;


public class PhetSimLauncher {
    /**
     * Launches a simulation (child of NonPiccoloPhetApplication) that has a
     * constructor accepting a String array (the command line arguments of the
     * program).
     *
     * @param args              The arguments passed to the main() method.
     *
     * @param phetApplication   The class that extends NonPiccoloPhetApplication.
     */
    public static void launchSim(String[] args, Class phetApplication) {
        if (!NonPiccoloPhetApplication.class.isAssignableFrom(phetApplication)) {
            throw new IllegalArgumentException("The class " + phetApplication.getName() + " should extend NonPiccoloPhetApplication.");
        }

        try {
            Class stringClass = (new String[0]).getClass();

            Constructor constructor = phetApplication.getConstructor(new Class[]{stringClass});

            final NonPiccoloPhetApplication simulation = (NonPiccoloPhetApplication)constructor.newInstance(new Object[]{args});

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    simulation.startApplication();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
