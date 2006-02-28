import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Vector;


/**
 * Applet to model diffusion.  Displays electrons moving in a rectangular
 * area, plus a temperature slider and momentum histograms.
 */
public class Diffusion extends BaseApplet2 implements Runnable {
    public void init() {
        super.init();

        electrons = new Vector();
        w = getSize().width;
        h = getGraphHeight() - 10;

        simW = 1;
        simH = h * simW / w;

        minT = 0;
        maxT = 600;

        setT( 300 );

        electronCount = 25;

        //spawnElectrons(5,26,simW/20);

        //electronSize = 5;
//		spawnElectrons(100,100,0.008);
//		electronSize = 1;

        setLayout( new GridLayout( 2, 3 ) );

        tempField = addField( "Temperature (K)", getT() );
        //add(statusLabel);
        animButton = addButton( "Start" );
        countField = addField( "Number of electrons", electronCount );
        //add(new Label());
        resetButton = addButton( "Reset" );


        tPlotter = new Plotter( new SolvFunction( new Function() {
            public double evaluate( double x ) {
                return getT();
            }
        } ) );
        tPlotter.setAxes( false );
        tPlotter.setScalesEnabled( true, true );
        tPlotter.noUnitsX = true;
        tPlotter.setLabels( "\n", "Temp.\n(K)" );
        tPlotter.setRangeY( minT, maxT );
        tPlotter.setViewAndRange( 0, 1 );

        double histW = 3;

        xHist = new Histogram( -histW, histW, 20 );
        yHist = new Histogram( -histW, histW, 20 );

        xvPlotter = new Plotter( xHist );
        xvPlotter.setViewAndRange( -histW, histW );
        xvPlotter.bottomMargin = 0;
        xvPlotter.setLabels( "dx/dt", null );

        yvPlotter = new Plotter( yHist );
        yvPlotter.setViewAndRange( -histW, histW );
        yvPlotter.bottomMargin = 0;
        yvPlotter.setLabels( "dy/dt", null );

        spawnElectrons( electronCount );

        tempListener = new TemperatureListener();
    }

    public void start() {
        super.start();

        addMouseListener( tempListener );
        addMouseMotionListener( tempListener );
    }

    public void stop() {
        stopAnim();
        removeMouseMotionListener( tempListener );
        removeMouseListener( tempListener );

        super.stop();
    }


    /**
     * Starts the animation.  This method will have no effect if the
     * animation is already in progress.
     */
    public synchronized void startAnim() {
        if( thr != null ) {
            return;
        }
        killed = false;
        ( thr = new Thread( this ) ).start();
    }


    /**
     * Stops the animation.  This method will have no effect if the
     * animation is not in progress.
     */
    public synchronized void stopAnim() {
        killed = true;
        try {
            thr.interrupt();
        }
        catch( NullPointerException e ) {
        }
    }


    /**
     * This method loops through an update/repaint cycle.  It is intended
     * to execute in a separate thread, so should not be invoked directly.
     * Instead, the {@link #startAnim() startAnim()} method should be used,
     * which will start this method in a new thread.
     */
    public void run() {
        animButton.setLabel( "Stop" );
        while( !killed ) {
            updateSimulation( dt );
            refresh();
            //	25 fps should be sufficient
            try {
                Thread.sleep( 40 );
            }
            catch( InterruptedException e ) {
            }
        }
        animButton.setLabel( "Start" );
        thr = null;
    }

    public void actionPerformed( ActionEvent ae ) {
        String message = "";
        try {
            updateInput();
            if( ae.getSource() == animButton ) {
                animate();
            }
            if( ae.getSource() == resetButton ) {
                reset();
            }
        }
        catch( IllegalArgumentException e ) {
            message = e.getMessage();
            if( e instanceof NumberFormatException ) {
                message = "Bad number:  " + e;
            }
        }

        statusLabel.setText( message );
        refresh();
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        double temp = getDouble( tempField );
        int count = getInt( countField );

        if( temp < minT ) {
            throw new IllegalArgumentException( "Need temp >= " + minT );
        }
        if( temp > maxT ) {
            throw new IllegalArgumentException( "Need temp <= " + maxT );
        }

        if( count < 1 ) {
            throw new IllegalArgumentException( "Need electrons >= 1" );
        }
        if( count > 10000 ) {
            throw new IllegalArgumentException( "Need electrons <= 10000" );
        }

        setT( temp );
        setCount( count );
    }


    /**
     * Toggles the animation.  If the animation is running, it will be
     * stopped, otherwise a new animation will be started.
     */
    public void animate() {
        if( thr != null ) {
            stopAnim();
        }
        else {
            startAnim();
        }
    }


    /**
     * Stops any running animation, and generates a new set of electrons
     * with random momentum.	The momentum components of the electrons will
     * follow an approximate gaussian distribution; the positions will be
     * so as to arrange the particles in an approximate square.
     */
    public synchronized void reset() {
        stopAnim();
        electrons = new Vector();
        //spawnElectrons(5,5,simW/20);
        spawnElectrons( electronCount );
    }

    public void doPaint( Graphics g ) {
        w = getSize().width;
        h = getGraphHeight();

        hSpace = tPlotter.getSpaceX( g.getFontMetrics() );
        vSpace = tPlotter.getSpaceY( g.getFontMetrics() );

        int graphX = ( 3 * w ) / 4;
        int graphW = w - graphX;

        sliderW = 20 + hSpace;
        sliderX = graphX - sliderW - 5;
        int dispW = sliderX - 5;
        tPlotter.updateSolution( 20 );

        simW = 1;
        simH = h * simW / dispW;

        g.setColor( Color.black );
        g.drawRect( 0, 0, dispW - 1, h - 1 );

        double xscl = ( dispW - 2 * electronSize ) / simW;
        double yscl = ( h - 2 * electronSize ) / simH;

        g.translate( electronSize, electronSize );

        g.setColor( Color.blue );
        for( int i = 0; i < electrons.size(); i++ ) {
            ( (Electron)electrons.elementAt( i ) ).paint( g, xscl, yscl );
        }

        g.translate( -electronSize, -electronSize );

        g.translate( sliderX, 0 );
        g.setColor( Color.red );
        tPlotter.paint( g, sliderW, h );
        g.translate( -sliderX, 0 );


        g.translate( graphX, 0 );

        xvPlotter.updateSolution( graphW - xvPlotter.getSpaceX( g.getFontMetrics() ) );
        xvPlotter.scaleToFit();
        xvPlotter.paint( g, graphW, h / 2 );

        g.translate( 0, h / 2 );

        yvPlotter.updateSolution( graphW - xvPlotter.getSpaceX( g.getFontMetrics() ) );
        yvPlotter.scaleToFit();
        yvPlotter.paint( g, graphW, h / 2 );

        g.translate( -graphX, -h / 2 );
    }


    /**
     * Steps through the simulation logic by a specified time step.
     * Any size of step can be chosen, with the proviso that it should be
     * sufficiently small that no particle can travel the entire distance
     * between two opposite walls within that time.
     *
     * @param    dt    the size of the time interval to consider
     */
    public void updateSimulation( double dt ) {
        double[] xv = new double[electrons.size()];
        double[] yv = new double[electrons.size()];

        for( int i = 0; i < electrons.size(); i++ ) {
            Electron e = (Electron)electrons.elementAt( i );
            xv[i] = e.xv;
            yv[i] = e.yv;
            e.update( dt );
        }

        xHist.setData( xv );
        yHist.setData( yv );
    }


    /**
     * Destroys any electrons present in the simulation, and replaces them
     * with a new set of electrons, arranged in a square, and with
     * (approximately) gaussian momentum distribution.
     *
     * @param    count    the number of electrons to create
     */
    public void spawnElectrons( int count ) {
        electrons = new Vector();
        int w = (int)Math.sqrt( count );
        if( w * ( w + 1 ) < count ) {
            w++;
        }
        double space = (double)1 / ( 3 * w );
        spawnElectrons( w, count, space );
        electronSize = (int)( getSize().width * space / 4 );
        electronSize = Math.min( electronSize, 5 );
        updateSimulation( 0 );
    }


    /**
     * Adds electrons to the simulation, arranged in a rectangle.
     *
     * @param    n    The number of electrons to add
     * @param    w    The number of electrons per row
     * @param    space    The spacing (simulation units) between electrons
     */
    public void spawnElectrons( int w, int n, double space ) {
        double meanEnergy = 1;
        double sd = 0.5;

        for( int i = 0; w * i < n; i++ ) {
            for( int j = 0; j < w && ( w * i + j < n ); j++ ) {
                double energy = 0;
//				while(energy<=0)
                //	energy = 1+0.1*gaussian();
                //energy = Math.sqrt(-2*Math.log(1-Math.random()));

                //energy = 0.5*energy*energy;
                energy = -Math.log( 1 - Math.random() );

                electrons.addElement( new Electron( ( i + 1 ) * space, ( j + 1 ) * space, energy ) );
                //electrons.addElement(new Electron((i+1)*space, (j+1)*space, gaussian(), gaussian()));
            }
        }
    }


    /**
     * Returns a gaussian-distributed random number, using
     * the Box-Muller transform.
     */
    protected double gaussian() {
        double x = 1 - Math.random();
        double y = 1 - Math.random();
        return Math.cos( 2 * Math.PI * x ) * Math.sqrt( -2 * Math.log( y ) );
    }


    /**
     * Returns the selected temperature
     */
    public double getT() {
        return temp;
    }

    /**
     * Sets the selected temperature
     */
    public void setT( double t ) {
        temp = t;
        double _3_k_over_m = 1;
        vMultiplier = Math.sqrt( _3_k_over_m * t );
    }


    /**
     * Returns the number of electrons
     */
    public int getCount() {
        return electronCount;
    }

    /**
     * Sets the number of electrons, resetting the simulation if
     * necessary.
     */
    public void setCount( int i ) {
        if( electronCount == i ) {
            return;
        }
        electronCount = i;
        stopAnim();
        spawnElectrons( electronCount );
    }


    protected int sliderW, sliderX, hSpace, vSpace;
    /**
     * The number of electrons
     */
    protected int electronCount;
    /**
     * The animation thread, or <code>null</code> if no animation is in
     * progress.
     */
    protected Thread thr;
    /**
     * <code>true</code> if the animation should terminate at the next
     * available opportunity, otherwise <code>false</code>.
     */
    protected boolean killed = false;
    /**
     * A Vector containing all electrons present in the simulation.
     */
    protected Vector electrons;
    /**
     * The size (in pixels) of circle to draw for an electron
     */
    protected int electronSize;
    /**
     * Local copy of the width of this applet
     */
    protected int w;
    /**
     * Local copy of the height of the display area
     */
    protected int h;
    /**
     * Width of the box, in simulation units
     */
    protected double simW;
    /**
     * Width of the box, in simulation units
     */
    protected double simH;
    /**
     * The time step to use (default 0.001)
     */
    protected double dt = 0.001;
    /**
     * The temperature
     */
    protected double temp;
    /**
     * The velocity multiplier.
     * Rather than adjusting the momentum of every electron whenever the
     * temperature is changed, a constant multiplier is applied to all velocities.
     * If the velocities were modified directly, inaccuracies would creep in
     * quite rapidly (temperature changes every time slider is &quot;seen&quot;
     * in a different position).
     */
    protected double vMultiplier;
    /**
     * The minimum temperature permitted
     */
    protected double minT;
    /**
     * The maximum temperature permitted
     */
    protected double maxT;
    protected Button animButton, resetButton;
    protected TextField tempField, countField;
    protected TemperatureListener tempListener;
    protected Plotter tPlotter, xvPlotter, yvPlotter;
    protected Histogram xHist, yHist;


    /**
     * Class to simulate a single electron
     */
    public class Electron {
        /**
         * Creates a new electron
         *
         * @param    x    The initial <i>x</i>-coordinate
         * @param    y    The initial <i>y</i>-coordinate
         * @param    xv    The initial <i>x</i> component of the velocity
         * @param    yv    The initial <i>y</i> component of the velocity
         */
        public Electron( double x, double y, double xv, double yv ) {
            this.x = x;
            this.y = y;
            this.xv = xv;
            this.yv = yv;
        }


        /**
         * Creates a new electron, with the specified energy, but a random
         * direction.
         *
         * @param    x    The initial <i>x</i>-coordinate
         * @param    y    The initial <i>y</i>-coordinate
         * @param    energy    The energy of the electron
         */
        public Electron( double x, double y, double energy ) {
            double m = 1;
            double v = Math.sqrt( 2 * energy / m );
            double angle = Math.random() * 2 * Math.PI;
            this.x = x;
            this.y = y;
            this.xv = v * Math.cos( angle );
            this.yv = v * Math.sin( angle );
        }


        /**
         * Plots this electron (as a filled circle).
         *
         * @param    g    The Graphics surface on which to paint
         * @param    xscl    The scaling factor for x coordinates
         * @param    yscl    The scaling factor for y coordinates
         */
        public void paint( Graphics g, double xscl, double yscl ) {
            int r = electronSize;
            int x = (int)( xscl * this.x );
            int y = (int)( yscl * this.y );
            if( r < 1 ) {
                g.drawLine( x, y, x, y );
            }
            else {
                g.fillOval( x - r, y - r, 2 * r, 2 * r );
            }
        }


        /**
         * Updates the position and velocity of this electron to reflect
         * a change in time.  If the particle hits a wall, the appropriate
         * momentum component will be negated.
         */
        public void update( double dt ) {
            dt *= vMultiplier;
            x += dt * xv;
            y += dt * yv;

            if( x < 0 ) {
                xv = -xv;
                x = -x;
            }

            if( x > simW ) {
                xv = -xv;
                x = 2 * simW - x;
            }

            if( y < 0 ) {
                yv = -yv;
                y = -y;
            }

            if( y > simH ) {
                yv = -yv;
                y = 2 * simH - y;
            }
        }


        double x, y, xv, yv;
    }


    /**    Listener to detect changes in the position of the temperature slider.
     */
    protected class TemperatureListener extends MouseAdapter implements MouseMotionListener {
        public void mousePressed( MouseEvent me ) {
            setPromptEnabled( false );
            requestFocus();
            statusLabel.setText( "" );
            update( me.getX(), me.getY(), true );
        }

        public void mouseReleased( MouseEvent me ) {
            update( me.getX(), me.getY(), false );
            active = false;
            setPromptEnabled( true );
        }

        public void mouseMoved( MouseEvent me ) {
        }

        public void mouseDragged( MouseEvent me ) {
            update( me.getX(), me.getY(), false );
        }

        protected void update( int x, int y, boolean mousePressed ) {
            int w = getSize().width;
            int h = getGraphHeight();

            if( mousePressed && x >= sliderX && x <= sliderX + sliderW ) {
                active = true;
            }

            if( !active ) {
                return;
            }

            double t = tPlotter.fromScreenY( h - vSpace - y, h - vSpace );
            t = Math.min( t, maxT );
            t = Math.max( t, minT);


			setT(t);
			tempField.setText(""+t);

			refresh();
    	}


		protected boolean active = false;
	}
}