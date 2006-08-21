import java.awt.*;


/**
 * Applet to demonstrate the Haynes-Shockley experiment.  The function
 * is animated as it changes over time.  Graphs are provided, plotting
 * the integral of the function, and the function seen by an observer
 * at some fixed point; both of these are plotted with respect to time.
 */
public class HSApplet extends TimeDep implements TimeDependent {
    public void init() {
        super.init();

    }


    /**
     * Performs initialisation specific to the Haynes-Shockley experiment,
     * not required by the time-dependent Schrodinger applet.
     */
    public void _init() {
        dt = 0.001;
        dx = 0.05;
        eqn.dt = dt;
        eqn.setX0( -2 );
        observations = 200;

        steps = (int)( intWidth / dx );
        setObsX( 0 );
        eqn.initFunction( -intWidth / 2, intWidth / steps, steps );
        plotter.setLabels( "x", "hole\ndensity" );
        plotter.setAxes( false );

        integral = new TimeGraph( observations );
        observer = new TimeGraph( observations );

        intPlotter = new Plotter( integral );
        intPlotter.bottomMargin = 0;
        //intPlotter.setInterpolated(false);
        intPlotter.setAxes( false );
        //intPlotter.noUnitsX = true;
        //intPlotter.noUnitsY = true;
        intPlotter.setViewAndRange( 0, 1 );
        intPlotter.setRangeY( 0, 1.05 * eqn.integralCheck() );
        intPlotter.setLabels( "Number of holes / time", null );

        obsPlotter = new Plotter( observer );
        obsPlotter.bottomMargin = 0;
        //obsPlotter.setInterpolated(false);
        obsPlotter.setAxes( false );
        //obsPlotter.noUnitsX = true;
        //obsPlotter.noUnitsY = true;
        obsPlotter.setViewAndRange( 0, 1 );
        obsPlotter.setRangeY( 0, 1.05 );
        obsPlotter.setLabels( "Density (at obs pt) / time", null );

        startIntegral = eqn.integralCheck();
        obsWidth = 0;

        obsListener = new GraphClickListener() {
            public void update( double x, double y ) {
                setObsX( x );
                obsField.setText( "" + x );
                killAnimation();
                reset();
                refresh();
            }
        };
        obsListener.setPlotter( plotter );
    }


    /**
     * Returns the model to be used for this applet.
     */
    public TimeDepSchrodinger createEqn() {
        return new HaynesShockley();
    }


    /**
     * Adds GUI components to this applet.  Components required by
     * the time-dependent Schrodinger applet but not required here will
     * be created and initialised (to satisfy input validation requirements),
     * but will be hidden from view.
     */
    protected void addControls() {
        setLayout( new GridLayout( 4, 4 ) );

        depthField = new TextField( "1" );
        widthField = new TextField( "1" );
        nWellsField = new TextField( "1" );
        quadBox = new Checkbox( "" );
        dtField = new TextField( "" + dt );
        dxField = new TextField( "" + intWidth / steps );

//		depthField    = addField("Depth of well"		,function.getDepth());
        fnWidthField = addField( "Initial width", eqn.width );
//		dtField       = addField("dt"					,dt);
//		widthField    = addField("Width of well"		,function.getWidth());
        fnStartField = addField( "Initial posn", eqn.x0 );
//		dxField       = addField("dx"					,intWidth/steps);
//		nWellsField   = addField("Number of wells"		,function.getWellCount());
        energyField = addField( "Field strength", ( (HaynesShockley)eqn ).getField() );
        intWidthField = addField( "Interval width", intWidth );
        lifetimeField = addField( "Carrier lifetime", ( (HaynesShockley)eqn ).getLifetime() );
        obsField = addField( "Observation point", obsX );

        dxField.setEditable( false );
        dtField.setEditable( false );

        animButton = addButton( "Start" );
        //add(stepButton);
        resetButton = addButton( "Reset" );
//		quadBox = addCheckbox("Quadratic pot.",false);

        add( statusLabel );
    }

    public void start() {
        super.start();
        addMouseListener( obsListener );
        addMouseMotionListener( obsListener );
    }

    public void stop() {
        removeMouseMotionListener( obsListener );
        removeMouseListener( obsListener );
        super.stop();
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        double field = getDouble( energyField );
        double lifetime = getDouble( lifetimeField );
        double obs = getDouble( obsField );

        if( field < -10 ) {
            throw new IllegalArgumentException( "Need field >= -10" );
        }
        if( field > 10 ) {
            throw new IllegalArgumentException( "Need field <= 10" );
        }

        if( safetyLimit && ( obs < plotter.viewLeft || obs > plotter.viewRight ) ) {
            throw new IllegalArgumentException( "Obs pt outside range" );
        }

        if( obs < plotter.minX || obs > plotter.maxX ) {
            throw new IllegalArgumentException( "Obs pt outside range" );
        }

        if( lifetime < 0.01 ) {
            throw new IllegalArgumentException( "Need lifetime >= 0.01" );
        }

        //if(lifetime>1000)
        //	throw new IllegalArgumentException("Need lifetime <= 1000");

        super.updateInput();

        ( (HaynesShockley)eqn ).setField( field );
        ( (HaynesShockley)eqn ).setLifetime( lifetime );
        setObsX( obs );
    }


    /**
     * Performs one step of the simulation, and updates the graphs to
     * reflect the changes.
     */
    public void advanceOnce() {
        eqn.advanceOnce();
        integral.addObservation( eqn.integralCheck() );
        observer.addObservation( ( (HaynesShockley)eqn ).p[obsPoint] );
    }


    /**
     * Returns the time elapsed in the model.
     */
    public double getT() {
        return eqn.getT();
    }


    /**
     * Stops any running animation, resets the model to the initial
     * function, and clears the graphs.
     */
    protected synchronized boolean reset() {
        integral.reset();
        observer.reset();
        boolean b = super.reset();
        intPlotter.setRangeY( 0, 1.05 * ( startIntegral = eqn.integralCheck() ) );
        obsPlotter.setRangeY( 0, 1.05 );
        obsMax = 0;
        obsWidth = 0;
        return b;
    }

    public void doPaint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics();

        int margin = 8;
        int w = getSize().width - 2 * margin;
        int h = getGraphHeight();
        int graphW = w / 2;
        plotter.setRect( 0, 0, graphW, h );
        plotter.setGraphColor( Color.black );

        plotter.updateSolution( steps );
        plotter.setRangeY( 0.00, 1.05 );

        intPlotter.updateSolution( observations );
        obsPlotter.updateSolution( observations );

        double tolerance = 0;
        obsPlotter.scaleToFit();
        obsMax = ( obsMax > obsPlotter.maxY ) ? obsMax : ( 1 + tolerance ) * obsPlotter.maxY; //Math.max(obsMax, obsPlotter.maxY);
        obsPlotter.setRangeY( 0, ( obsMax != 0 ) ? obsMax : 1 );


        int hSpace = plotter.getSpaceX( fm );
        int vSpace = plotter.getSpaceY( fm );

        //_paint(g,w,h);


        plotter.paint( g );


        int markerX = hSpace + plotter.toScreenX( obsX, graphW - hSpace );
        g.setColor( Color.red );
        g.drawLine( markerX, 0, markerX, h - vSpace );
        //paintDashedLineV(g,markerX,0,h-vSpace,9);


        g.translate( graphW + 2 * margin, 0 );

        double x0 = intPlotter.minX;
        double step = intPlotter.rangeX / integral.getObservations().length;
        int hSpace2 = intPlotter.getSpaceX( fm );
        int vSpace2 = intPlotter.getSpaceY( fm );
        int nowX = hSpace2 + intPlotter.toScreenX( integral.getCurrentPos( x0, step ), graphW - hSpace2 );

        g.setColor( Color.black );
        intPlotter.paint( g, graphW, h / 2 );

        g.setColor( Color.blue );
        int startY = intPlotter.toScreenY( startIntegral, h / 2 - vSpace2 );
        paintDashedLineH( g, hSpace2, startY, graphW, 5 );

        g.setColor( new Color( 0xc0c0c0 ) );
        g.drawLine( nowX, 0, nowX, h / 2 - vSpace2 );

        g.translate( 0, h / 2 );

        g.setColor( Color.black );
        obsPlotter.paint( g, graphW, h / 2 );
        g.setColor( new Color( 0xc0c0c0 ) );
        g.drawLine( nowX, 0, nowX, h / 2 - vSpace2 );

        g.setColor( Color.black );
        String s = "Observed width:  ";
        g.drawString( s + getObservedWidth(), graphW - 50 - fm.stringWidth( s ), h / 4 );

        g.translate( -graphW - 2 * margin, -h / 2 );
    }


    /**
     * Attempts to calculate the width of the (skewed) gaussian seen by the
     * observer.
     *
     * @return The width of the gaussian, or zero if there is not enough data
     */
    public double getObservedWidth() {
        if( obsWidth == 0 ) {
            double[] obs = observer.getObservations();
            int i = new GaussianWidthFinder( obs ).getWidth( observer.maxIndex, observer.pos );
            obsWidth = i * obsPlotter.viewRange / obs.length;
        }
        return obsWidth;
    }


    /**
     * Sets the observation point.  The value of the function at this point
     * will be plotted against time on the lower-right graph.
     */
    public void setObsX( double d ) {
        obsX = d;
        obsPoint = (int)( steps * ( obsX - plotter.minX ) / plotter.rangeX );
    }


    /**
     * Listener to detect changes in the position of the observer.
     */
    protected GraphClickListener obsListener;
    /**
     * The selected observation point
     */
    protected double obsX;
    /**
     * The largest value &quot;seen&quot; so far at the observation point
     */
    protected double obsMax;
    /**
     * The integral at the start of the simulation
     */
    protected double startIntegral;
    /**
     * The width of the observed gaussian, or zero if unknown
     */
    protected double obsWidth;
    protected TextField lifetimeField, obsField;
    protected TimeGraph integral, observer;
    /**
     * The number of time samples before the graphs
     * &quot;wrap around&quot;
     */
    protected int observations;
    /**
     * The array index corresponding to the observation point
     */
    protected int obsPoint;
	protected Plotter intPlotter, obsPlotter;
}