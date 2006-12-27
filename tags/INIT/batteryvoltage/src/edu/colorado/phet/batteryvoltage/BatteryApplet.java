/*
  java edu.colorado.phet.batteryvoltage.BatteryApplet
*/

package edu.colorado.phet.batteryvoltage;

import electron.utils.ImageLoader;
import electron.utils.ResourceLoader4;
import phys2d.System2D;
import phys2d.SystemRunner;
import phys2d.gui.Range;
import phys2d.gui.SystemRunnerControl;
import util.ExitOnClose;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class BatteryApplet extends JApplet {
    private SystemRunnerControl timeControls;

    public BatteryApplet() {
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
        JFrame f = new JFrame();
        f.setContentPane( new BatteryApplet() );
        f.setSize( new Dimension( 850, 525 ) );
        f.setVisible( true );
        f.addWindowListener( new ExitOnClose() );
    }
}
