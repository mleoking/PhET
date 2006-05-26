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

import edu.colorado.phet.simlauncher.InstalledSimsPane;
import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.TopLevelPane;
import edu.colorado.phet.simlauncher.UninstalledSimsPane;
import edu.colorado.phet.simlauncher.actions.*;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * SimLauncherMenuBar
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimLauncherMenuBar extends JMenuBar {

    public SimLauncherMenuBar() {
        add( new FileMenu() );
        add( new SimulationMenu() );
        add( new SimulationsViewMenu() );
        add( new OptionsMenu() );
        add( new HelpMenu() );
    }

    private class FileMenu extends JMenu {
        public FileMenu() {
            super( "File" );
            add( new JMenuItem( new ClearCacheAction() ));
            add( new JMenuItem( new ExitAction() ) );
        }
    }

    private class SimulationMenu extends JMenu {
        private JMenuItem launchMI;
        private JMenuItem updateMI;
        private JMenuItem uninstallMI;
        private JMenuItem installMI;

        public SimulationMenu() {
            super( "Simulation" );

            // Menu items for installed simulations
            launchMI = new JMenuItem( "Launch simulation" );
            launchMI.addActionListener( new LaunchSimulationAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
            add( launchMI );
            updateMI = new JMenuItem( "Check for updates" );
            updateMI.addActionListener( new UpdateSimulationAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
            add( updateMI );
            uninstallMI = new JMenuItem( "Uninstall" );
            uninstallMI.addActionListener( new UninstallSimulationAction( this, TopLevelPane.getInstance().getInstalledSimsPane() ) );
            add( uninstallMI );

            //Menu items for uninstalled simulations
            add( new JPopupMenu.Separator() );
            installMI = new JMenuItem( "Install" );
            installMI.addActionListener( new InstallSimulationAction( this,
                                                                      TopLevelPane.getInstance().getUninstalledSimsPane() ) );
            add( installMI );

            // Set up listeners that will cause the proper menu items to be enabled and disabled when
            // certain things happen in the UI
            TopLevelPane.getInstance().getInstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
                public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                    enableItems();
                }
            } );

            TopLevelPane.getInstance().getUninstalledSimsPane().addChangeListener( new ChangeEventChannel.ChangeListener() {
                public void stateChanged( ChangeEventChannel.ChangeEvent event ) {
                    enableItems();
                }
            } );

            TopLevelPane.getInstance().addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    enableItems();
                }
            } );

            // Enable/disable the appropriate menu items
            enableItems();
        }

        /**
         * Enables/disables menu items according to the state of the UI
         */
        private void enableItems() {
            InstalledSimsPane isp = TopLevelPane.getInstance().getInstalledSimsPane();
            boolean installedItemsEnabled = isp.getSimulation() != null;
            UninstalledSimsPane usp = TopLevelPane.getInstance().getUninstalledSimsPane();
            boolean uninstalledItemsEnabled = usp.getSimulation() != null;

            JTabbedPane jtp = TopLevelPane.getInstance();
            Component component = jtp.getSelectedComponent();
            boolean installedPaneSelected = component == TopLevelPane.getInstance().getInstalledSimsPane();
            boolean uninstalledPaneSelected = component == TopLevelPane.getInstance().getUninstalledSimsPane();

            installedItemsEnabled &= installedPaneSelected;
            uninstalledItemsEnabled &= uninstalledPaneSelected;

            // Menu items for installed simulations
            launchMI.setEnabled( installedItemsEnabled );
            updateMI.setEnabled( installedItemsEnabled );
            uninstallMI.setEnabled( installedItemsEnabled );

            // Menu items for uninstalled simulations
            installMI.setEnabled( uninstalledItemsEnabled );
        }
    }

    private class OptionsMenu extends JMenu {
        public OptionsMenu() {
            super( "Options" );
            JCheckBoxMenuItem autoUpdateOption = new JCheckBoxMenuItem( "Automatically check for updates" );
            add( autoUpdateOption );
            autoUpdateOption.addActionListener( new AutoUpdateAction() );
        }
    }

    private class SimulationsViewMenu extends JMenu {
        public SimulationsViewMenu() {
            super( "View" );
            add( new JMenuItem( new SimListingOptionsAction() ) );
            JMenu subMenu = new JMenu( "Sort installed simulations" );
            subMenu.add( new JMenuItem( "Alphabetical" ) );
            subMenu.add( new JMenuItem( "Most recently used first" ) );
            JMenuItem customMI = new JMenuItem( "Custom" );
            subMenu.add( customMI );
            customMI.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JOptionPane.showMessageDialog( SimulationsViewMenu.this, "User can reorder the simulations in the list by drag-and-drop, \nthen have that order preserved ");
                }
            } );
            subMenu.setVisible( true );
            add( subMenu );
        }

    }

    private class HelpMenu extends JMenu {
        public HelpMenu() {
            super( "Help" );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Actions
    //--------------------------------------------------------------------------------------------------

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
            super( "Simulation information..." );
        }

        public void actionPerformed( ActionEvent e ) {

            // Installed simulations
            JLabel installedSimsSectionLabel = new JLabel( "Installed Simulations" );
            final JCheckBoxMenuItem iconOptionCB_A = new JCheckBoxMenuItem( "Show thumbnails" );
            iconOptionCB_A.addActionListener( new AbstractAction() {
                public void actionPerformed( ActionEvent e ) {
                    Options.instance().setShowInstalledThumbnailsNoUpdate( iconOptionCB_A.isSelected() );
                }
            } );
            iconOptionCB_A.setSelected( Options.instance().isShowInstalledThumbnails() );

            JCheckBoxMenuItem abstractCB_A = new JCheckBoxMenuItem( "Show description" );

            // Unistalled simulations
            JLabel uninstalledSimsSectionLabel = new JLabel( "Uninstalled Simulations" );
            final JCheckBoxMenuItem iconOptionCB_B = new JCheckBoxMenuItem( "Show thumbnails" );
            iconOptionCB_B.addActionListener( new AbstractAction() {
                public void actionPerformed( ActionEvent e ) {
                    Options.instance().setShowUninstalledThumbnailsNoUpdate( iconOptionCB_B.isSelected() );
                }
            } );
            iconOptionCB_B.setSelected( Options.instance().isShowUninstalledThumbnails() );

            JCheckBoxMenuItem abstractCB_B = new JCheckBoxMenuItem( "Show description" );

            // Lay out the panel for the dialog
            JPanel optionsPane = new JPanel( new GridBagLayout() );
            Insets sectionHeaderInsets = new Insets( 5, 0, 0, 0 );
            Insets optionInsets = new Insets( 0, 10, 0, 0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             sectionHeaderInsets,
                                                             0, 0 );

            optionsPane.add( installedSimsSectionLabel, gbc );
            gbc.insets = optionInsets;
            optionsPane.add( iconOptionCB_A, gbc );
            optionsPane.add( abstractCB_A, gbc );
            gbc.insets = sectionHeaderInsets;
            optionsPane.add( uninstalledSimsSectionLabel, gbc );
            gbc.insets = optionInsets;
            optionsPane.add( iconOptionCB_B, gbc );
            optionsPane.add( abstractCB_B, gbc );

            int option = JOptionPane.showOptionDialog( SimLauncherMenuBar.this, optionsPane,
                                                       "Simulation Listing Options",
                                                       JOptionPane.OK_CANCEL_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE,
                                                       null, null,
                                                       JOptionPane.OK_OPTION );
            if( option == JOptionPane.OK_OPTION ) {
                Options.instance().notifyListeners();
            }
        }
    }

    class AutoUpdateAction extends AbstractAction {
        public void actionPerformed( ActionEvent e ) {
            JCheckBoxMenuItem jcbmi = (JCheckBoxMenuItem)e.getSource();
            if( jcbmi.isSelected() ) {
                JOptionPane.showMessageDialog( SimLauncherMenuBar.this, "Updates for all simulations will be checked for\n"
                                                                        + "each time the launcher is started" );
            }
        }
    }
}
