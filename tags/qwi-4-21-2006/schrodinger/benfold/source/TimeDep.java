import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Applet to animate the time-dependent Schrodinger equation
 */
public class TimeDep extends BaseApplet2 implements TimeDependent {
    public void init() {
        super.init();

        intWidth = 40;

        double minX = -10;
        double maxX = 10;


        function = new MultipleWells( 1 );
        function.setDepth( -5 );
        function.setWidth( 3 );

        quadFunction = new Quadratic();

        eqn = createEqn();
        eqn.dt = dt;

        anim = new AnimThread( this, this );

        plotter = new Plotter2( eqn );
        plotter.setRangeX( -intWidth / 2, +intWidth / 2 );
        plotter.setView( minX, maxX );
        plotter.setLabels( "x", "wave fn\n(arb. units)" );

        _init();

        eqn.initFunction( -intWidth / 2, intWidth / steps, steps );

        addControls();
    }


    /**
     * Performs initialisation specific to this applet
     */
    public void _init() {
        dt = 0.002;
        dx = 0.1;
        steps = (int)( intWidth / dx );

        potPlotter = new Plotter2( new SolvFunction( quadFunction ) );
        potPlotter.axes = false;
        potPlotter.setScalesEnabled( false, false );
        potPlotter.copyScale( plotter );
        potPlotter.bottomMargin = 0;
    }


    /**
     * Adds the GUI controls to this applet
     */
    protected void addControls() {
        setLayout( new GridLayout( 4, 6 ) );

        depthField = addField( "Depth of well", function.getDepth() );
        fnWidthField = addField( "Gaussian width", eqn.width );
        dtField = addField( "dt", dt );
        widthField = addField( "Width of well", function.getWidth() );
        fnStartField = addField( "Gaussian posn", eqn.x0 );
        dxField = addField( "dx", intWidth / steps );
        nWellsField = addField( "Number of wells", function.getWellCount() );
        energyField = addField( "Energy", eqn.getEnergy() );
        intWidthField = addField( "Interval width", intWidth );

        dxField.setEditable( false );
        dtField.setEditable( false );

        animButton = addButton( "Start" );
        //add(stepButton);
        resetButton = addButton( "Reset" );
        quadBox = addCheckbox( "Quadratic pot.", false );

        add( statusLabel );
    }


    /**
     * Creates the model to be used to obtain solution data.
     */
    protected TimeDepSchrodinger createEqn() {
        return new TimeDepSchrodinger( function );
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public synchronized void updateInput() {
        boolean quad = quadBox.getState();
        eqn.potFn = quad ? (Function)quadFunction : function;

        killAnimation();

        double dt = getDouble( dtField );
        double depth = getDouble( depthField );
        double width = getDouble( widthField );
        double intWidth = getDouble( intWidthField );
        double dx = getDouble( dxField );
        double energy = getDouble( energyField );
        int nWells = getInt( nWellsField );
        double x0 = getDouble( fnStartField );
        double fnW = getDouble( fnWidthField );

        int nSteps = (int)( intWidth / dx );

        validateInput( energy, depth, width, intWidth, nWells, nSteps, fnW, x0 );

        TimeDepSchrodinger s = (TimeDepSchrodinger)plotter.eqn;

        function.depth = depth;
        function.width = width;
        plotter.setRangeX( -intWidth / 2, +intWidth / 2 );
        if( function instanceof MultipleWells ) {
            ( (MultipleWells)function ).setWellCount( nWells );
        }

        //	Read adjusted width back in
        double w = ( (MultipleWells)function ).getWidth();
        if( w != width ) {
            widthField.setText( "" + w );
        }

        steps = (int)( intWidth / dx );
        eqn.dt = dt;
        eqn.energy = energy;
        eqn.x0 = x0;
        eqn.width = fnW;
        eqn.initFunction( plotter.minX, plotter.rangeX / steps, steps );
    }


    /**
     * Performs the validation for {@link #updateInput() updateInput()}.
     */
    protected void validateInput( double energy, double depth, double width, double intWidth, int nWells, int nSteps, double fnW, double x0 ) {

        if( width <= 0 ) {
            throw new IllegalArgumentException( "well width must be > 0" );
        }

        if( intWidth <= 0 ) {
            throw new IllegalArgumentException( "range must be positive" );
        }

        if( plotter.viewRange < width ) {
            throw new IllegalArgumentException( "well wider than view" );
        }

        if( intWidth < plotter.viewRange ) {
            throw new IllegalArgumentException( "int. range < view" );
        }

        if( nWells < 0 ) {
            throw new IllegalArgumentException( "need wells >= 0" );
        }

        if( nWells > 10 ) {
            throw new IllegalArgumentException( "need wells <= 10" );
        }

        if( nSteps <= 100 ) {
            throw new IllegalArgumentException( "need int rng/dx > 100" );
        }

        if( !safetyLimit && fnW <= 0 ) {
            throw new IllegalArgumentException( "need gaus. width > 0" );
        }


        if( !safetyLimit ) {
            return;
        }

        if( depth > 1000 ) {
            throw new IllegalArgumentException( "need depth <= 1000" );
        }

        if( depth < -1000 ) {
            throw new IllegalArgumentException( "need depth >= -1000" );
        }

        if( Math.abs( energy ) > 10 ) {
            throw new IllegalArgumentException( "need abs(energy) <= 10" );
        }

        if( fnW < 0.1 ) {
            throw new IllegalArgumentException( "need initial width >= 0.1" );
        }

        if( x0 < plotter.minX || x0 > plotter.maxX ) {
            throw new IllegalArgumentException( "init. posn outside range" );
        }

        if( intWidth > 200 ) {
            throw new IllegalArgumentException( "need int rng <= 200" );
        }
    }


    public void start() {
        super.start();
    }

    public void stop() {
        anim.kill();
        super.stop();
    }

    public void actionPerformed( ActionEvent ae ) {
        if( ae != null && ae.getSource() == animButton ) {
            animate();
            return;
        }

        if( ae != null && ae.getSource() == resetButton ) {
            reset();
            refresh();
            return;
        }

        if( ae != null && ae.getSource() == stepButton ) {
            step();
            return;
        }

        if( !reset() ) {
            refresh();
            return;
        }

        statusLabel.setText( "" );
        refresh();
    }


    /**
     * Reqests that the animation be started if it is stopped or paused, and
     * paused if it has already started.  The label of the <code>Start</code>
     * button will be changed to reflect the state of the animation.
     */
    protected synchronized void animate() {
        if( anim.isRunning() ) {
            animButton.setLabel( "Resume" );
            anim.kill();
        }
        else {
            if( !anim.hasStarted() ) {
                updateInput();
            }
            animButton.setLabel( "Stop" );
            anim.setRange( 0, 1000 );
            anim.setSpeed( 1 );
            anim.setRefreshDelay( 5 );
            //eqn.setT(0);
            anim.start();
        }
    }


    /**
     * Requests the animation stop at the next convenient opportunity.
     */
    public synchronized void killAnimation() {
        if( anim.isRunning() ) {
            anim.kill();
        }
        if( !animButton.getLabel().equals( "Start" ) ) {
            animButton.setLabel( "Start" );
        }
    }


    /**
     * Executes one time step of the model, then refreshes the display.
     */
    protected synchronized void step() {
        eqn.advanceOnce( eqn.dt );
        refresh();
    }


    /**
     * Resets the model, causing the function data to be reset to a
     * gaussian.
     *
     * @return    <code>true</code> if initialisation succeeded,
     * <code>false</code> if there was an input validation error
     */
    protected synchronized boolean reset() {
        try {
            updateInput();
        }
        catch( IllegalArgumentException e ) {
            String message = e.getMessage();
            if( e instanceof NumberFormatException ) {
                message = "Bad number:  " + message;
            }
            statusLabel.setText( message );
            return false;
        }

        eqn.setT( 0 );
        //eqn.initFunction(plotter.minX, plotter.rangeX/steps, steps);
        anim.t = 0;
        refresh();
        return true;
    }


    /**
     * Executes one time step of the model
     */
    public void advanceOnce() {
        eqn.advanceOnce();
    }


    /**
     * Returns the time in the model
     */
    public double getT() {
        return eqn.getT();
    }

    public void doPaint( Graphics g ) {
        int margin = 0;
        int w = getSize().width - margin;
        int h = getGraphHeight();

        plotter.updateSolution( steps );
        plotter.setRangeY( 0.00, 1.05 );

        _paint( g, w, h );

        // Plot wave function
        g.setColor( WAVE_FN_COL );

        plotter.paint( g, w, h );
    }


    /**
     * Marks the potential function on the graph.  If the function consists
     * of square wells, then only discontinuity markers will be drawn.
     * Otherwise, the potential function will be plotted behind the graph.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     */
    public void _paint( Graphics g, int w, int h ) {

        potPlotter.updateSolution( steps );
        potPlotter.scaleToFit();

        int hSpace = plotter.getSpaceX( g.getFontMetrics() );
        int vSpace = plotter.getSpaceY( g.getFontMetrics() );
        g.translate( hSpace, 0 );

        //	Mark edges of well
        if( function instanceof SquareWell && !quadBox.getState() ) {
            g.setColor( WELL_BOUNDS_COL );
            plotter.paintVerticalMarkers( g, w - hSpace, h - vSpace, ( (SquareWell)function ).getMarkers( plotter.minX, plotter.minY ) );

        }

        if( quadBox.getState() ) {
            g.setColor( Color.blue );
            potPlotter.paint( g, w - hSpace, h - vSpace );
        }

        g.translate( -hSpace, 0 );

    }


    /**
     * The number of different <i>x</i> values to use in the model
     */
    protected int steps = DEFAULT_STEPS;
    protected TextField energyField, depthField, widthField, intWidthField, nWellsField, dxField, dtField, fnWidthField, fnStartField;
    protected Checkbox quadBox;
    protected Button resetButton, animButton, stepButton;

    /**
     * The time-dependent schrodinger equation
     */
    protected TimeDepSchrodinger eqn;

    /**
     * The thread used for animation, or <code>null</code> if no animation
     * is in progress.
     */
    protected AnimThread anim;

    /**
     * The step size for the time direction
     */
    protected double dt;

    /**
     * The step size for the <i>x</i> direction
     */
    protected double dx;

    /**
     * The size of the <i>x</i> axis interval to solve across
     */
    protected double intWidth;

    protected Plotter2 plotter, potPlotter;

    /**
     * The potential function to use when in &quot;wells mode&quot;
     */
    protected MultipleWells function;

    /**
     * The potential function to use when in &quot;quadratic mode&quot;
     */
    protected Quadratic quadFunction;
    protected static Color
            POT_COL = Color.blue,
            ENERGY_COL = Color.red,
            AXES_COL = Color.blue,
            WAVE_FN_COL = Color.black,
            WELL_BOUNDS_COL = Color.red;

    protected static int
		DEFAULT_STEPS = 4000,
		DEFAULT_NWELLS = 1;
}
