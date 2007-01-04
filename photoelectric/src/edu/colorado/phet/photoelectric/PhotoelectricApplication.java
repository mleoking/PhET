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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;
import edu.colorado.phet.photoelectric.controller.BeamControlModeAction;
import edu.colorado.phet.photoelectric.controller.BeamControl;

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

    static private String resourceBundleName = "localization/PhotoelectricStrings";
    static private String description = SimStrings.get( "PhotoelectricApplication.description" );
    static private String version = "1.02";
    static private FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 680 );

    // Clock specification
    public static final double DT = 12;
    public static final int FPS = 25;
    private JMenu optionsMenu;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------


    public PhotoelectricApplication( String[] args ) {
        super( args,
               SimStrings.get( "PhotoelectricApplication.title" ),
               SimStrings.get( "PhotoelectricApplication.description" ),
               PhotoelectricConfig.VERSION,
               frameSetup );

        // Make the frame non-resizable until we make the intensity slider a graphic
        getPhetFrame().setResizable( false );

        // Get a reference to the Options menu. The module will need it
        optionsMenu = new JMenu( SimStrings.get( "Menu.Options" ) );

        final PhotoelectricModule photoelectricModule = new PhotoelectricModule( this );
        setModules( new Module[]{photoelectricModule} );

        // Add an option to show photons
        final JCheckBoxMenuItem photonMI = new JCheckBoxMenuItem( SimStrings.get( "Menu.PhotonView" ) );
        photonMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                photoelectricModule.setPhotonViewEnabled( photonMI.isSelected() );
            }
        } );
        optionsMenu.add( photonMI );

        final JCheckBoxMenuItem beamRateMI = new JCheckBoxMenuItem( SimStrings.get( "Options.photonsPerSecond" ) );
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
                SimStrings.init( args, resourceBundleName );
                new PhotoelectricApplication( args ).startApplication();
            }
        } );
    }
}
