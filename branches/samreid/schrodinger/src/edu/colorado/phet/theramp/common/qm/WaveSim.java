package edu.colorado.phet.theramp.common.qm;

/*
 * Integrating the Schrodinger Wave Equation
 */


import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

public class WaveSim extends JApplet implements Runnable {
    int wx, wy, yoff, xpts[], ypts[], nx;
    double x0, xmin, xmax, dx, ymin, ymax, dy, xscale, yscale, tmin, t, dt;
    double hbar, mass, epsilon, width, vx, vwidth, energy, energyScale;
    McComplex psi[], EtoV[], alpha, beta;
    Thread kicker = null;
    Button Restart = new Button( "Restart" );
    Button Pause = new Button( "Pause" );
    Button Stop = new Button( "Stop" );
    Choice C = new Choice();


    public void init() {

        wx = 400;//size().width;
        wy = 400;//size().height;
        resize( wx, wy );
        setBackground( Color.white );
        nx = wx / 2;
        yoff = 50;
        wy -= yoff;
        xpts = new int[nx];
        ypts = new int[nx];
        psi = new McComplex[nx];
        EtoV = new McComplex[nx];
        energyScale = 1;
        initPhysics();
        Panel p = new Panel();
        p.setLayout( new FlowLayout() );
        p.add( Pause );
        p.add( Restart );
        p.add( Stop );
        C.addItem( "Barrier V = 2*E" );
        C.addItem( "Barrier V = E" );
        C.addItem( "Barrier V = E/2" );
        C.addItem( "No Barrier" );
        C.addItem( "Well V = -E/2" );
        C.addItem( "Well V = -E" );
        C.addItem( "Well V = -2*E" );
        C.select( "Barrier V = E" );
        p.add( C );
        getContentPane().add( "North", p );
        Restart.disable();
        C.disable();
    }

    public void initPhysics() {
        x0 = -2;
        xmin = -3;
        xmax = 3;
        dx = ( xmax - xmin ) / ( nx - 1 );
        xscale = ( wx - 0.5 ) / ( xmax - xmin );
        ymin = -1.5;
        ymax = 1.5;
        dy = ( ymax - ymin ) / ( wy - 1 );
        yscale = ( wy - 0.5 ) / ( ymax - ymin );
        tmin = 0;
        t = tmin;
        hbar = 1;
        mass = 100;
        width = 0.50;
        vwidth = 0.25;
        vx = 0.25;
        dt = 0.8 * mass * dx * dx / hbar;
        epsilon = hbar * dt / ( mass * dx * dx );
        alpha = new McComplex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) )
                               , -0.5 * Math.sin( epsilon / 2 ) );
        beta = new McComplex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 )
                              , 0.5 * Math.sin( epsilon / 2 ) );
        energy = 0.5 * mass * vx * vx;

        for( int x = 0; x < nx; x++ ) {
            double r, xval;
            xval = xmin + dx * x;
            xpts[x] = (int)( xscale * ( xval - xmin ) );
            r = Math.exp( -( ( xval - x0 ) / width ) * ( ( xval - x0 ) / width ) );
            psi[x] = new McComplex( r * Math.cos( mass * vx * xval / hbar ),
                                    r * Math.sin( mass * vx * xval / hbar ) );
            r = v( xval ) * dt / hbar;
            EtoV[x] = new McComplex( Math.cos( r ), -Math.sin( r ) );
        }
    }


    double v( double x ) {
        return
                ( Math.abs( x ) < vwidth ) ? ( energy * energyScale ) : 0;
    }


    public void paint( Graphics g ) {
        MakeGraph( g );
    }

    public void MakeGraph( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor( Color.white );
        g2.fillRect( 0, 0, getWidth(), getHeight() );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        int ix, iy;
        int jx, jy;

        //g.setColor(Color.black);
        //g.drawRect(0,yoff,wx-1,wy-1);

        g.setColor( Color.red );
        ix = (int)( xscale * 0.5 * ( xmax - xmin - 2 * vwidth ) );
        jx = (int)( xscale * 0.5 * ( xmax - xmin + 2 * vwidth ) );
        iy = (int)( wy - 1 - yscale * ( 0.5 * ymax * energyScale - ymin ) );
        jy = (int)( wy - 1 - yscale * ( 0 - ymin ) );

        g.drawLine( ix, yoff + iy, ix, yoff + jy );
        g.drawLine( jx, yoff + iy, jx, yoff + jy );
        g.drawLine( ix, yoff + iy, jx, yoff + iy );

        g.setColor( Color.blue );

        for( int x = 0; x < nx; x++ ) {
            ypts[x] = yoff + (int)( wy - 1 -
                                    yscale * ( psi[x].re - ymin ) );
        }
        for( int x = 0; x < nx - 1; x++ ) {
            g.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }

        g.setColor( Color.green );

        for( int x = 0; x < nx; x++ ) {
            ypts[x] = yoff + (int)( wy - 1 -
                                    yscale * ( psi[x].im - ymin ) );
        }
        for( int x = 0; x < nx - 1; x++ ) {
            g.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }

        g.setColor( Color.black );

        for( int x = 0; x < nx; x++ ) {
            ypts[x] = yoff + (int)( wy - 1 -
                                    yscale * ( psi[x].re * psi[x].re + psi[x].im * psi[x].im - ymin ) );
        }
        for( int x = 0; x < nx - 1; x++ ) {
            g.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }
    }

    public void run() {
        while( kicker != null ) {
//            step();
//            step();
            step();
            t += dt;
            repaint();
            try {
                Thread.sleep( 30 );
            }
            catch( InterruptedException e ) {
                break;
            }
        }
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
        if( kicker == null ) {
            kicker = new Thread( this );
            kicker.start();
        }
    }

    /**
     * Stop the applet. The thread will exit because kicker is set to null.
     */
    public void stop() {
        if( kicker != null ) {
            kicker.stop();
            kicker = null;
        }
    }

    public boolean mouseDown( java.awt.Event evt, int x, int y ) {
        /*
        requestFocus();
        if( null != kicker ) stop();
        else start();
        */
        return true;
    }

    public boolean action( Event evt, Object arg ) {
        if( evt.target instanceof Choice ) {
            if( "Barrier V = E".equals( arg ) ) {
                energyScale = 1;
            }
            else if( "Barrier V = E/2".equals( arg ) ) {
                energyScale = 0.5;
            }
            else if( "Barrier V = 2*E".equals( arg ) ) {
                energyScale = 2;
            }
            else if( "Well V = -2*E".equals( arg ) ) {
                energyScale = -2;
            }
            else if( "Well V = -E".equals( arg ) ) {
                energyScale = -1;
            }
            else if( "Well V = -E/2".equals( arg ) ) {
                energyScale = -0.5;
            }
            else if( "No Barrier".equals( arg ) ) {
                energyScale = 0;
            }
        }
        else {
            if( "Stop".equals( arg ) ) {
                stop();
                Stop.disable();
                Restart.enable();
                C.enable();
                Pause.disable();
            }
            else if( "Restart".equals( arg ) ) {
                Stop.enable();
                Restart.disable();
                C.disable();
                Pause.enable();
                Pause.setLabel( "Pause" );
                initPhysics();
                start();
            }
            else if( "Pause".equals( arg ) ) {
                stop();
                Pause.setLabel( "Resume" );
            }
            else if( "Resume".equals( arg ) ) {
                start();
                Pause.setLabel( "Pause" );
            }
        }
        return true;
    }

    public void step() {
        McComplex x = new McComplex( 0, 0 );
        McComplex y = new McComplex( 0, 0 );
        McComplex w = new McComplex( 0, 0 );
        McComplex z = new McComplex( 0, 0 );

        /*
         * The time stepping algorithm used here is described in:
         *
         * Richardson, John L.,
         * Visualizing quantum scattering on the CM-2 supercomputer,
         * Computer Physics Communications 63 (1991) pp 84-94
         */

        for( int i = 0; i < nx - 1; i += 2 ) {
            x.set( psi[i] );
            y.set( psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            psi[i + 1].add( w, z );
        }

        for( int i = 1; i < nx - 1; i += 2 ) {
            x.set( psi[i] );
            y.set( psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            psi[i + 1].add( w, z );
        }

        x.set( psi[nx - 1] );
        y.set( psi[0] );
        w.mult( alpha, x );
        z.mult( beta, y );
        psi[nx - 1].add( w, z );
        w.mult( alpha, y );
        z.mult( beta, x );
        psi[0].add( w, z );

        for( int i = 0; i < nx; i++ ) {
            x.set( psi[i] );
            psi[i].mult( x, EtoV[i] );
        }

        x.set( psi[nx - 1] );
        y.set( psi[0] );
        w.mult( alpha, x );
        z.mult( beta, y );
        psi[nx - 1].add( w, z );
        w.mult( alpha, y );
        z.mult( beta, x );
        psi[0].add( w, z );

        for( int i = 1; i < nx - 1; i += 2 ) {
            x.set( psi[i] );
            y.set( psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            psi[i + 1].add( w, z );
        }

        for( int i = 0; i < nx - 1; i += 2 ) {
            x.set( psi[i] );
            y.set( psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            psi[i + 1].add( w, z );
        }
    }

    public double Norm() {
        double sum = 0.0;
        for( int x = 0; x < nx; x++ ) {
            sum += psi[x].re * psi[x].re + psi[x].im * psi[x].im;
        }
        return sum;
    }
}

class McComplex {
    double re, im;

    McComplex( double x, double y ) {
        re = x;
        im = y;
    }

    public void add( McComplex a, McComplex b ) {
        re = a.re + b.re;
        im = a.im + b.im;
    }

    public void mult( McComplex a, McComplex b ) {
        re = a.re * b.re - a.im * b.im;
        im = a.re * b.im + a.im * b.re;
    }

    public void set( McComplex a ) {
        re = a.re;
        im = a.im;
    }

    public static void main( String[] args ) {
        Applet a = new WaveSim();

        a.setSize( 600, 600 );
        JFrame f = new JFrame();
        f.setContentPane( a );
        f.pack();
        f.show();
        a.init();
    }

}
