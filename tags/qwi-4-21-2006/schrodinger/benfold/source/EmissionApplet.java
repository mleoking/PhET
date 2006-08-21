import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;


/**
 * Displays an animated diagram to illustrate absorption, emission
 * (spontaneous and stimulated) and pumping.
 */
public class EmissionApplet extends BaseApplet2 implements Runnable {
    public void init() {
        super.init();

        simW = simH = 1;
        hGap = simW / 40;

        absorb = 0.1;
        emit = 0.1;
        spont = 0.04;
        pump = 0.1;

        dt = 0.003;
        //dt = 0.01;
        electronSpeed = 8;

        createElectrons();
        photons = new Vector();

        setLayout( new GridLayout( 4, 4 ) );

        addFields();

        inversionField = addField( "Inversion", 0.5 );
        exciteAll = addButton( "All excited" );
        add( new Label() );

        destroy = addButton( "Remove all photons" );
        invert = addButton( "Apply inversion" );
        exciteNone = addButton( "None excited" );
        add( statusLabel );

        buildSineTable( 30, 8 );
    }


    /**
     * Adds the text fields to the GUI
     */
    protected void addFields() {
        absorbField = addField( "Absorption rate", absorb );
        emitField = addField( "Stimulated emission rate", emit );

        spontField = addField( "Spontaneous emission rate", spont );
        pumpField = addField( "Pump rate", pump );
    }

    public void start() {
        super.start();
        killAnim();
        startAnim();
    }

    public void stop() {
        killAnim();
        super.stop();
    }

    public void doPaint( Graphics g ) {
        int w = getSize().width;
        int h = getGraphHeight();

        vMargin = (int)( 0.1 * h );
        hMargin = (int)( 0.1 * w );

        g.translate( hMargin, vMargin );

        excitedY = 0;
        normalY = h - 2 * vMargin;

        double xscl = ( w - 2 * hMargin ) / simW;
        double yscl = normalY / simH;

        int rectH = 5;
        g.setColor( new Color( 0xc0c0c0 ) );
        g.fillRect( 0, -rectH, w - 2 * hMargin, rectH );
        g.fillRect( 0, normalY, w - 2 * hMargin, rectH );

        g.translate( (int)( xscl * hGap / 2 ), 0 );
        paintPhotons( g, xscl, yscl );
        paintElectrons( g, xscl, yscl );
        g.translate( (int)( -xscl * hGap / 2 ), 0 );

        int textH = -rectH - ( vMargin - rectH - g.getFontMetrics().getHeight() ) / 2;
        g.drawString( "Inversion:  " + toString( calcInversion() ), 0, textH );

        g.translate( -hMargin, -vMargin );
    }


    /**
     * Paints all the photons in the simulation.
     *
     * @param    g        The Graphics surface on which to paint
     * @param    xscl Multiplier to convert simulation <i>x</i> coordinates
     * to screen coordinates
     * @param    yscl Multiplier to convert simulation <i>y</i> coordinates
     * to screen coordinates
     */
    protected void paintPhotons( Graphics g, double xscl, double yscl ) {

        for( int i = 0; i < photons.size(); i++ ) {
            ( (Photon)photons.elementAt( i ) ).paint( g, xscl, yscl );
        }
    }


    /**
     * Paints all the electrons in the simulation.
     *
     * @param    g        The Graphics surface on which to paint
     * @param    xscl Multiplier to convert simulation <i>x</i> coordinates
     * to screen coordinates
     * @param    yscl Multiplier to convert simulation <i>y</i> coordinates
     * to screen coordinates
     */
    protected void paintElectrons( Graphics g, double xscl, double yscl ) {
        for( int i = 0; i < electrons.length; i++ ) {
            electrons[i].paint( g, xscl, yscl );
        }
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        double absorb = getDouble( absorbField );
        double emit = getDouble( emitField );
        double spont = getDouble( spontField );
        double pump = getDouble( pumpField );

        if( absorb < 0 ) {
            throw new IllegalArgumentException( "Need absorption >= 0" );
        }
        if( absorb > 1 ) {
            throw new IllegalArgumentException( "Need absorption <= 1" );
        }

        if( emit < 0 ) {
            throw new IllegalArgumentException( "Need stim. emission >= 0" );
        }
        if( emit > 1 ) {
            throw new IllegalArgumentException( "Need stim. emission <= 1" );
        }

        if( spont < 0 ) {
            throw new IllegalArgumentException( "Need spont. emission >= 0" );
        }
        /*if(safetyLimit && spont>1)
              throw new IllegalArgumentException("Need spont. emission <= 1");*/

        if( pump < 0 ) {
            throw new IllegalArgumentException( "Need pump rate >= 0" );
        }
        /*if(safetyLimit && pump>1)
              throw new IllegalArgumentException("Need pump rate <= 1");*/

        this.pump = pump;
        this.absorb = absorb;
        this.emit = emit;
        this.spont = spont;
    }

    public void actionPerformed( ActionEvent ae ) {
        if( ae.getSource() == exciteAll ) {
            setAllExcited( true );
            return;
        }

        if( ae.getSource() == exciteNone ) {
            setAllExcited( false );
            return;
        }

        if( ae.getSource() == destroy ) {
            destroyPhotons();
            return;
        }

        String message = "";
        try {
            if( ae.getSource() == invert ) {
                invert();
                return;
            }

            updateInput();
        }
        catch( IllegalArgumentException e ) {
            message = e.getMessage();
            if( e instanceof NumberFormatException ) {
                message = "Bad number:  " + message;
            }
        }
        statusLabel.setText( message );
        refresh();
    }


    /**
     * Part of initialisation; creates a row of electrons, with spacing
     * determined by <code>hGap</code>.  Any prior existing electrons will
     * be lost; all newly created electrons will be at the lower energy
     * level.
     */
    public void createElectrons() {
        int n = (int)( simW / hGap );
        electrons = new Electron[n];
        for( int i = 0; i < n; i++ ) {
            electrons[i] = new Electron( i * hGap );
        }
    }


    /**
     * Calls the {@link EmissionApplet.Photon#update(double) update()} method of every
     * photon, thus advancing them through the specified time interval.
     * <p/>
     * If there are currently no photons in the simulation, {@link
     * #spawnPhoton() spawnPhoton()} will be called to create one.
     */
    public void updatePhotons( double dt ) {
        if( photons.isEmpty() ) {
            spawnPhoton();
            destroyPhotons = false;
        }


        for( int i = 0; i < photons.size(); i++ ) {
            ( (Photon)photons.elementAt( i ) ).update( dt );
        }

        cullPhotons();
    }


    /**
     * Calls the {@link EmissionApplet.Electron#update(double) update()} method of every
     * electron, thus advancing them through the specified time interval.
     */
    public void updateElectrons( double dt ) {
        for( int i = 0; i < electrons.length; i++ ) {
            electrons[i].update( dt );
        }
    }


    /**
     * Removes from the list any photons which have left the simulation.
     *
     * @return The number of photons leaving
     */
    protected int cullPhotons() {
        int count = 0;
        for( int i = 0; i < photons.size(); i++ ) {
            if( destroyPhotons || ( (Photon)photons.elementAt( i ) ).isFinished() ) {
                photons.removeElementAt( i-- );
                count++;
            }
        }

        destroyPhotons = false;
        return count;
    }


    /**
     * Creates a new photon at the &quot;start&quot; end of the simulation,
     * with random vertical position (of no real consequence), and random
     * phase.
     */
    public void spawnPhoton() {
        photons.addElement( new Photon( -0.2, simH * ( 0.1 + 0.8 * Math.random() ) ) );
    }


    /**
     * Performs the work of the simulation: an update/repaint loop.
     */
    public void run() {
        while( thr == Thread.currentThread() ) {
            //System.err.println(thr);
            updatePhotons( dt );
            Thread.yield();
            updateElectrons( dt );
            Thread.yield();
            refresh();
            try {
                Thread.sleep( 20 );
            }
            catch( InterruptedException e ) {
                return;
            }
        }
    }


    /**
     * Requests that the simulation be started.  If a simulation is already
     * in progress, this method will have no effect.
     */
    public synchronized void startAnim() {
        if( thr != null ) {
            return;
        }

        ( thr = new Thread( this ) ).start();
    }


    /**
     * Requests that the currently running simulation should stop at the
     * next opportunity.  If no simulation is running, this method will have
     * no effect.
     */
    public void killAnim() {
        Thread t = thr;
        thr = null;
        try {
            t.interrupt();
        }
        catch( NullPointerException e ) {
        }
    }


    /**
     * Creates a table of sine values (for painting the photons as waves).
     *
     * @param    len    The wavelength, in pixels
     * @param    amp    The amplitude, in pixels
     */
    public void buildSineTable( int len, int amp ) {
        sinTable = new int[len];
        for( int i = 0; i < sinTable.length; i++ ) {
            sinTable[i] = (int)( amp * Math.sin( 2 * Math.PI * i / len ) );
        }
    }


    /**
     * Looks up the sin value to use for a particular <i>x</i>.  Since this
     * used the table built by {@link #buildSineTable(int,int)
     * buildSineTable()}, it's much faster than <code>Math.sin()</code>.
     */
    public int sin( int x ) {
        int len = sinTable.length;
        if( x < 0 ) {
            x += getSize().width * len;
        }
        return sinTable[x % len];
    }


    /**
     * Paints a sine wave to represent a photon.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    x    The <i>x</i> coordinate of the leading end of the wave
     * @param    y    The <i>y</i> coordinate of the leading end of the wave
     * @param    amp    ignored; all waves now use the cached sine table
     * @param    wavelength    used only to determine how much wave to draw.
     * @param    phase    The phase shift for the wave
     */
    protected void paintWave( Graphics g, int x, int y, int amp, double wavelength, int phase ) {
        //int len = (int)(3*wavelength);
        int len = waveDispLen;
        int xd = len;
        int oldY = y + sin( xd - phase );//(int)(amp*Math.sin(2*Math.PI*(xd-phase)/wavelength));
        int step = 2;
        int limit = Math.max( x - getSize().width + hMargin + 10, 0 );
        //int fade = (int)(255*(1-life));
        for( ; xd >= limit; xd -= step ) {
            int col = ( 255 * xd ) / len;// Math.min((255*xd)/len + fade,255);
            g.setColor( new Color( 0xff, col, col ) );
            g.drawLine( x - xd - step, oldY, x - xd, oldY = y + sin( xd - phase ) );//(int)(amp*Math.sin(2*Math.PI*(xd-phase)/wavelength)));
        }

        if( limit != 0 ) {
            return;
        }

        // add an arrowhead for fun
        double angle = Math.PI / 4 - Math.cos( 2 * Math.PI * ( -phase ) / wavelength );

        int ax1 = (int)( 0.7 * amp * Math.cos( angle ) );
        int ay1 = (int)( 0.7 * amp * Math.sin( angle ) );
        g.drawLine( x - ax1, oldY - ay1, x, oldY );
        g.drawLine( x - ay1, oldY + ax1, x, oldY );
    }


    /**
     * Finds the first electron within a specified interval.  Used by
     * photons to find an electron to interact with.
     */
    protected Electron findElectron( double left, double right ) {
        int startIndex = (int)( left / hGap );
        int endIndex = (int)( ( right + 1 ) / hGap );
        for( int i = startIndex; i < endIndex; i++ ) {
            try {
                if( electrons[i].x < right && electrons[i].x >= left ) {
                    //System.err.println("foo");
                    return electrons[i];
                }
            }
            catch( IndexOutOfBoundsException e ) {
            }
        }
        //System.err.println("bar");
        return null;
    }


    /**
     * Sets all the electrons to either the upper or lower energy level.
     *
     * @param    b    <code>true</code> if all electrons should be excited,
     * <code>false</code> if all electrins should be unexcited
     * (bored?)
     */
    public void setAllExcited( boolean b ) {
        for( int i = 0; i < electrons.length; i++ ) {
            electrons[i].setExcited( b );
        }
    }


    /**
     * Applys the selected inversion to the electrons.  Electrons will
     * be randomly distributed according to the inversion, with no regard
     * to their previous energy level.
     */
    public void invert() {
        double d = getDouble( inversionField );

        if( d < 0 ) {
            throw new IllegalArgumentException( "Need inversion >= 0" );
        }
        if( d > 1 ) {
            throw new IllegalArgumentException( "Need inversion <= 1" );
        }

        int up = (int)( electrons.length * d );
        int down = electrons.length - up;

        Vector v = new Vector();
        for( int i = 0; i < electrons.length; i++ ) {
            v.addElement( electrons[i] );
        }
        for( int i = 0; i < up; i++ ) {
            int index = (int)( v.size() * Math.random() );
            Electron e = (Electron)v.elementAt( index );
            v.removeElementAt( index );
            e.setExcited( true );
        }
        for( int i = 0; i < v.size(); i++ ) {
            ( (Electron)v.elementAt( i ) ).setExcited( false );
        }
    }


    /**
     * Measures the population inversion (the proportion of electrons at the
     * upper energy level.
     */
    public double calcInversion() {
        int len = electrons.length;
        int down = 0;
        int up = 0;
        for( int i = 0; i < len; i++ ) {
            if( electrons[i].canEmit() ) {
                up++;
            }
            if( electrons[i].canAbsorb() ) {
                down++;
            }
        }

        return (double)up / ( up + down );
    }


    /**
     * Causes all photons in the simulation to be destroyed
     */
    public void destroyPhotons() {
        destroyPhotons = true;
    }


    /**
     * Indicates that on the next logic cycle, all photons should be culled
     */
    protected boolean destroyPhotons;


    /**
     * Probability that a passing photon will be absorbed by a particular
     * electron
     */
    protected double absorb;

    /**
     * Probability that a passing photon will trigger an emission from a
     * particular electron
     */
    protected double emit;

    /**
     * Average proportion of excited electrons which will spontaneously emit
     * a photon in the time it takes for a photon to pass through the
     * simulation.
     */
    protected double spont;

    /**
     * Average proportion of non-excited electrons which will be pumped to
     * the upper energy level in the time it takes for a photon to pass
     * through the	simulation.
     */
    protected double pump;


    /**
     * The thread used to perform the work of the simulation
     */
    protected Thread thr;

    /**
     * A list of all the photons in the simulation
     */
    protected Vector photons;

    /**
     * An array of all the electrons in the simulation
     */
    protected Electron[] electrons;

    /**
     * The width (in simulation units) of the simulation
     */
    protected double simW;

    /**
     * The width (in simulation units) of the simulation
     */
    protected double simH;

    /**
     * The width (in simulation units) of the spacing between electrons
     */
    protected double hGap;

    /**
     * The rate of advancement of simulation time
     */
    protected double dt;

    /**
     * The ratio of electron speed to photon speed
     */
    protected double electronSpeed;

    /**
     * Proportion of the simulation reserved at the top and bottom for
     * electrons.  No photons will be created in those regions.
     */
    protected double margin = 0.1;

    /**
     * The <i>y</i> coordinate at which excited electrons should be drawn
     */
    protected int excitedY;

    /**
     * The <i>y</i> coordinate at which non-excited electrons should be drawn
     */
    protected int normalY;
    protected int waveDispLen = 50, hMargin, vMargin;

    /**
     * Cache of sine values
     */
    protected int[] sinTable;
    protected TextField absorbField, emitField, spontField, inversionField, pumpField;
    protected Button exciteAll, exciteNone, invert, destroy;


    /**
     * Models a photon
     */
    public class Photon implements Cloneable {
        /**
         * Creates a new photon with random phase
         *
         * @param    x    The starting <i>x</i> coordinate
         * @param    y    The starting <i>y</i> coordinate
         */
        public Photon( double x, double y ) {
            this.x = x;
            this.y = y;
            this.phase = (int)( 30 * Math.random() );
            dead = false;
        }


        /**
         * Creates a new photon with random phase and <i>y</i> position
         *
         * @param    x    The starting <i>x</i> coordinate
         */
        public Photon( double x ) {
            this( x, 0.1 + 0.8 * Math.random() );
        }


        /**
         * Creates a duplicate of this photon, with the same <i>x</i>
         * coordinate and phase, but a random <i>y</i> position.
         */
        public Object clone() {
            try {
                Photon p = (Photon)super.clone();
                p.x = x;
                p.y = 0.1 + 0.8 * Math.random();
                p.phase = phase;
                return p;
            }
            catch( CloneNotSupportedException e ) {
                throw new RuntimeException( "<Object>.clone() failed (!)" );
            }
        }


        /**
         * Displays this photon
         *
         * @param    g        The Graphics surface on which to paint
         * @param    xscl Multiplier to convert simulation <i>x</i>
         * coordinates	to screen coordinates
         * @param    yscl Multiplier to convert simulation <i>y</i>
         * coordinates to screen coordinates
         */
        public void paint( Graphics g, double xscl, double yscl ) {
            int r = 3;
            int x = (int)( xscl * this.x );
            int y = (int)( yscl * this.y );

            g.setColor( Color.red );
            int wavelength = 30;
            paintWave( g, x, y, 8, wavelength, ( x % wavelength ) + phase );
            //g.fillOval(x-r,y-r,2*r,2*r);
            //System.err.println("Painting photon("+x+","+y+")");
        }


        /**
         * Advances this photon through time by the specified interval
         */
        public void update( double dt ) {
            Electron e = findElectron( x, x = x + dt );
            if( e == null || e.isMoving() ) {
                return;
            }

            if( Math.random() < absorb && e.canAbsorb() ) {
                e.absorb( this );
                return;
            }

            if( Math.random() < emit && e.canEmit() ) {
                e.emit( this );
                return;
            }
        }


        /**
         * @return    <code>true</code> if this photon has been absorbed or has
         * otherwise left the simulation, else <code>false</code>
         */
        public boolean isFinished() {
            return dead || x >= simW + 0.2;
        }


        /**
         * Indicates that this photon has been absorbed
         */
        public void die() {
            dead = true;
        }


        /**
         * <code>true</code> if this photon has been absorbed, else
         * <code>false</code>
         */
        protected boolean dead;
        protected double x, y;
        protected int phase;
    }


    /**    Models an electron
     */
    public class Electron {
        /**
         * Creates a new (non-excited) electron
         *
         * @param    x    The <i>x</i> position
         */
        public Electron( double x ) {
            this.x = x;
            excited = false;
            progress = 1;
        }


        /**
         * Displays this Electron
         *
         * @param    g        The Graphics surface on which to paint
         * @param    xscl Multiplier to convert simulation <i>x</i>
         * coordinates	to screen coordinates
         * @param    yscl Multiplier to convert simulation <i>y</i>
         * coordinates to screen coordinates
         */
        public void paint( Graphics g, double xscl, double yscl ) {
            int r = 3;
            int x = (int)( xscl * this.x );

            int destY = excited ? excitedY : normalY;
            int srcY = !excited ? excitedY : normalY;

            int y = (int)( progress * destY + ( 1 - progress ) * srcY );

            g.setColor( Color.blue );
            g.fillOval( x - r, y - r, 2 * r, 2 * r );
        }


        /**
         * @return    <code>true</code> if this electron is excited, else
         * <code>false</code>
         */
        public boolean isExcited() {
            return excited;
        }


        /**
         * Absorbs the specified photon, causing this electron to become
         * excited.
         */
        public void absorb( Photon p ) {
            p.die();
            excited = true;
            progress = 0;
        }


        /**
         * Emits the specified photon, causing this electron to become
         * non-excited.
         */
        public void emit( Photon p ) {
            photons.addElement( p = (Photon)p.clone() );
            p.x -= dt;
            excited = false;
            progress = 0;
        }


        /**
         * @return    <code>true</code> if this electron is in transition
         * between energy levels, <code>false</code> if this electron
         * is stationary.
         */
        public boolean isMoving() {
            return progress != 1;
        }


        /**
         * Indicates whether this electron is in a suitable state to absorb
         * a photon.
         */
        public boolean canAbsorb() {
            return !excited;
        }


        /**
         * Indicates whether this electron is in a suitable state for
         * stimulated emission of a photon.
         */
        public boolean canEmit() {
            return excited;
        }


        /**
         * Advances this photon through time by the specified interval
         */
        public void update( double dt ) {
            progress = Math.min( progress + electronSpeed * dt, 1 );
            /*if(absorbed!=null && progress>absPos)
               {
                   absorbed.die();
                   absorbed = null;
               } */

            //	Spontaneous emission
            if( !isMoving() && isExcited() && Math.random() < spont * dt ) {
                emit( new Photon( x ) );
            }

            if( !isMoving() && !isExcited() && Math.random() < pump * dt ) {
                pump();
            }
        }


        /**
         * Moves this electron to the upper energy level
         */
        public void pump() {
            excited = true;
            progress = 0;
        }


        /**    Immediately (without animation) moves this electron to the
         specified energy level.
         @param    b    <code>true</code> if the electron is to be moved to
         the upper energy level, <code>false</code> if the
						electron is to be moved to the lower energy level.
		*/
		public void setExcited(boolean b)
		{
			excited = b;
			progress = 1;
		}



		protected boolean excited;
		protected double x, progress;
	}
}