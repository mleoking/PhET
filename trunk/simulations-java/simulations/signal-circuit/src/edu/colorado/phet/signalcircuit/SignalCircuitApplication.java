/*
  cd E:\java\projects\phets\data
  C:\j2sdk1.4.0_01\bin\java phet.edu.colorado.phet.signal.SignalApplet
  java phet.edu.colorado.phet.signal.SignalApplet
*/

package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.phys2d.laws.Validate;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

//import edu.colorado.phet.util.ExitOnClose;

public class SignalCircuitApplication extends JApplet {
    public static boolean applet = true;

    // Localization
    public static final String localizedStringsPath = "signal-circuit/localization/signal-circuit-strings";
    private static final String VERSION = PhetApplicationConfig.getVersion( "signal-circuit" ).formatForTitleBar();

    public SignalCircuitApplication() {
        int width = 600;
        int height = 300;

        if( applet ) {
            String applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.phet.locale", null );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                Locale.setDefault( new Locale( applicationLocale ) );
            }
            SimStrings.getInstance().addStrings( localizedStringsPath );
        }

        Signal s = new Signal( width, height, applet );
        s.getSystem().addLaw( new Validate( this ) );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( s.getPanel(), BorderLayout.CENTER );

        getContentPane().add( s.getControlPanel(), BorderLayout.SOUTH );
        getContentPane().validate();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                runMain( args );
            }
        } );
    }

    private static void runMain( String[] args ) {
        SimStrings.getInstance().init( args, localizedStringsPath );

        applet = false;
        JFrame f = new JFrame( SimStrings.getInstance().getString( "SignalCircuitApplication.title" ) + " (" + VERSION + ")" );
        f.setContentPane( new SignalCircuitApplication() );
        f.setSize( new Dimension( 850, 435 ) );
        f.setVisible( true );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
