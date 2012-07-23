// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.energyformsandchanges.intro.EFACIntroModule;
import edu.colorado.phet.energyformsandchanges.intro.dev.HeatTransferValuesDialog;
import edu.colorado.phet.energyformsandchanges.intro.view.EFACIntroCanvas;

/**
 * Main application class for PhET's Energy Forms and Changes simulation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class EnergyFormsAndChangesApplication extends PiccoloPhetApplication {

    private JCheckBoxMenuItem showHeatTransferValuesDialogCheckBox;
    private HeatTransferValuesDialog heatTransferValuesDialog = null;

    public EnergyFormsAndChangesApplication( PhetApplicationConfig config ) {
        super( config );

        // Create the modules
        addModule( new EFACIntroModule() );
//        addModule( new EnergySystemsModule() );
//        addModule( new EnergyStoriesModule() );
        initMenuBar();
    }

    private void initMenuBar() {
        final PhetFrame frame = getPhetFrame();

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();

        // Add a check box for controlling whether the button for dumping energy values is visible.
        final JCheckBoxMenuItem showDumpEnergiesButtonCheckBox = new JCheckBoxMenuItem( "Show Dump Energies Button" ) {{
            setSelected( EFACIntroCanvas.showDumpEnergiesButton.get() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    EFACIntroCanvas.showDumpEnergiesButton.set( isSelected() );
                }
            } );
        }};

        // Add a check box for controlling whether the button for dumping energy values is visible.
        showHeatTransferValuesDialogCheckBox = new JCheckBoxMenuItem( "Show Heat Transfer Values Dialog" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setHeatTransferValuesDialogVisible( isSelected() );
                }
            } );
        }};

        developerMenu.add( showDumpEnergiesButtonCheckBox );
        developerMenu.add( showHeatTransferValuesDialogCheckBox );
    }

    private void setHeatTransferValuesDialogVisible( boolean isVisible ) {
        if ( isVisible && heatTransferValuesDialog == null ) {
            // Dialog hasn't been created yet, so create it.
            heatTransferValuesDialog = new HeatTransferValuesDialog( getPhetFrame() );

            // Just hide when closed so its position is retained.
            heatTransferValuesDialog.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );

            // Center the window on the screen (initially - it will retain its
            // position when moved after that).
            heatTransferValuesDialog.setLocationRelativeTo( null );

            // Clear the check box if the user closes this by closing the
            // dialog itself.
            heatTransferValuesDialog.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    showHeatTransferValuesDialogCheckBox.setSelected( false );
                }
            } );
        }

        if ( heatTransferValuesDialog != null ) {
            heatTransferValuesDialog.setVisible( isVisible );
        }
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, EnergyFormsAndChangesResources.PROJECT_NAME, "energy-forms-and-changes", EnergyFormsAndChangesApplication.class );
    }
}