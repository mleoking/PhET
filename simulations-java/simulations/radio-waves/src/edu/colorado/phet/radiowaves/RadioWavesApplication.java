/**
 * Class: EmfApplication Package: edu.colorado.phet.emf Author: Another Guy
 * Date: May 23, 2003
 */

package edu.colorado.phet.radiowaves;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_1200.application.ApplicationModel;
import edu.colorado.phet.common_1200.application.PhetApplication;
import edu.colorado.phet.common_1200.view.PhetFrame;
import edu.colorado.phet.radiowaves.view.WaveMediumGraphic;

public class RadioWavesApplication {

    public static double s_speedOfLight = 6;

    public static void main( String[] args ) {
        new PhetLookAndFeel().initLookAndFeel();

        // Initialize simulation strings using resource bundle for the locale.
        SimStrings.setStrings( EmfConfig.localizedStringsPath );//todo: add String[] args to this
        SimStrings.getInstance().addStrings( "localization/phetcommon-strings" );//todo: add String[] args to this

        ConstantDtClock clock = new ConstantDtClock( 40, 0.5 );
        final EmfModule antennaModule = new EmfModule( clock );
        FrameSetup fs = new FrameSetup.CenteredWithSize( 1024, 768 );
        ApplicationModel appDescriptor = new ApplicationModel( new String( SimStrings.get( "radio-waves.name" ) + " (" + EmfConfig.VERSION + ")" ), SimStrings.get( "radio-waves.description" ), EmfConfig.VERSION, fs );
        appDescriptor.setModule( antennaModule );
        appDescriptor.setInitialModule( antennaModule );
        appDescriptor.setClock( clock );
        appDescriptor.setName( "radiowaves" );

        PhetApplication application = new PhetApplication( appDescriptor );
        PhetFrame frame = application.getApplicationView().getPhetFrame();
        PhetFrame.setDefaultLookAndFeelDecorated( true );

        // Add an options menu
        JMenu optionsMenu = new JMenu( "Options" );
        final JCheckBoxMenuItem fadeScalarRepCB = new JCheckBoxMenuItem( "Fade scalar representation" );
        fadeScalarRepCB.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                WaveMediumGraphic.Y_GRADIENT = fadeScalarRepCB.isSelected();
                antennaModule.setFieldSense( antennaModule.getFieldSense() );
            }
        } );
        optionsMenu.add( fadeScalarRepCB );

        // Prevent the frame from being resized
        frame.setResizable( false );

        Runtime.getRuntime().gc();
        application.startApplication();
    }

}
