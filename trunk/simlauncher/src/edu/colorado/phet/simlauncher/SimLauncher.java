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

import edu.colorado.phet.simlauncher.menus.SimLauncherMenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * SimLauncher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncher {

    private SimLauncher() {

        JFrame frame = new JFrame( "PhET Simulation Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                centerFrame( (JFrame)e.getComponent() );
            }
        } );

        frame.setContentPane( TopLevelPane.getInstance() );
        frame.setJMenuBar( new SimLauncherMenuBar() );
        frame.pack();
        frame.setVisible( true );
    }

    private static void centerFrame( JFrame frame ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = ( d.width - frame.getWidth() ) / 2;
            int y = ( d.height - frame.getHeight() ) / 2;
            frame.setLocation( x, y );
    }

    public static void main( String[] args ) {
        new SimLauncher();
    }
}
