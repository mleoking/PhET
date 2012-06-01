// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.energyformsandchanges.energysystems.EnergySystemsModule;
import edu.colorado.phet.energyformsandchanges.intro.EFACIntroModule;
import edu.colorado.phet.energyformsandchanges.intro.view.EFACIntroCanvas;

/**
 * Main application class for PhET's Energy Forms and Changes simulation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class EnergyFormsAndChangesApplication extends PiccoloPhetApplication {

    public EnergyFormsAndChangesApplication( PhetApplicationConfig config ) {
        super( config );

        // Create the modules
        addModule( new EFACIntroModule() );
        addModule( new EnergySystemsModule() );
        initMenuBar();
    }

    private void initMenuBar() {
        final PhetFrame frame = getPhetFrame();

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();

        // Add an item for controlling whether unstable nuclei are animated.
        final JCheckBoxMenuItem animateNucleusCheckBox = new JCheckBoxMenuItem( "Show Dump Energies Button" ) {{
            setSelected( EFACIntroCanvas.showDumpEnergiesButton.get() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    EFACIntroCanvas.showDumpEnergiesButton.set( isSelected() );
                }
            } );
        }};

        developerMenu.add( animateNucleusCheckBox );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, EnergyFormsAndChangesResources.PROJECT_NAME, "energy-forms-and-changes", EnergyFormsAndChangesApplication.class );
    }
}