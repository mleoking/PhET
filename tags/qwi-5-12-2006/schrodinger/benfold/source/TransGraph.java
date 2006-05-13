import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * Applet to produce graphs of transmission and reflection coefficient
 * against energy, for a finite number of evenly spaced potential wells of
 * identical width and depth.
 */
public class TransGraph extends BaseApplet2 {
    public void init() {
        super.init();

        function = new MultipleWells( 1 );
        eqn = new Transmission( function );

        function.setDepth( 5 );

        plotter = new Plotter( eqn );
        potPlotter = new Plotter( new SolvFunction( function ) );
        potPlotter.setLabels( "x", "Potential" );
        potPlotter.setAxes( false );

        plotter.setViewAndRange( 0, 20 );
        plotter.setRangeY( 0.0, 1.05 );
        plotter.setLabels( "Energy", "Transmission,\nreflection" );

        setLayout( new GridLayout( 4, 2 ) );

        minEnergyField = new TextField( "0" );
        maxEnergyField = new TextField( "20" );
        depthField = new TextField( "" + function.getDepth() );
        widthField = new TextField( "" + function.getWidth() );
        periodField = new TextField( "" + function.getPeriod() );
        nWellsField = new TextField( "" + function.getWellCount() );


        CheckboxGroup cbg = new CheckboxGroup();
        transmission = new Checkbox( "Transmission", cbg, false );
        reflection = new Checkbox( "Reflection", cbg, false );
        both = new Checkbox( "Both", cbg, false );
        cbg.setSelectedCheckbox( transmission );

        new MozillaWorkaround( transmission, cbg );
        new MozillaWorkaround( reflection, cbg );
        new MozillaWorkaround( both, cbg );

        Panel modePanel = new Panel();
        modePanel.setLayout( new GridLayout( 1, 3 ) );
        modePanel.add( transmission );
        modePanel.add( reflection );
        modePanel.add( both );

        register( transmission );
        register( reflection );
        register( both );

        addWithLabel( depthField, "Height" );
        addWithLabel( maxEnergyField, "Max energy" );

        addWithLabel( periodField, "Period" );
        addWithLabel( minEnergyField, "Min energy" );

        addWithLabel( widthField, "Width of barrier" );
        add( modePanel );

        addWithLabel( nWellsField, "Number of barriers" );
        add( statusLabel );


        abortListener = new MouseAdapter() {
            public void mousePressed( MouseEvent me ) {
                eqn.abortSolve();
            }
        };
    }


    /**
     * Helper method; puts a component and a label in a Panel, then adds it
     * to the GUI.
     *
     * @param    c    The component to add
     * @param    label    The text label associated with the component
     */
    protected void addWithLabel( Component c, String label ) {
        Panel p = new Panel();
        p.setLayout( new GridLayout( 1, 2 ) );
        p.add( new Label( label ) );
        p.add( c );
        add( p );
        register( c );
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        double minEnergy = Double.valueOf( minEnergyField.getText() ).doubleValue();
        double maxEnergy = Double.valueOf( maxEnergyField.getText() ).doubleValue();
        double depth = Double.valueOf( depthField.getText() ).doubleValue();
        double width = Double.valueOf( widthField.getText() ).doubleValue();
        double period = Double.valueOf( periodField.getText() ).doubleValue();
        int nWells = Integer.parseInt( nWellsField.getText() );

        validateInput( minEnergy, maxEnergy, depth, width, period, nWells );

        //Shrodinger s = (Shrodinger) plotter.eqn;

        //s.setEnergy(energy);
        function.setDepth( depth );
        function.setWidth( width );
        function.setPeriod( period );

        if( function instanceof MultipleWells ) {
            ( (MultipleWells)function ).setWellCount( nWells );
        }

        plotter.setViewAndRange( minEnergy, maxEnergy );

        statusLabel.setText( "" );
    }


    /**
     * Performs the validation for {@link #updateInput() updateInput()}.
     */
    protected void validateInput( double minEnergy, double maxEnergy, double depth, double width, double period, int nWells ) {
        if( minEnergy < 0 ) {
            throw new IllegalArgumentException( "need minEnergy > 0" );
        }

        if( maxEnergy <= minEnergy ) {
            throw new IllegalArgumentException( "need maxEnergy > minEnergy" );
        }

        if( period <= 0 ) {
            throw new IllegalArgumentException( "need period > 0" );
        }

        if( width <= 0 ) {
            throw new IllegalArgumentException( "need width > 0" );
        }

        if( width > period ) {
            throw new IllegalArgumentException( "need period >= width" );
        }

        if( nWells <= 0 ) {
            throw new IllegalArgumentException( "need barriers > 0" );
        }

        if( depth == 0 ) {
            throw new IllegalArgumentException( "need height " + ( safetyLimit ? " > 0" : "nonzero" ) );
        }

        /*
              Just to be extra careful
          */
        if( !safetyLimit ) {
            return;
        }

        if( nWells > 100 ) {
            throw new IllegalArgumentException( "need barriers <= 100" );
        }

        if( maxEnergy > 1000 ) {
            throw new IllegalArgumentException( "need maxEnergy <= 1000" );
        }

        if( depth > 100 ) {
            throw new IllegalArgumentException( "need height <= 100" );
        }

        if( depth < 0 ) {
            throw new IllegalArgumentException( "need height >= 0" );
        }

    }


    public void start() {
        super.start();

        addMouseListener( abortListener );
    }

    public void stop() {
        removeMouseListener( abortListener );

        super.stop();
    }

    public void actionPerformed( ActionEvent ae ) {
        try {
            updateInput();
        }
        catch( IllegalArgumentException e ) {
            String message = e.getMessage();
            if( e instanceof NumberFormatException ) {
                message = "Bad number:  " + message;
            }
            statusLabel.setText( message );
            return;
        }

        if( ae != null ) {
            eqn.flush();
        }
        refresh();
    }


    /**
     * Updates the solution data in the two plotters.
     *
     * @param    w    The width of the sample array (number of samples taken)
     * @param    h    Unused
     */
    protected void updateSolution( int w, int h ) {
        plotter.updateSolution( w );
        potPlotter.updateSolution( w );
        potPlotter.setRangeY( function.getLow(), function.getHigh() * 1.05 );
        //potPlotter.scaleToFit();
    }


    /**
     * Modified <code>refresh()</code> method; performs repainting in a
     * separate thread, so that events (such as an abort request) can still
     * be delivered by the AWT event dispatching thread.  If the calculations
     * are likely to take more than a couple of seconds, all the GUI controls
     * will be disabled (visibly, they will be greyed out, or some similar
     * effect will be applied), to ensure the user knows that the applet is
     * busy.
     * <p/>
     * A call to this method while repainting is already in progress will
     * have no effect.
     */
    public void refresh() {
        synchronized( this ) {
            final boolean lengthy = ( eqn.cache == null ) && ( function.getWellCount() > 5 );

            if( refreshThread != null ) {
                return;
            }

            refreshThread = new Thread() {
                public void run() {
                    setPriority( Thread.MIN_PRIORITY );
                    statusLabel.setText( "Calculating, click graph to cancel" );
                    if( lengthy ) {
                        lockControls();
                    }
                    refreshImpl();
                    statusLabel.setText( "" );
                    refreshThread = null;
                    if( lengthy ) {
                        unlockControls();
                    }
                }
            };

            refreshThread.start();
        }
    }


    /**
     * Calls the superclass implementation of <code>refresh()</code>
     */
    protected void refreshImpl() {
        super.refresh();
    }


    public void doPaint( Graphics g ) {
        int margin = GRAPH_MARGIN;
        int space = 0;//10+g.getFontMetrics().stringWidth("8.88");
        int w = getSize().width / 2 - space;
        int h = getGraphHeight();

        updateSolution( w, h );


        g.translate( w, 0 );


        if( both.getState() || transmission.getState() ) {
            g.setColor( WAVE_FN_COL );
            plotter.paint( g, w, h );
        }

        if( both.getState() || reflection.getState() ) {
            ( (Transmission)eqn ).wantTransmission = false;

            g.setColor( Color.red );
            plotter.updateSolution( w );
            plotter.paint( g, w, h );

            ( (Transmission)eqn ).wantTransmission = true;
        }

        g.translate( -w, 0 );

        //	Potential graph
        g.setColor( POT_COL );
        potPlotter.paint( g, w, h );
    }


    protected TextField minEnergyField, maxEnergyField, depthField, widthField, periodField, nWellsField;
    protected Checkbox transmission, reflection, both;
    protected Panel bottomPanel;

    /**
     * The object responsible for performing the calculations
     */
    protected Transmission eqn;

    /**
     * The thread used to perform the repainting, or <code>null</code> if
     * no repainting is in progress.
     */
    protected Thread refreshThread;

    /**
     * Listens for clicks on the graph, which indicate that a calculation
     * should be aborted.
     */
    protected MouseListener abortListener;

    protected Plotter plotter, potPlotter;

    /**
     * The potential function
     */
    protected MultipleWells function;

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
