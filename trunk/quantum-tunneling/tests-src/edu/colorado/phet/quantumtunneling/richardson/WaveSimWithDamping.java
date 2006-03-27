/*
 * Version of Richardson's "WaveSim" with damping, 
 * to prevent periodic boundary conditions.
 * Ie, we don't want to wave to exit via one side of
 * the display and immediately return on the other side.
 */

package edu.colorado.phet.quantumtunneling.richardson;
import java.awt.*;

public class WaveSimWithDamping extends java.applet.Applet implements Runnable {
    int wx, wy, yoff, xpts[], ypts[], nx;
    double x0, xmin, xmax, dx, ymin, ymax, dy, xscale, yscale, tmin, t, dt;
    double hbar, mass, epsilon, width, vx, vwidth, energy, energyScale;
    complex Psi[], EtoV[], alpha, beta;
    Thread kicker = null;
    Button Restart = new Button( "Restart" );
    Button Pause = new Button( "Pause" );
    Button Stop = new Button( "Stop" );
    Choice C = new Choice();


    public void init() {

        setSize( 500, 300 );
        wx = size().width; //number of grid points in x direction
        wy = size().height; //number of grid points in y direction (needed for graphics, not computation
        resize( wx, wy );
        setBackground( Color.white );
        nx = wx / 2; //half number of grid points in x direction.  Why?
        yoff = 50; //only used for graphics, not computation
        wy -= yoff; // only used for graphics, not computation.
        xpts = new int[nx]; //array of values of x at each point, only for graphics
        ypts = new int[nx]; //array of values of y at each point, only for graphics
        Psi = new complex[nx]; //value of psi at each point
        EtoV = new complex[nx]; //Potential energy propagator = exp(-i*V(x)*dt/hbar)
        energyScale = 1; //Ratio of V/E.  Later this is set to other values depending on V.
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
        add( "North", p );
        Restart.disable();
        C.disable();
    }

    public void initPhysics() {
        x0 = -2;  //"initial center" of wave packet
        xmin = -3;  //position of left edge of box
        xmax = 3; //position of right edge of box
        dx = ( xmax - xmin ) / ( nx - 1 ); //spacing between grid points.
        xscale = ( wx - 0.5 ) / ( xmax - xmin ); //number of grid points divided by size of box.
        ymin = -1.5; //min energy on graph
        ymax = 1.5; //max energy on graph
        dy = ( ymax - ymin ) / ( wy - 1 ); //never used
        yscale = ( wy - 0.5 ) / ( ymax - ymin ); //used only for graphics
        tmin = 0; //starting time?
        t = tmin; //time
        hbar = 1; //set hbar to 0.658
        mass = 100; //set mass to 5.7
        width = 0.50; //width = sqrt(2)*"initial width" of wave packet
        vwidth = 0.25; //half width of barrier (if vwidth=0.25, then width=0.5)
        vx = 0.25; // velocity = sqrt(2E/m) (we would set E, not vx)
        dt = 0.8 * mass * dx * dx / hbar; //size of time step; can set this to anything convenient.
        epsilon = hbar * dt / ( mass * dx * dx ); //special parameter for Richardson algorithm
        alpha = new complex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) )
                , -0.5 * Math.sin( epsilon / 2 ) );//special parameter for Richardson algorithm
        beta = new complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 )
                , 0.5 * Math.sin( epsilon / 2 ) );//special parameter for Richardson algorithm
        energy = 0.5 * mass * vx * vx; //total energy, we would set this directly

        for( int x = 0; x < nx; x++ ) { // set up initial value of psi, xpts, and EtoV.
            //note that r is a dummy variable that is used for two completely different things.
            double r, xval;
            xval = xmin + dx * x;  //value of x at each grid point
            xpts[x] = (int)( xscale * ( xval - xmin ) );
            r = Math.exp( -( ( xval - x0 ) / width ) * ( ( xval - x0 ) / width ) );
            Psi[x] = new complex( r * Math.cos( mass * vx * xval / hbar ),
                                  r * Math.sin( mass * vx * xval / hbar ) );
            //Above we set up initial gaussian wave packet.
            //Note that mass*vx*xval/hbar can be replaced by k*xval,
            //where k = sqrt(2m*(E-V)/hbar^2)
            //Also, the entire psi above should be multiplied by a normalization constant A:
            //A = pi^(1/4)*sigma^(1/2) where sigma = width/sqrt(2)
            r = v( xval ) * dt / hbar;
            EtoV[x] = new complex( Math.cos( r ), -Math.sin( r ) ); // = exp(-i*V(x)*dt/hbar)
        }
    }

    //function to return potential energy as function of x.  If within barrier,
    //set to energy*energyscale, otherwise set equal to zero.
    //Remember that energy = total energy, energyscale = total energy/potential energy
    double v( double x ) {
        return
                ( Math.abs( x ) < vwidth ) ? ( energy * energyScale ) : 0;
    }


    public void paint( Graphics g ) {
        MakeGraph( g );
    }

    public void MakeGraph( Graphics g ) {
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
                                    yscale * ( Psi[x].re - ymin ) );
        }
        for( int x = 0; x < nx - 1; x++ ) {
            g.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }

        g.setColor( Color.green );

        for( int x = 0; x < nx; x++ ) {
            ypts[x] = yoff + (int)( wy - 1 -
                                    yscale * ( Psi[x].im - ymin ) );
        }
        for( int x = 0; x < nx - 1; x++ ) {
            g.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }

        g.setColor( Color.black );

        for( int x = 0; x < nx; x++ ) {
            ypts[x] = yoff + (int)( wy - 1 -
                                    yscale * ( Psi[x].re * Psi[x].re + Psi[x].im * Psi[x].im - ymin ) );
        }
        for( int x = 0; x < nx - 1; x++ ) {
            g.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }
    }

    public void run() {
        while( kicker != null ) {
            step();
            step();
            step();
            t += dt;
            repaint();
            try {
                Thread.sleep( 60 );
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
        complex x = new complex( 0, 0 );
        complex y = new complex( 0, 0 );
        complex w = new complex( 0, 0 );
        complex z = new complex( 0, 0 );

        /*
        * The time stepping algorithm used here is described in:
        *
        * Richardson, John L.,
        * Visualizing quantum scattering on the CM-2 supercomputer,
        * Computer Physics Communications 63 (1991) pp 84-94
        */

        for( int i = 0; i < nx - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        for( int i = 1; i < nx - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        x.set( Psi[nx - 1] );
        y.set( Psi[0] );
        w.mult( alpha, x );
        z.mult( beta, y );
        Psi[nx - 1].add( w, z );
        w.mult( alpha, y );
        z.mult( beta, x );
        Psi[0].add( w, z );

        for( int i = 0; i < nx; i++ ) {
            x.set( Psi[i] );
            Psi[i].mult( x, EtoV[i] );
        }

        x.set( Psi[nx - 1] );
        y.set( Psi[0] );
        w.mult( alpha, x );
        z.mult( beta, y );
        Psi[nx - 1].add( w, z );
        w.mult( alpha, y );
        z.mult( beta, x );
        Psi[0].add( w, z );

        for( int i = 1; i < nx - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        for( int i = 0; i < nx - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }
        dampLeft();
        dampRight();
    }

    private void dampRight() {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int i = Psi.length - damp.length + depth;
            Psi[i].scale( scale );
        }
    }

    /**
     * Should go to zero at fraction =1
     */
    private double getScaleFactor( int depth ) {
        return damp[depth];
    }

    private void dampLeft() {
        for( int depth = 0; depth < damp.length; depth++ ) {
            double scale = getScaleFactor( depth );
            int i = damp.length - depth - 1;
            Psi[i].scale( scale );
        }
    }

    private double[] damp = new double[] {0.999, 0.995, 0.99, 0.975, 0.95, 0.925, 0.9, 0.85, 0.7, 0.3};

    public double Norm() {
        double sum = 0.0;
        for( int x = 0; x < nx; x++ ) {
            sum += Psi[x].re * Psi[x].re + Psi[x].im * Psi[x].im;
        }
        return sum;
    }
    

    class complex {
        double re, im;

        complex( double x, double y ) {
            re = x;
            im = y;
        }

        public void add( complex a, complex b ) {
            re = a.re + b.re;
            im = a.im + b.im;
        }

        public void mult( complex a, complex b ) {
            re = a.re * b.re - a.im * b.im;
            im = a.re * b.im + a.im * b.re;
        }

        public void set( complex a ) {
            re = a.re;
            im = a.im;
        }

        public void scale( double scale ) {
            re *= scale;
            im *= scale;
        }
    }
}

