package edu.colorado.phet.quantumtunneling.richardson;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;

/**
 * My version of Richardson's "WaveSim" applet, 
 * with model and view fully separated.
 * (There's also a lot of code cleanup.)
 * <br>
 * Richardson's original program lives at:
 * http://www.neti.no/java/sgi_java/WaveSim.html
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MyWaveSim extends java.applet.Applet implements Runnable {

    //----------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------

    // Thread
    private static final long THREAD_SLEEP_TIME = 60; // milliseconds

    // Model (all values are in model coordinates)
    private static final double X0 = -2; // initial position of the wave packet's center
    private static final double MIN_POSITION = -3; // min position
    private static final double MAX_POSITION = +3; // max position
    private static final double MIN_ENERGY = -1.5; // min energy
    private static final double MAX_ENERGY = +1.5; // max energy
    private static final double HBAR = 1; // Planck's constant
    private static final double MASS = 100; // mass
    private static final double PACKET_WIDTH = 0.50; // wave packet width
    private static final double BARRIER_WIDTH = 0.50; // barrier width
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

    private Thread _thread;

    // Model
    private RichardsonModel _model;
    
    // View
    private int _xpts[], _ypts[]; // (x,y) pixel coordinates for each sample point
    private double _xscale; // pixels per 1 unit of position
    private double _yscale; // pixels per 1 unit of energy
    private int _viewHeight; // view height, in pixels
    private boolean _antialiasing; // should drawing be done using antialiasing?

    // Controls
    private JButton _restartButton;
    private JButton _playButton;
    private JButton _pauseButton;
    private JButton _stepButton;
    private JComboBox _barrierComboBox;
    private JCheckBox _antialiasingCheckBox;

    //----------------------------------------------------------------------
    // Physics
    //----------------------------------------------------------------------

    private static class RichardsonModel {
        
        // Model
        private double _dt; // time step
        private double _dx; // change in position for each sample point
        private double _energyScale; // ratio of V/E
        private double _positions[]; // position values at each sample point
        private MutableComplex _Psi[]; // wave function values at each sample point
        private Complex _EtoV[]; // potential energy propogator = exp(-i*V(x)*dt/hbar)
        private Complex _alpha; //special parameter for Richardson algorithm
        private Complex _beta; //special parameter for Richardson algorithm
        
        // Damping, to prevent periodic boundary conditions.
        private double[] _dampingFactors = new double[] { 0.3, 0.7, 0.85, 0.9, 0.925, 0.95, 0.975, 0.99, 0.995, 0.999 };
        
        // Reusable temporary variables
        private MutableComplex _c1 = new MutableComplex( 0, 0 );
        private MutableComplex _c2 = new MutableComplex( 0, 0 );
        private MutableComplex _c3 = new MutableComplex( 0, 0 );
        private MutableComplex _c4 = new MutableComplex( 0, 0 );
        
        public RichardsonModel() {}
        
        public double getNumberOfSamples() {
            return _positions.length;
        }
        
        public void setEnergyScale( double energyScale ) {
            _energyScale = energyScale;
        }
        
        public double getEnergyScale() {
            return _energyScale;
        }
        
        public double getPosition( int index ) {
            return _positions[index];
        }
        
        public Complex getPsi( int index ) {
            return _Psi[ index ];
        }

        /**
         * Resets (reinitializes) the model.
         * 
         * @param numberOfPoints
         */
        public void reset( int numberOfPoints ) {

            _positions = new double[numberOfPoints];
            _Psi = new MutableComplex[numberOfPoints];
            _EtoV = new Complex[numberOfPoints];

            _dx = ( MAX_POSITION - MIN_POSITION ) / ( numberOfPoints - 1 );
            _dt = 0.8 * MASS * _dx * _dx / HBAR;

            final double epsilon = HBAR * _dt / ( MASS * _dx * _dx );
            _alpha = new Complex( 0.5 * ( 1.0 + Math.cos( epsilon / 2 ) ), -0.5 * Math.sin( epsilon / 2 ) );
            _beta = new Complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );

            for ( int i = 0; i < numberOfPoints; i++ ) {
                final double position = MIN_POSITION + ( i * _dx );
                _positions[i] = position;
                final double r1 = Math.exp( -( ( position - X0 ) / PACKET_WIDTH ) * ( ( position - X0 ) / PACKET_WIDTH ) );
                _Psi[i] = new MutableComplex( r1 * Math.cos( MASS * VX * position / HBAR ), r1 * Math.sin( MASS * VX * position / HBAR ) );
                final double r2 = getPotentialEnergy( position ) * _dt / HBAR;
                _EtoV[i] = new Complex( Math.cos( r2 ), -Math.sin( r2 ) );
            }
        }

        /*
         * Determines the potential energy at a position.
         * This assumes that the barrier is centered at 0, and that 
         * potential energy is 0 everywhere is except within the barrier.
         */
        private double getPotentialEnergy( double x ) {
            return ( Math.abs( x ) < BARRIER_WIDTH / 2 ) ? ( TOTAL_ENERGY * _energyScale ) : 0;
        }

        /**
         * Time stepping algorithm, as described in:
         *
         * Richardson, John L.,
         * Visualizing quantum scattering on the CM-2 supercomputer,
         * Computer Physics Communications 63 (1991) pp 84-94 
         */
        public void propogate() {
            propogate1();
            propogate2();
            propogate3();
            propogate4();
            propogate3();
            propogate2();
            propogate1();
            damp();
        }

        private void propogate1() {
            int numberOfPoints = _Psi.length;
            for ( int i = 0; i < numberOfPoints - 1; i += 2 ) {

                // A = Psi[i]
                _c1.setValue( _Psi[i] );

                // B = Psi[i + 1]
                _c2.setValue( _Psi[i + 1] );

                // Psi[i] = (alpha * A) + (beta * B)
                _c3.setValue( _alpha );
                _c3.multiply( _c1 );
                _c4.setValue( _beta );
                _c4.multiply( _c2 );
                _Psi[i].setValue( _c3 );
                _Psi[i].add( _c4 );

                // Psi[i+1] = (alpha * B) + (beta * A)
                _c3.setValue( _alpha );
                _c3.multiply( _c2 );
                _c4.setValue( _beta );
                _c4.multiply( _c1 );
                _Psi[i + 1].setValue( _c3 );
                _Psi[i + 1].add( _c4 );
            }
        }

        private void propogate2() {
            int numberOfPoints = _Psi.length;
            for ( int i = 1; i < numberOfPoints - 1; i += 2 ) {

                // A = Psi[i]
                _c1.setValue( _Psi[i] );

                // B = Psi[i + 1]
                _c2.setValue( _Psi[i + 1] );

                // Psi[i] = (alpha * A) + (beta * B)
                _c3.setValue( _alpha );
                _c3.multiply( _c1 );
                _c4.setValue( _beta );
                _c4.multiply( _c2 );
                _Psi[i].setValue( _c3 );
                _Psi[i].add( _c4 );

                // Psi[i+1] = (alpha * B) + (beta * A)
                _c3.setValue( _alpha );
                _c3.multiply( _c2 );
                _c4.setValue( _beta );
                _c4.multiply( _c1 );
                _Psi[i + 1].setValue( _c3 );
                _Psi[i + 1].add( _c4 );
            }
        }

        private void propogate3() {
            int numberOfPoints = _Psi.length;

            // A = Psi[numberOfPoints - 1]
            _c1.setValue( _Psi[numberOfPoints - 1] );

            // B = Psi[0]
            _c2.setValue( _Psi[0] );

            // Psi[numberOfPoints - 1] = (alpha * A) + (beta * B)
            _c3.setValue( _alpha );
            _c3.multiply( _c1 );
            _c4.setValue( _beta );
            _c4.multiply( _c2 );
            _Psi[numberOfPoints - 1].setValue( _c3 );
            _Psi[numberOfPoints - 1].add( _c4 );

            // Psi[0] = (alpha * B) + (beta * A)
            _c3.setValue( _alpha );
            _c3.multiply( _c2 );
            _c4.setValue( _beta );
            _c4.multiply( _c1 );
            _Psi[0].setValue( _c3 );
            _Psi[0].add( _c4 );
        }

        private void propogate4() {
            int numberOfPoints = _Psi.length;
            for ( int i = 0; i < numberOfPoints; i++ ) {
                // Psi[i= = Psi[i] * EtoV[i]
                _Psi[i].multiply( _EtoV[i] );
            }
        }
        
        private void damp() {
            for ( int i = 0; i < _dampingFactors.length; i++ ) {
                double scale = _dampingFactors[i];
                _Psi[i].scale( scale );  // left edge
                _Psi[ _Psi.length - i - 1 ].scale( scale ); // right edge
            }
        }
    }

    //----------------------------------------------------------------------
    // Applet overrides
    //----------------------------------------------------------------------

    /**
     * Called by the browser or applet viewer to inform 
     * this applet that it has been loaded into the system.
     */
    public void init() {
        _thread = null;
        _model = new RichardsonModel();
        createUI();
        restart();
        play();
    }

    public void paint( Graphics g ) {
        super.paint( g );
        drawPlots( g );
    }

    //----------------------------------------------------------------------
    // User Interface
    //----------------------------------------------------------------------

    /*
     * Creates the user interface.
     */
    private void createUI() {

        _restartButton = new JButton( RESTART_LABEL );
        _restartButton.setOpaque( false );

        _playButton = new JButton( PLAY_LABEL );
        _playButton.setOpaque( false );

        _pauseButton = new JButton( PAUSE_LABEL );
        _pauseButton.setOpaque( false );

        _stepButton = new JButton( STEP_LABEL );
        _stepButton.setOpaque( false );

        _barrierComboBox = new JComboBox();
        _barrierComboBox.setOpaque( false );
        _barrierComboBox.addItem( CHOICE1 );
        _barrierComboBox.addItem( CHOICE2 );
        _barrierComboBox.addItem( CHOICE3 );
        _barrierComboBox.addItem( CHOICE4 );
        _barrierComboBox.addItem( CHOICE5 );
        _barrierComboBox.addItem( CHOICE6 );
        _barrierComboBox.addItem( CHOICE7 );
        _barrierComboBox.setSelectedItem( CHOICE2 );
        selectBarrier();

        _antialiasingCheckBox = new JCheckBox( ANTIALIASING_LABEL );
        _antialiasingCheckBox.setOpaque( false );
        selectAntialiasing();

        Panel buttonPanel = new Panel();
        buttonPanel.setLayout( new FlowLayout() );
        buttonPanel.add( _restartButton );
        buttonPanel.add( _playButton );
        buttonPanel.add( _pauseButton );
        buttonPanel.add( _stepButton );
        buttonPanel.add( _barrierComboBox );
        buttonPanel.add( _antialiasingCheckBox );
        add( "North", buttonPanel );

        _restartButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                restart();
            }
        } );
        _playButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                play();
            }
        } );
        _pauseButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                pause();
            }
        } );
        _stepButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                step();
            }
        } );
        _barrierComboBox.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                selectBarrier();
            }
        } );
        _antialiasingCheckBox.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                selectAntialiasing();
            }
        } );
        addComponentListener( new ComponentAdapter() {

            public void componentResized( ComponentEvent event ) {
                restart();
            }
        } );

        setBackground( Color.WHITE );
        setSize( APP_SIZE ); // do this last
    }

    /*
     * Resets values used in the view.
     */
    private void reset() {

        _viewHeight = getSize().height - CONTROL_PANEL_HEIGHT;
        final int viewWidth = getSize().width;
        final int numberOfPoints = viewWidth / PIXELS_PER_SAMPLE_POINT; // number of sample points

        _model.reset( numberOfPoints );
        
        _xpts = new int[numberOfPoints];
        _ypts = new int[numberOfPoints];

        _xscale = ( viewWidth - 0.5 ) / ( MAX_POSITION - MIN_POSITION ); // pixels per 1 unit of position
        _yscale = ( _viewHeight - 0.5 ) / ( MAX_ENERGY - MIN_ENERGY ); // pixels per 1 unit of energy

        // X coordinates
        for ( int i = 0; i < numberOfPoints; i++ ) {
            _xpts[i] = (int) ( _xscale * ( _model.getPosition( i ) - MIN_POSITION ) );
            _ypts[i] = 0;
        }
    }

    /*
     * Converts the model data to view coordinates and
     * draws it as a set of connected line segments.
     */
    private void drawPlots( Graphics g ) {

        final int numberOfPoints = _xpts.length;
        final double energyScale = _model.getEnergyScale();

        if ( _antialiasing && g instanceof Graphics2D ) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        }

        // Barrier
        g.setColor( BARRIER_COLOR );
        int ix = (int) ( _xscale * 0.5 * ( MAX_POSITION - MIN_POSITION - BARRIER_WIDTH ) );
        int jx = (int) ( _xscale * 0.5 * ( MAX_POSITION - MIN_POSITION + BARRIER_WIDTH ) );
        int iy = (int) ( _viewHeight - 1 - _yscale * ( 0.5 * MAX_ENERGY * energyScale - MIN_ENERGY ) ) + CONTROL_PANEL_HEIGHT;
        int jy = (int) ( _viewHeight - 1 - _yscale * ( 0 - MIN_ENERGY ) ) + CONTROL_PANEL_HEIGHT;
        g.drawLine( ix, iy, ix, jy );
        g.drawLine( jx, iy, jx, jy );
        g.drawLine( ix, iy, jx, iy );

        // Real part
        g.setColor( REAL_COLOR );
        for ( int i = 0; i < numberOfPoints; i++ ) {
            _ypts[i] = CONTROL_PANEL_HEIGHT + (int) ( _viewHeight - 1 - _yscale * ( _model.getPsi(i).getReal() - MIN_ENERGY ) );
            if ( i > 0 ) {
                g.drawLine( _xpts[i - 1], _ypts[i - 1], _xpts[i], _ypts[i] );
            }
        }

        // Imaginary part
        g.setColor( IMAGINARY_COLOR );
        for ( int i = 0; i < numberOfPoints; i++ ) {
            _ypts[i] = CONTROL_PANEL_HEIGHT + (int) ( _viewHeight - 1 - _yscale * ( _model.getPsi(i).getImaginary() - MIN_ENERGY ) );
            if ( i > 0 ) {
                g.drawLine( _xpts[i - 1], _ypts[i - 1], _xpts[i], _ypts[i] );
            }
        }

        // Probability Density (abs^2)
        g.setColor( PROBABILITY_DENSITY_COLOR );
        for ( int i = 0; i < numberOfPoints; i++ ) {
            _ypts[i] = CONTROL_PANEL_HEIGHT + (int) ( _viewHeight - 1 - _yscale * ( Math.pow( _model.getPsi(i).getAbs(), 2 ) - MIN_ENERGY ) );
            if ( i > 0 ) {
                g.drawLine( _xpts[i - 1], _ypts[i - 1], _xpts[i], _ypts[i] );
            }
        }
    }

    //----------------------------------------------------------------------
    // Event handling methods
    //----------------------------------------------------------------------

    private void restart() {
        reset();
        repaint();
    }

    private void play() {
        start();
        _playButton.setEnabled( false );
        _pauseButton.setEnabled( true );
        _stepButton.setEnabled( false );
        _thread = new Thread( this );
        _thread.start();
    }

    private void pause() {
        _playButton.setEnabled( true );
        _pauseButton.setEnabled( false );
        _stepButton.setEnabled( true );
        _thread = null;
    }

    private synchronized void step() {
        for ( int i = 0; i < STEPS_PER_FRAME; i++ ) {
            _model.propogate();
        }
        repaint();
    }

    private void selectBarrier() {
        BarrierChoice choice = (BarrierChoice) _barrierComboBox.getSelectedItem();
        _model.setEnergyScale( choice.getEnergyScale() );
        restart();
    }

    private void selectAntialiasing() {
        _antialiasing = _antialiasingCheckBox.isSelected();
        repaint();
    }

    //----------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------

    public void run() {
        while ( _thread != null ) {
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
}
