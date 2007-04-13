/*
  java edu.colorado.phet.batteryvoltage.BatteryApplet
*/

package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.batteryvoltage.common.view.util.SimStrings;
import edu.colorado.phet.batteryvoltage.common.electron.utils.ImageLoader;
import edu.colorado.phet.batteryvoltage.common.electron.utils.ResourceLoader4;
import edu.colorado.phet.batteryvoltage.common.phys2d.System2D;
import edu.colorado.phet.batteryvoltage.common.phys2d.SystemRunner;
import edu.colorado.phet.batteryvoltage.common.phys2d.gui.Range;
import edu.colorado.phet.batteryvoltage.common.phys2d.gui.SystemRunnerControl;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Random;

public class BatteryVoltageApplication extends JApplet {
    private SystemRunnerControl timeControls;

    public static boolean applet = true;

    // Localization
    public static final String localizedStringsPath = "localization/BatteryVoltageStrings";

    public BatteryVoltageApplication() {
        if ( applet ) {
            String applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.locale", null );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                SimStrings.setLocale( new Locale( applicationLocale ) );
            }
            SimStrings.setStrings( localizedStringsPath );
        }

        int width = 500;
        int height = 300;
        int barrierX = 100;
        int barrierWidth = 300;
        int numElectrons = 30;
        //ImageLoader i = new ResourceLoader2((URLClassLoader)getClass().getClassLoader(), this);
        ImageLoader i = new ResourceLoader4( getClass().getClassLoader(), this );

        int x = 20;
        int y = 20;
        int seed = 0;
        int numMen = 7;
        double dt = .021;
        Battery b = new Battery( x, y, width, height, barrierX, barrierWidth, i, numElectrons, new Random( seed ), numMen, dt );

        int waitTime = 20;
        //double dt=.031;
        System2D sys = b.getSystem();
        SystemRunner sr = new SystemRunner( sys, dt, waitTime );
        timeControls = new SystemRunnerControl( new Range( .005, .08 ), dt, new Range( 0, 100 ), waitTime, sr );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( b.getJPanel(), BorderLayout.CENTER );

        JPanel south = new JPanel();
        getContentPane().add( south, BorderLayout.SOUTH );
        south.setLayout( new BorderLayout() );

//        south.add(timeControls.getJPanel(), BorderLayout.CENTER);
        south.add( b.getControlPanel(), BorderLayout.CENTER );
        getContentPane().validate();

        new Thread( sr ).start();
    }

    public static void main( String[] args ) {
        SimStrings.init( args, localizedStringsPath );
        
        applet = false;

        JFrame f = new JFrame();
        f.setContentPane( new BatteryVoltageApplication() );
        f.setSize( new Dimension( 850, 525 ) );
        f.setVisible( true );
//        f.addWindowListener( new ExitOnClose() );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
