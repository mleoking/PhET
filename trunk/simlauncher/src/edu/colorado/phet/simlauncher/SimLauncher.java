/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import com.sun.java.swing.SwingUtilities2;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * SimLauncher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncher {

    public SimLauncher() {
        JFrame frame = new JFrame( "PhET Simulation Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane( new TopLevelPane() );
        frame.setJMenuBar( new SimLauncherMenuBar( frame ) );
        frame.pack();
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new SimLauncher();
    }
}
