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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * SimLauncherMenuBar
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncherMenuBar extends JMenuBar {
    private JFrame frame;

    public SimLauncherMenuBar( JFrame frame ) {
        this.frame = frame;
        add( new FileMenu() );
        add( new SimulationsMenu() );
        add( new OptionsMenu() );
        add( new HelpMenu() );
    }

    private class FileMenu extends JMenu {
        public FileMenu() {
            super( "File" );
            add( new JMenuItem( new ExitAction() ) );
        }
    }

    private class SimulationsMenu extends JMenu {
        public SimulationsMenu() {
            super( "Simulations" );
            add( new JMenuItem( "Launch simulation" ) );
            add( new JMenuItem( "Check for updates" ) );
        }
    }

    private class OptionsMenu extends JMenu {
        public OptionsMenu() {
            super( "Options" );
            add( new JCheckBox( "Automatically check for updates" ) );
            add( new JMenuItem( new SimListingOptionsAction() ) );
        }
    }

    private class HelpMenu extends JMenu {
        public HelpMenu() {
            super( "Help" );
        }
    }

    class ExitAction extends AbstractAction {
        public ExitAction() {
            super( "Exit" );
        }

        public void actionPerformed( ActionEvent e ) {
            System.exit( 0 );
        }
    }

    class SimListingOptionsAction extends AbstractAction {
        public SimListingOptionsAction() {
            super( "Simulation Listings..." );
        }

        public void actionPerformed( ActionEvent e ) {
            JLabel installedSimsSectionLabel = new JLabel( "Installed Simulations");
            JCheckBoxMenuItem iconOptionCB_A = new JCheckBoxMenuItem( "Show thumbnails");
            JCheckBoxMenuItem abstractCB_A = new JCheckBoxMenuItem( "Show description");
            JLabel uninstalledSimsSectionLabel = new JLabel( "Uninstalled Simulations");
            JCheckBoxMenuItem iconOptionCB_B = new JCheckBoxMenuItem( "Show thumbnails");
            JCheckBoxMenuItem abstractCB_B = new JCheckBoxMenuItem( "Show description");

            JPanel optionsPane = new JPanel( new GridBagLayout() );
            Insets sectionHeaderInsets = new Insets( 5, 0, 0, 0 );
            Insets optionInsets = new Insets( 0, 10, 0, 0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             sectionHeaderInsets,
                                                             0,0);

            optionsPane.add( installedSimsSectionLabel, gbc );
            gbc.insets = optionInsets;
            optionsPane.add( iconOptionCB_A, gbc );
            optionsPane.add( abstractCB_A, gbc );
            gbc.insets = sectionHeaderInsets;
            optionsPane.add( uninstalledSimsSectionLabel, gbc );
            gbc.insets = optionInsets;
            optionsPane.add( iconOptionCB_B, gbc );
            optionsPane.add( abstractCB_B, gbc );

            JOptionPane optionDlg = new JOptionPane( optionsPane,
                                                     JOptionPane.QUESTION_MESSAGE,
                                                     JOptionPane.OK_CANCEL_OPTION );
            JDialog dlg = optionDlg.createDialog( SimLauncherMenuBar.this, "Simulation Listing Options" );
            dlg.setVisible( true );
        }
    }
}
