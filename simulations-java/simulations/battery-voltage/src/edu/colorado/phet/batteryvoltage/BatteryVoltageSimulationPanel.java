/*
  java edu.colorado.phet.batteryvoltage.BatteryApplet
*/

package edu.colorado.phet.batteryvoltage;

import java.awt.*;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.batteryvoltage.common.phys2d.System2D;
import edu.colorado.phet.batteryvoltage.common.phys2d.SystemRunner;
import edu.colorado.phet.batteryvoltage.common.phys2d.gui.Range;
import edu.colorado.phet.batteryvoltage.common.phys2d.gui.SystemRunnerControl;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class BatteryVoltageSimulationPanel extends JApplet {
    private SystemRunnerControl timeControls;

    public BatteryVoltageSimulationPanel() {
        int width = 500;
        int height = 300;
        int barrierX = 100;
        int barrierWidth = 300;
        int numElectrons = 30;

        int x = 20;
        int y = 20;
        int seed = 0;
        int numMen = 7;
        double dt = .021;
        Battery b = new Battery( x, y, width, height, barrierX, barrierWidth, numElectrons, new Random( seed ), numMen, dt );

        int waitTime = 20;
        System2D sys = b.getSystem();
        SystemRunner sr = new SystemRunner( sys, dt, waitTime );
        timeControls = new SystemRunnerControl( new Range( .005, .08 ), dt, new Range( 0, 100 ), waitTime, sr );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( b.getJPanel(), BorderLayout.CENTER );

        JPanel south = new JPanel();
        getContentPane().add( south, BorderLayout.SOUTH );
        south.setLayout( new BorderLayout() );

        south.add( b.getControlPanel(), BorderLayout.CENTER );
        getContentPane().validate();

        new Thread( sr ).start();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                phetLookAndFeel.initLookAndFeel();
                PhetApplicationConfig phetApplicationConfig = new PhetApplicationConfig( args, new FrameSetup.NoOp(), BatteryVoltageResources.getResourceLoader() );
                JFrame f = new JFrame( phetApplicationConfig.getName() + " (" + phetApplicationConfig.getVersion().formatForTitleBar() + ")" );
                f.setContentPane( new BatteryVoltageSimulationPanel() );
                f.setSize( new Dimension( 850, 525 ) );
                f.setVisible( true );
                f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            }
        } );
    }
}
