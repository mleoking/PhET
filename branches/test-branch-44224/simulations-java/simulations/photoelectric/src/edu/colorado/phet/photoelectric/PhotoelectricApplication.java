/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.photoelectric.controller.BeamControl;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;

/**
 * PhotoelectricApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricApplication extends PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 680 );

    // Clock specification
    public static final double DT = 12;
    public static final int FPS = 25;
    private JMenu optionsMenu;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------


    public PhotoelectricApplication( PhetApplicationConfig config ) {
        super( config );

        // Make the frame non-resizable until we make the intensity slider a graphic
        getPhetFrame().setResizable( false );

        // Get a reference to the Options menu. The module will need it
        optionsMenu = new JMenu( PhotoelectricResources.getString( "Menu.Options" ) );

        final PhotoelectricModule photoelectricModule = new PhotoelectricModule( getPhetFrame(), this );
        setModules( new Module[]{photoelectricModule} );

        // Add an option to show photons
        final JCheckBoxMenuItem photonMI = new JCheckBoxMenuItem( PhotoelectricResources.getString( "Menu.PhotonView" ) );
        photonMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                photoelectricModule.setPhotonViewEnabled( photonMI.isSelected() );
            }
        } );
        optionsMenu.add( photonMI );

        final JCheckBoxMenuItem beamRateMI = new JCheckBoxMenuItem( PhotoelectricResources.getString( "Options.photonsPerSecond" ) );
        beamRateMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( beamRateMI.isSelected() ) {
                    photoelectricModule.getBeamControl().setMode( BeamControl.RATE );
                }
                else {
                    photoelectricModule.getBeamControl().setMode( BeamControl.INTENSITY );
                }
            }
        } );
        optionsMenu.add( beamRateMI );

        getPhetFrame().addMenu( optionsMenu );
    }

    public JMenu getOptionsMenu() {
        return optionsMenu;
    }

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new PhotoelectricApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, PhotoelectricConfig.PROJECT_NAME );
        appConfig.setFrameSetup( FRAME_SETUP );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
