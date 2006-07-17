/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.simlauncher.SimLauncher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HelpMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class HelpMenu extends JMenu {
    public HelpMenu() {
        super( "Help" );

        JMenuItem aboutMI = new JMenuItem( "About" );
        aboutMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showAboutDialog();
            }
        } );
        add( aboutMI);

    }


    public void showAboutDialog() {
        String javaVersion = SimStrings.get( "Common.HelpMenu.JavaVersion" ) + ": " + System.getProperty( "java.version" );
        final String msg = "PhET Sim Launcher" + "\n\n" +
                           "A program for installing, launching, and managing\n PhET simulations."
                           + "\n\n" + SimStrings.get( "Common.HelpMenu.VersionLabel" ) + ": " +
                           SimLauncher.getVersion() + "\n\n" + javaVersion + "\n";
        JOptionPane.showMessageDialog( this, msg, SimStrings.get( "Common.HelpMenu.AboutTitle" ) + " " + "Phet Sim Launcher", JOptionPane.INFORMATION_MESSAGE );
    }

}
