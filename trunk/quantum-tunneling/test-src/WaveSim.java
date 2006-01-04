
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    
    // Thread
    private static final long THREAD_SLEEP_TIME = 60; // milliseconds
    
    // Model (all values are in model coordinates)
    private static final double X0 = -2;  // initial position of the wave packet's center
    private static final double MIN_POSITION = -3; // min position
    private static final double MAX_POSITION = +3; // max position
    private static final double MIN_ENERGY = -1.5; // min energy
    private static final double MAX_ENERGY = +1.5; // max energy
    private static final double HBAR = 1; // Planck's constant
    private static final double MASS = 100; // mass
    private static final double WIDTH = 0.50; // sqrt(2) * (initial width of wave packet)
    private static final double VX = 0.25; // velocity = sqrt(2*E/mass)
    private static final double TOTAL_ENERGY = ( VX * VX * MASS / 2 ); // total energy
    
    // View
    private static final Dimension APP_SIZE = new Dimension( 600, 400 ); // pixels
    private static final int CONTROL_PANEL_HEIGHT = 50; // pixels
    private static final int STEPS_PER_FRAME = 3;
    private static final Color BARRIER_COLOR = Color.RED;
    private static final Color REAL_COLOR = Color.BLUE;
    private static final Color IMAGINARY_COLOR = Color.GREEN;
    private static final Color PROBABILITY_DENSITY_COLOR = Color.BLACK;
    private static final int PIXELS_PER_SAMPLE_POINT = 2;
    
    // Controls
    private static final String PLAY_LABEL = "Play >";
    private static final String PAUSE_LABEL = "Pause ||";
    private static final String RESTART_LABEL = "<< Restart";
    private static final String STEP_LABEL = "Step >";
    private static final String ANTIALIASING_LABEL = "antialiasing";
    private static final BarrierChoice CHOICE1 = new BarrierChoice( "Barrier V = 2*E", 2 );
    private static final BarrierChoice CHOICE2 = new BarrierChoice( "Barrier V = E", 1 );
    private static final BarrierChoice CHOICE3 = new BarrierChoice( "Barrier V = E/2", 0.5 );
    private static final BarrierChoice CHOICE4 = new BarrierChoice( "No Barrier", 0 );
    private static final BarrierChoice CHOICE5 = new BarrierChoice( "Well V = -E/2", -0.5 );
    private static final BarrierChoice CHOICE6 = new BarrierChoice( "Well V = -E", -1 );
    private static final BarrierChoice CHOICE7 = new BarrierChoice( "Well V = -2*E", -2 );
    
    //----------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------

    private Thread thread;
    
    // Model
    private int numberOfPoints; // number of sample points
    private double dt; // time step
    private double dx;  // change in position for each sample point
    private double energyScale; // ratio of V/E
    private Complex Psi[]; // wave function values at each sample point
    private Complex EtoV[]; // potential energy propogator = exp(-i*V(x)*dt/hbar)
    private Complex alpha; //special parameter for Richardson algorithm
    private Complex beta; //special parameter for Richardson algorithm
    private double epsilon; //special parameter for Richardson algorithm
    
    // View
    private int viewWidth, viewHeight; // view size, in pixels
    private int xpts[], ypts[];   // (x,y) pixel coordinates for each sample point
    private double xscale; // pixels per 1 unit of position
    private double yscale; // pixels per 1 unit of energy
    private boolean antialiasing; // should drawing be done using antialiasing?
    
    // Controls
    private JButton restartButton;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stepButton;
    private JComboBox barrierComboBox;
    private JCheckBox antialiasingCheckBox;

    //----------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------
    
    public void init() {      
        thread = null;
        createUI();
        restart();
        play();
    }
    
    //----------------------------------------------------------------------
    // Physics
    //----------------------------------------------------------------------
    
    private void initPhysics() {
        viewWidth = getSize().width;
        viewHeight = getSize().height - CONTROL_PANEL_HEIGHT;
        numberOfPoints = viewWidth / PIXELS_PER_SAMPLE_POINT;

        xpts = new int[numberOfPoints];
        ypts = new int[numberOfPoints];
        Psi = new Complex[numberOfPoints];
        EtoV = new Complex[numberOfPoints];
        
        dx = ( MAX_POSITION - MIN_POSITION ) / ( numberOfPoints - 1 );
        xscale = ( viewWidth - 0.5 ) / ( MAX_POSITION - MIN_POSITION );
        yscale = ( viewHeight - 0.5 ) / ( MAX_ENERGY - MIN_ENERGY );
        dt = 0.8 * MASS * dx * dx / HBAR;
        epsilon = HBAR * dt / ( MASS * dx * dx );
        alpha = new Complex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) ), -0.5 * Math.sin( epsilon / 2 ) );
        beta = new Complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );

        for ( int x = 0; x < numberOfPoints; x++ ) {
            double r, xval;
            xval = MIN_POSITION + dx * x;
            xpts[x] = (int) ( xscale * ( xval - MIN_POSITION ) );
            r = Math.exp( -( ( xval - X0 ) / WIDTH ) * ( ( xval - X0 ) / WIDTH ) );
            Psi[x] = new Complex( r * Math.cos( MASS * VX * xval / HBAR ), r * Math.sin( MASS * VX * xval / HBAR ) );
            r = v( xval ) * dt / HBAR;
            EtoV[x] = new Complex( Math.cos( r ), -Math.sin( r ) );
        }
    }

    private double v( double x ) {
        return ( Math.abs( x ) < WIDTH / 2 ) ? ( TOTAL_ENERGY * energyScale ) : 0;
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

        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        x.set( Psi[numberOfPoints - 1] );
        y.set( Psi[0] );
        w.mult( alpha, x );
        z.mult( beta, y );
        Psi[numberOfPoints - 1].add( w, z );
        w.mult( alpha, y );
        z.mult( beta, x );
        Psi[0].add( w, z );

        for ( int i = 0; i < numberOfPoints; i++ ) {
            x.set( Psi[i] );
            Psi[i].mult( x, EtoV[i] );
        }

        x.set( Psi[numberOfPoints - 1] );
        y.set( Psi[0] );
        w.mult( alpha, x );
        z.mult( beta, y );
        Psi[numberOfPoints - 1].add( w, z );
        w.mult( alpha, y );
        z.mult( beta, x );
        Psi[0].add( w, z );

        for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {
            x.set( Psi[i] );
            y.set( Psi[i + 1] );
            w.mult( alpha, x );
            z.mult( beta, y );
            Psi[i + 0].add( w, z );
            w.mult( alpha, y );
            z.mult( beta, x );
            Psi[i + 1].add( w, z );
        }

        for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {
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
    
    /*
     * Creates the user interface.
     */
    private void createUI() {
        
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
        
        antialiasingCheckBox = new JCheckBox( ANTIALIASING_LABEL );
        antialiasingCheckBox.setOpaque( false );
        selectAntialiasing();
        
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout( new FlowLayout() );
        buttonPanel.add( restartButton );
        buttonPanel.add( playButton );
        buttonPanel.add( pauseButton );
        buttonPanel.add( stepButton );
        buttonPanel.add( barrierComboBox );
        buttonPanel.add( antialiasingCheckBox );
        add( "North", buttonPanel );
        
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
        antialiasingCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                selectAntialiasing();
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
    
    /*
     * Converts the model data to view coordinates and
     * draws it as a set of connected line segments.
     */
    private void drawPlots( Graphics g ) {

        if ( antialiasing && g instanceof Graphics2D ) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        }

        // Barrier
        g.setColor( BARRIER_COLOR );
        int ix = (int) ( xscale * 0.5 * ( MAX_POSITION - MIN_POSITION - WIDTH ) );
        int jx = (int) ( xscale * 0.5 * ( MAX_POSITION - MIN_POSITION + WIDTH ) );
        int iy = (int) ( viewHeight - 1 - yscale * ( 0.5 * MAX_ENERGY * energyScale - MIN_ENERGY ) ) + CONTROL_PANEL_HEIGHT;
        int jy = (int) ( viewHeight - 1 - yscale * ( 0 - MIN_ENERGY ) ) + CONTROL_PANEL_HEIGHT;
        g.drawLine( ix, iy, ix, jy );
        g.drawLine( jx, iy, jx, jy );
        g.drawLine( ix, iy, jx, iy );

        // Real part
        g.setColor( REAL_COLOR );
        for ( int x = 0; x < numberOfPoints; x++ ) {
            ypts[x] = CONTROL_PANEL_HEIGHT + (int) ( viewHeight - 1 - yscale * ( Psi[x].re - MIN_ENERGY ) );
            if ( x > 0 ) {
                g.drawLine( xpts[x - 1], ypts[x - 1], xpts[x], ypts[x] );
            }
        }

        // Imaginary part
        g.setColor( IMAGINARY_COLOR );
        for ( int x = 0; x < numberOfPoints; x++ ) {
            ypts[x] = CONTROL_PANEL_HEIGHT + (int) ( viewHeight - 1 - yscale * ( Psi[x].im - MIN_ENERGY ) );
            if ( x > 0 ) {
                g.drawLine( xpts[x - 1], ypts[x - 1], xpts[x], ypts[x] );
            }
        }

        // Probability Density (abs^2)
        g.setColor( PROBABILITY_DENSITY_COLOR );
        for ( int x = 0; x < numberOfPoints; x++ ) {
            ypts[x] = CONTROL_PANEL_HEIGHT + (int) ( viewHeight - 1 - yscale * ( Math.pow( Psi[x].abs(), 2 ) - MIN_ENERGY ) );
            if ( x > 0 ) {
                g.drawLine( xpts[x - 1], ypts[x - 1], xpts[x], ypts[x] );
            }
        }
    }
    
    //----------------------------------------------------------------------
    // Applet overrides
    //----------------------------------------------------------------------
    
    public void paint( Graphics g ) {
        super.paint( g );
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
        repaint();
    }
    
    private void selectBarrier() {
        BarrierChoice choice = (BarrierChoice) barrierComboBox.getSelectedItem();
        energyScale = choice.getEnergyScale();
        restart();
    }
    
    private void selectAntialiasing() {
        antialiasing = antialiasingCheckBox.isSelected();
        repaint();
    }
    
    //----------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------

    public void run() {
        while ( thread != null ) {
            step();
            try {
                Thread.sleep( THREAD_SLEEP_TIME );
            }
            catch ( InterruptedException e ) {
                break;
            }
        }
    }
    
    //----------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------
    
    /*
     * Choices that appear in the barrier/well combo box.
     */
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

    /*
     * Very basic support for complex numbers.
     */
    private static class Complex {

        private double re, im;

        Complex( double x, double y ) {
            re = x;
            im = y;
        }

        public void set( Complex a ) {
            re = a.re;
            im = a.im;
        }
        
        public void add( Complex a, Complex b ) {
            re = a.re + b.re;
            im = a.im + b.im;
        }

        public void mult( Complex a, Complex b ) {
            re = a.re * b.re - a.im * b.im;
            im = a.re * b.im + a.im * b.re;
        }

        public double abs() {
            return Math.sqrt( re * re + im * im );
        }
    }
}
