/*
  cd E:\java\projects\phets\data
  C:\j2sdk1.4.0_01\bin\java phet.edu.colorado.phet.signal.SignalApplet
  java phet.edu.colorado.phet.signal.SignalApplet
*/

package edu.colorado.phet.signalcircuit;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.signalcircuit.phys2d.laws.Validate;


public class SignalCircuitSimulationPanel extends JApplet {
    // Localization
    public static final String localizedStringsPath = "signal-circuit/localization/signal-circuit-strings";
    private static final String VERSION = PhetApplicationConfig.getVersion( "signal-circuit" ).formatForTitleBar();

    public SignalCircuitSimulationPanel() {
        int width = 600;
        int height = 300;

        Signal s = new Signal( width, height, false );
        s.getSystem().addLaw( new Validate( this ) );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( s.getPanel(), BorderLayout.CENTER );

        getContentPane().add( s.getControlPanel(), BorderLayout.SOUTH );
        getContentPane().validate();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PhetLookAndFeel().initLookAndFeel();
                SimStrings.getInstance().init( args, localizedStringsPath );
                JFrame f = new JFrame( SimStrings.getInstance().getString( "SignalCircuitApplication.title" ) + " (" + VERSION + ")" );
                f.setContentPane( new SignalCircuitSimulationPanel() );
                f.setSize( new Dimension( 850, 435 ) );
                f.setVisible( true );
                f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            }
        } );
    }

}
