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

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.photoelectric.controller.BeamControl;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


    public PhotoelectricApplication( String[] args ) {
        super( args,
               SimStrings.getInstance().getString( "photoelectric.name" ),
               SimStrings.getInstance().getString( "photoelectric.description" ),
               PhotoelectricConfig.VERSION,
               FRAME_SETUP );

        // Make the frame non-resizable until we make the intensity slider a graphic
        getPhetFrame().setResizable( false );

        // Get a reference to the Options menu. The module will need it
        optionsMenu = new JMenu( SimStrings.getInstance().getString( "Menu.Options" ) );

        final PhotoelectricModule photoelectricModule = new PhotoelectricModule( this );
        setModules( new Module[]{photoelectricModule} );

        // Add an option to show photons
        final JCheckBoxMenuItem photonMI = new JCheckBoxMenuItem( SimStrings.getInstance().getString( "Menu.PhotonView" ) );
        photonMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                photoelectricModule.setPhotonViewEnabled( photonMI.isSelected() );
            }
        } );
        optionsMenu.add( photonMI );

        final JCheckBoxMenuItem beamRateMI = new JCheckBoxMenuItem( SimStrings.getInstance().getString( "Options.photonsPerSecond" ) );
        beamRateMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( beamRateMI.isSelected() ) {
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
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PhetLookAndFeel().initLookAndFeel();
                SimStrings.getInstance().init( args, PhotoelectricConfig.LOCALIZATION_RESOURCE_NAME );
                new PhotoelectricApplication( args ).startApplication();
            }
        } );
    }
}
