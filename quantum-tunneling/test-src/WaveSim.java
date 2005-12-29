
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 * Integrating the Schrodinger Wave Equation.
 * <br>
 * http://www.neti.no/java/sgi_java/WaveSim.html
 * 
 * @author John L. Richardson (jlr@sgi.com)
 * @version December 1995
 */
public class WaveSim extends java.applet.Applet implements Runnable {

    //----------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------
    
    // User interface
    private static final Dimension APP_SIZE = new Dimension( 500, 400 );
    private static final String PLAY_LABEL = "Play >";
    private static final String PAUSE_LABEL = "Pause ||";
    private static final String RESTART_LABEL = "<< Restart";
    private static final String STEP_LABEL = "Step >";
    private static final BarrierChoice CHOICE1 = new BarrierChoice( "Barrier V = 2*E", 2 );
    private static final BarrierChoice CHOICE2 = new BarrierChoice( "Barrier V = E", 1 );
    private static final BarrierChoice CHOICE3 = new BarrierChoice( "Barrier V = E/2", 0.5 );
    private static final BarrierChoice CHOICE4 = new BarrierChoice( "No Barrier", 0 );
    private static final BarrierChoice CHOICE5 = new BarrierChoice( "Well V = -E/2", -0.5 );
    private static final BarrierChoice CHOICE6 = new BarrierChoice( "Well V = -E", -1 );
    private static final BarrierChoice CHOICE7 = new BarrierChoice( "Well V = -2*E", -2 );
    
    // Graphics
    private static final boolean ANTI_ALIAS = true;
    private static final int STEPS_PER_FRAME = 3;
    private static final int Y_OFFSET = 50;
    private static final Color BARRIER_COLOR = Color.RED;
    private static final Color REAL_COLOR = Color.BLUE;
    private static final Color IMAGINARY_COLOR = Color.GREEN;
    private static final Color MAGNITUDE_COLOR = Color.BLACK;
    
    // Physics
    private static final double X0 = -2;  // initial position of the wave packet's center
    private static final double X_MIN = -3; // min position
    private static final double X_MAX = +3; // max position
    private static final double Y_MIN = -1.5; // min energy
    private static final double Y_MAX = +1.5; // max energy
    private static final double T_MIN = 0; // min time
    private static final double HBAR = 1; // Planck's constant
    private static final double MASS = 100; // mass
    private static final double WIDTH = 0.50; // sqrt(2) * (initial width of wave packet)
    private static final double VWIDTH = WIDTH / 2; // half the width
    private static final double VX = 0.25; // velocity = sqrt(2*E/mass)
    
    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------
    
    private int wx, wy, nx, xpts[], ypts[];
    private double t, dt, dx, dy, xscale, yscale;
    private double epsilon, energy, energyScale;
    private Complex Psi[], EtoV[], alpha, beta;
    
    private JButton restartButton;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stepButton;
    private JComboBox barrierComboBox;

    private Thread thread;
    
    //----------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------
    
    public void init() {      
        thread = null;
        initUI();
        restart();
        play();
    }
    
    //----------------------------------------------------------------------
    // Physics
    //----------------------------------------------------------------------
    
    private void initPhysics() {
        wx = getSize().width;
        wy = getSize().height - Y_OFFSET;
        nx = wx / 2;

        xpts = new int[nx];
        ypts = new int[nx];
        Psi = new Complex[nx];
        EtoV = new Complex[nx];
        
        dx = ( X_MAX - X_MIN ) / ( nx - 1 );
        xscale = ( wx - 0.5 ) / ( X_MAX - X_MIN );
        dy = ( Y_MAX - Y_MIN ) / ( wy - 1 );
        yscale = ( wy - 0.5 ) / ( Y_MAX - Y_MIN );
        t = T_MIN;
        dt = 0.8 * MASS * dx * dx / HBAR;
        epsilon = HBAR * dt / ( MASS * dx * dx );
        alpha = new Complex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) ), -0.5 * Math.sin( epsilon / 2 ) );
        beta = new Complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );
        energy = 0.5 * MASS * VX * VX;

        for ( int x = 0; x < nx; x++ ) {
            double r, xval;
            xval = X_MIN + dx * x;
            xpts[x] = (int) ( xscale * ( xval - X_MIN ) );
            r = Math.exp( -( ( xval - X0 ) / WIDTH ) * ( ( xval - X0 ) / WIDTH ) );
            Psi[x] = new Complex( r * Math.cos( MASS * VX * xval / HBAR ), r * Math.sin( MASS * VX * xval / HBAR ) );
            r = v( xval ) * dt / HBAR;
            EtoV[x] = new Complex( Math.cos( r ), -Math.sin( r ) );
        }
    }

    private double v( double x ) {
        return ( Math.abs( x ) < VWIDTH ) ? ( energy * energyScale ) : 0;
    }

    private void stepPhysics() {
        Complex x = new Complex( 0, 0 );
        Complex y = new Complex( 0, 0 );
        Complex w = new Complex( 0, 0 );
        Complex z = new Complex( 0, 0 );

        /*
         * The time stepping algorithm used here is described in:
         *
         * Richardson, John L.,
         * Visualizing quantum scattering on the CM-2 supercomputer,
         * Computer Physics Communications 63 (1991) pp 84-94 
         */

        for ( int i = 0; i < nx - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        for ( int i = 1; i < nx - 1; i += 2 ) {
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

        for ( int i = 0; i < nx; i++ ) {
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

        for ( int i = 1; i < nx - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        for ( int i = 0; i < nx - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }
    }
    
    //----------------------------------------------------------------------
    // User Interface
    //----------------------------------------------------------------------
    
    private void initUI() {
        
        restartButton = new JButton( RESTART_LABEL );
        restartButton.setOpaque( false );
        
        playButton = new JButton( PLAY_LABEL );
        playButton.setOpaque( false );
        
        pauseButton = new JButton( PAUSE_LABEL );
        pauseButton.setOpaque( false );
        
        stepButton = new JButton( STEP_LABEL );
        stepButton.setOpaque( false );
        
        barrierComboBox = new JComboBox();
        barrierComboBox.setOpaque( false );
        barrierComboBox.addItem( CHOICE1 );
        barrierComboBox.addItem( CHOICE2 );
        barrierComboBox.addItem( CHOICE3 );
        barrierComboBox.addItem( CHOICE4 );
        barrierComboBox.addItem( CHOICE5 );
        barrierComboBox.addItem( CHOICE6 );
        barrierComboBox.addItem( CHOICE7 );
        barrierComboBox.setSelectedItem( CHOICE2 );
        selectBarrier();
        
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout( new FlowLayout() );
        buttonPanel.add( restartButton );
        buttonPanel.add( playButton );
        buttonPanel.add( pauseButton );
        buttonPanel.add( stepButton );
        buttonPanel.add( barrierComboBox );
        add( "North", buttonPanel );
        
        playButton.setEnabled( false );
        pauseButton.setEnabled( true );
        stepButton.setEnabled( false );
        
        restartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                restart();
            }
        } );
        playButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                play();
            }
        } );
        pauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                pause();
            }
        } );
        stepButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                step();
            }
        } );
        barrierComboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                selectBarrier();
            }
        } );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent event ) {
                restart();
            }
        });
        
        setBackground( Color.WHITE );
        setSize( APP_SIZE ); // do this last
    }

    //----------------------------------------------------------------------
    // Graphics
    //----------------------------------------------------------------------
    
    private void drawPlots( Graphics g ) {
        
        Graphics2D g2 = (Graphics2D)g;
        
        int ix, iy;
        int jx, jy;

        if ( ANTI_ALIAS ) {
            g2.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        }
        
        g2.setColor( BARRIER_COLOR );
        ix = (int) ( xscale * 0.5 * ( X_MAX - X_MIN - 2 * VWIDTH ) );
        jx = (int) ( xscale * 0.5 * ( X_MAX - X_MIN + 2 * VWIDTH ) );
        iy = (int) ( wy - 1 - yscale * ( 0.5 * Y_MAX * energyScale - Y_MIN ) );
        jy = (int) ( wy - 1 - yscale * ( 0 - Y_MIN ) );
        g2.drawLine( ix, Y_OFFSET + iy, ix, Y_OFFSET + jy );
        g2.drawLine( jx, Y_OFFSET + iy, jx, Y_OFFSET + jy );
        g2.drawLine( ix, Y_OFFSET + iy, jx, Y_OFFSET + iy );

        g2.setColor( REAL_COLOR );
        for ( int x = 0; x < nx; x++ ) {
            ypts[x] = Y_OFFSET + (int) ( wy - 1 - yscale * ( Psi[x].re - Y_MIN ) );
        }
        for ( int x = 0; x < nx - 1; x++ ) {
            g2.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }

        g2.setColor( IMAGINARY_COLOR );
        for ( int x = 0; x < nx; x++ ) {
            ypts[x] = Y_OFFSET + (int) ( wy - 1 - yscale * ( Psi[x].im - Y_MIN ) );
        }
        for ( int x = 0; x < nx - 1; x++ ) {
            g2.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }

        g2.setColor( MAGNITUDE_COLOR );
        for ( int x = 0; x < nx; x++ ) {
            ypts[x] = Y_OFFSET + (int) ( wy - 1 - yscale * ( Psi[x].re * Psi[x].re + Psi[x].im * Psi[x].im - Y_MIN ) );
        }
        for ( int x = 0; x < nx - 1; x++ ) {
            g2.drawLine( xpts[x], ypts[x], xpts[x + 1], ypts[x + 1] );
        }
    }
    
    //----------------------------------------------------------------------
    // Applet overrides
    //----------------------------------------------------------------------
    
    public void paint( Graphics g ) {
        drawPlots( g );
    }
    
    //----------------------------------------------------------------------
    // Event handling methods
    //----------------------------------------------------------------------
    
    private void restart() {
        initPhysics();
        repaint();  
    }
    
    private void play() {
        start();
        playButton.setEnabled( false );
        pauseButton.setEnabled( true );
        stepButton.setEnabled( false );
        thread = new Thread( this );
        thread.start();
    }
    
    private void pause() {
        playButton.setEnabled( true );
        pauseButton.setEnabled( false );
        stepButton.setEnabled( true );
        thread = null;
    }
   
    private synchronized void step() {
        for ( int i = 0; i < STEPS_PER_FRAME; i++ ) {
            stepPhysics();
        }
        t += dt;
        repaint();
    }
    
    private void selectBarrier() {
        BarrierChoice choice = (BarrierChoice) barrierComboBox.getSelectedItem();
        energyScale = choice.getEnergyScale();
        restart();
    }
    
    //----------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------

    public void run() {
        while ( thread != null ) {
            step();
            try {
                Thread.sleep( 60 );
            }
            catch ( InterruptedException e ) {
                break;
            }
        }
    }
    
    //----------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------
    
    private static class BarrierChoice {

        private String label;
        private double energyScale;

        public BarrierChoice( String label, double energyScale ) {
            this.label = label;
            this.energyScale = energyScale;
        }

        public double getEnergyScale() {
            return energyScale;
        }

        public String toString() {
            return label;
        }
    }

    private static class Complex {

        private double re, im;

        Complex( double x, double y ) {
            re = x;
            im = y;
        }

        public void add( Complex a, Complex b ) {
            re = a.re + b.re;
            im = a.im + b.im;
        }

        public void mult( Complex a, Complex b ) {
            re = a.re * b.re - a.im * b.im;
            im = a.re * b.im + a.im * b.re;
        }

        public void set( Complex a ) {
            re = a.re;
            im = a.im;
        }
    }
}
