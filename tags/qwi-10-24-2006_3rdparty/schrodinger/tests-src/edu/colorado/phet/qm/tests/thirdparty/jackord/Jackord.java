package edu.colorado.phet.qm.tests.thirdparty.jackord;/*

http://www.kw.igs.net/~jackord/bp/n4.html


 * nm4a.java - revised 19 Apr 05 - width 385, height 261
 * @author jack@ord.ca
 * quantum rectilinear pluck and pulse wavefunctions,
 *   and a standing or travelling localized sinusoidal wavefunction
 *   in a square well with time dependence calculated either directly
 *   from the Schrodinger Equation or from FFSS expansion
 * 320 frames (looping 600 times/frame), 192 elements (and FFSS terms)
 */

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Jackord extends Applet
        implements ActionListener, Runnable {
    int kk = 0;
    boolean running = false;
    int first = 0;                    // Declarations
    String b4s = "Pulse B";
    Button b4 = new Button( b4s );
    String b5s = "Motion";
    Button b5 = new Button( b5s );
    Image bim;
    Graphics bgr;
    Thread tmr;
    private int NUM_LATTICE_PTS;
    private double normValue = 63.6396;

    public Jackord() {
        NUM_LATTICE_PTS = 193;
    }

    public void init() {
        setBackground( new Color( 211, 211, 211 ) );
        add( b4 );
        add( b5 );
        b4.addActionListener( this );
        b5.addActionListener( this );
    }

    public void start() {
        if( tmr == null ) {
            tmr = new Thread( this );
            tmr.setPriority( Thread.MIN_PRIORITY );
            tmr.start();
        }
    }

    public void stop() {
        tmr = null;
        running = true;
    }

    public void run() {
        repaint();
    }

    public void paint( Graphics g ) {
        int n, nf, c, frame, timeStep, delt;                       // Declarations
        double[] yReal = new double[NUM_LATTICE_PTS];
        double[] yReal2 = new double[NUM_LATTICE_PTS];
        double[] dyReal = new double[NUM_LATTICE_PTS];
        double[] yImag = new double[NUM_LATTICE_PTS];
        double[] yImag2 = new double[NUM_LATTICE_PTS];
        double[] dyImag = new double[NUM_LATTICE_PTS];

        int[] polygonXValues = new int[NUM_LATTICE_PTS];
        int[] polygonYValues = new int[NUM_LATTICE_PTS];
        double dt, pi, phi, energy;
        n = NUM_LATTICE_PTS - 1;
        nf = 320;
        c = 600;                              // Loop constants
        pi = Math.PI;
        dt = n * n / 4 / pi / c / nf;
        energy = 0; // d(t/T1) * constants
        g.setColor( Color.black );
        g.drawRect( 0, 0, 384, 260 );
        if( first == 0 ) {
            bim = createImage( 385, 261 );                         // Animation buffer
            bgr = bim.getGraphics();
            first = 1;
        }
        if( kk > 0 ) {
            if( kk > 2 ) {                                      // Init pulses A and B
                for( int i = 3 * n / 8; i <= 5 * n / 8; i = i + 1 ) {
                    phi = ( i - 3 * n / 8 ) * pi * 8 / n;
                    yReal[i] = 7.5 * ( 1 - Math.cos( phi ) );
                    if( kk == 4 ) {
                        yImag[i] = -yReal[i] * Math.sin( phi );
                        yReal[i] = yReal[i] * Math.cos( phi );
                    }
                }
                energy = 21.333;
                if( kk == 4 ) { energy = 64 + energy; }
            }

            for( int i = 1; i <= n - 1; i = i + 1 ) {                 // Initialize dy step
                dyReal[i] = dt * ( yImag[i - 1] + yImag[i + 1] - 2 * yImag[i] );
                dyImag[i] = -dt * ( yReal[i - 1] + yReal[i + 1] - 2 * yReal[i] );
            }
            for( int i = 0; i <= n; i = i + 1 ) {
                polygonXValues[i] = 2 * i;
            }
            frame = 0;
            polygonYValues[0] = 260;
            polygonYValues[n] = 260;
            energy = (int)( energy * 1000 ) / 1000.;
            long time = System.currentTimeMillis();
            timeStep = 60;      // Timing
            do {                                             // Frame loop
                for( int i = 1; i <= n - 1; i = i + 1 ) {                 // To plot yy[i]...
                    polygonYValues[i] = 260 - (int)( yReal[i] * yReal[i] + yImag[i] * yImag[i] + .5 );
                }
                bgr.setColor( getBackground() );                 // Set up plot buffer
                bgr.fillRect( 0, 0, 384, 260 );
                bgr.setColor( Color.black );
                bgr.drawRect( 0, 0, 384, 260 );
                bgr.setFont( new Font( "TimesRoman", Font.PLAIN, 14 ) );
                bgr.drawString( "Frame " + frame + "/" + nf, 9, 50 );
                bgr.drawString( "E/gs = " + energy, 9, 70 );
                bgr.setColor( Color.blue );
                bgr.fillPolygon( polygonXValues, polygonYValues, n + 1 );                  // Draw filled polygon
                g.drawImage( bim, 0, 0, null );                  // Show plot buffer
                frame = frame + 1;
                // Schroginger equation
                for( int j = 1; j <= c; j = j + 1 ) {             // Accuracy loop
                    for( int i = 1; i <= n - 1; i = i + 1 ) {             // Project ahead dy/2
                        yReal2[i] = yReal[i] + dyReal[i] / 2;
                        yImag2[i] = yImag[i] + dyImag[i] / 2;
                    }
                    for( int i = 1; i <= n - 1; i = i + 1 ) {
                        dyReal[i] = dt * ( yImag2[i - 1] + yImag2[i + 1] - 2 * yImag2[i] );
                        yReal[i] = yReal[i] + dyReal[i];
                        dyImag[i] = -dt * ( yReal2[i - 1] + yReal2[i + 1] - 2 * yReal2[i] );
                        yImag[i] = yImag[i] + dyImag[i];
                    }
                }
                renormalize( yReal, yImag );
                if( time > System.currentTimeMillis() ) { delt = timeStep; }
                else { delt = 0; }
                try {
                    Thread.sleep( delt );
                }
                catch( InterruptedException e ) {
                    stop();
                }
                time = time + timeStep;
                if( frame == nf + 1 ) {
                    kk = 0;
                    running = false;
                }
            } while( running );
        }
        stop();
    }

    private void renormalize( double[] yReal, double[] yImag ) {
        double sum = 0;
        for( int i = 0; i < yReal.length; i++ ) {
            sum += yReal[i] * yReal[i] + yImag[i] * yImag[i];
        }
        sum = Math.sqrt( sum );
        System.out.println( "sum = " + sum );
        sum /= normValue;
        for( int i = 0; i < yReal.length; i++ ) {
            yReal[i] /= sum;
            yImag[i] /= sum;
        }
    }

    public void actionPerformed( ActionEvent e ) {         // Buttons
        String tst;
        tst = e.getActionCommand();
        if( b4s.equals( tst ) ) { kk = 4; }
        if( b5s.equals( tst ) ) { if( kk > 0 ) { running = true;} }
        start();
    }
}
