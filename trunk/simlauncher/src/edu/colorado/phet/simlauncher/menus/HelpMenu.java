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
import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.SimLauncher;
import edu.colorado.phet.simlauncher.actions.SimLauncherHelpAction;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * HelpMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class HelpMenu extends JMenu {
    public HelpMenu() {
        super( "Help" );

        JMenuItem simLauncherHelp = new JMenuItem( "SimLauncher help");
        simLauncherHelp.addActionListener( new SimLauncherHelpAction() );
        add( simLauncherHelp );


        // Find the HelpSet file and create the HelpSet object:
        String helpHS = "help/SimLauncherHelp.hs";
        ClassLoader cl = this.getClass().getClassLoader();
        HelpSet hs = null;
        try {
            URL hsURL = HelpSet.findHelpSet( cl, helpHS );
            hs = new HelpSet( null, hsURL );
        }
        catch( Exception ee ) {
            // Say what the exception really is
            System.out.println( "HelpSet " + ee.getMessage() );
            System.out.println( "HelpSet " + helpHS + " not found" );
            return;
        }
        // Create a HelpBroker object:
        HelpBroker hb = hs.createHelpBroker();
        // Create a "Help" menu item to trigger the help viewer:
        JMenuItem helpTopicsMI = new JMenuItem( "Help Topics");
        helpTopicsMI.addActionListener(new CSH.DisplayHelpFromSource( hb )  );
        add(helpTopicsMI);


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
