/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/HelpMenu.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2006/07/28 23:31:32 $
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.SimLauncher;
import edu.colorado.phet.simlauncher.actions.SimLauncherHelpAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HelpMenu
 *
 * @author Ron LeMaster
 * @version $Revision: 1.4 $
 */
class HelpMenu extends JMenu {
    public HelpMenu() {
        super( "Help" );

        JMenuItem simLauncherHelp = new JMenuItem( "SimLauncher help");
        simLauncherHelp.addActionListener( new SimLauncherHelpAction() );
        add( simLauncherHelp );

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
        final String msg = Configuration.instance().getProgramName() + "\n\n" +
                           "A program for installing, launching, and managing\nPhET simulations."
                           + "\n\n" + SimStrings.get( "Common.HelpMenu.VersionLabel" ) + ": " +
                           SimLauncher.getVersion() + "\n\n" + javaVersion + "\n";
        JOptionPane.showMessageDialog( this, msg, SimStrings.get( "Common.HelpMenu.AboutTitle" ) + " " + Configuration.instance().getProgramName(), JOptionPane.INFORMATION_MESSAGE );
    }

}
