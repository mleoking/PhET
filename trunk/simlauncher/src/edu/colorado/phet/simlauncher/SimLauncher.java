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
import edu.colorado.phet.simlauncher.util.LauncherUtil;
import edu.colorado.phet.simlauncher.util.MiscUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * SimLauncher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncher {

    /**
     * Private constructor. The only instance created is created by main()
     */
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
        startup( frame );
    }

    private void startup( Component parent) {
        boolean remoteAvailable = LauncherUtil.instance().isRemoteAvailable( Configuration.instance().getPhetUrl());

        // Tell the user if we're not online
        if( !Catalog.instance().isRemoteAvailable() ) {
                JOptionPane.showMessageDialog( parent,
                                               "<html>You are working offline, and will not be able to" +
                                               "<br>browse the online catalog of simulations. " );
        }

        // If there aren't any simulations installed, ask the user if he'd like to go to the catalog
        else if( Catalog.instance().getInstalledSimulations() == null
            || Catalog.instance().getInstalledSimulations().size() == 0 ) {
            int choice = JOptionPane.showConfirmDialog( parent, "<html><center>You have no simulations installed.<br>Would you like to visit the online simulation catalog?</html>", "Startup..", JOptionPane.YES_NO_OPTION );
            if( choice == JOptionPane.YES_OPTION) {
                if( !remoteAvailable ) {
                    JOptionPane.showMessageDialog( parent, "The online simulation catolg is not available.\nEither you do not have access to the internet, or the PhET web site is not available.");
                }
                else {
                    TopLevelPane.getInstance().setActivePane( TopLevelPane.ONLINE_SIMS_PANE );
                }
            }
        }

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