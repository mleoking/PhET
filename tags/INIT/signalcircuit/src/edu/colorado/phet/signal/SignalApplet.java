/*
  cd E:\java\projects\phets\data
  C:\j2sdk1.4.0_01\bin\java phet.edu.colorado.phet.signal.SignalApplet
  java phet.edu.colorado.phet.signal.SignalApplet
*/

package edu.colorado.phet.signal;

import edu.colorado.phet.phys2d.laws.Validate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//import edu.colorado.phet.util.ExitOnClose;

public class SignalApplet extends JApplet {
    public static boolean applet = true;

    public SignalApplet() {
        int width = 600;
        int height = 300;
        int barrierX = 100;
        int barrierWidth = 300;
        int numElectrons = 40;
        int waitTime = 20;
        double dt = .021;

        int x = 20;
        int y = 20;
        int seed = 0;

        Signal s = new Signal( width, height, applet );
        s.getSystem().addLaw( new Validate( this ) );

// 	System2D sys=b.getSystem();
// 	SystemRunner sr=new SystemRunner(sys,dt,waitTime);
// 	SystemRunnerControl src=new SystemRunnerControl(new Range(.01,.3),dt,new Range(5,40),waitTime,sr);

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( s.getPanel(), BorderLayout.CENTER );

        JPanel south = new JPanel();
        getContentPane().add( s.getControlPanel(), BorderLayout.SOUTH );
        getContentPane().validate();
    }

    public static void main( String[] args ) {
        SignalApplet.applet = false;
        JFrame f = new JFrame();
        f.setContentPane( new SignalApplet() );
        f.setSize( new Dimension( 850, 435 ) );
        f.setVisible( true );
        edu.colorado.phet.util.ThreadHelper.quietNap( 800 );
//        f.addWindowListener(new ExitOnClose());
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.getFocusOwner().addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent ke ) {
                System.exit( 0 );
            }
        } );
    }
}
