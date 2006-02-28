import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


/**
 * This applet will plot the Fermi-Dirac distribution, and provides a slider
 * for temperature to be modified.
 * <p/>
 * Options are provided to allow plotting of the hole distribution, and to
 * show the Boltzmann distribution.  Either a standard or logarithmic scale
 * may be used.
 */
public class FDApplet extends BaseApplet2 {
    public void init() {
        super.init();

        minX = -0.1;
        maxX = 0.1;

        function = new FermiDirac();
        function.setT( 300 );
        function.setEF( 0.0 );

        boltzmann = new Boltzmann();
        boltzmann.setT( 300 );
        boltzmann.setEF( 0.0 );

        fdPlotter = new Plotter( new SolvFunction( function ) );
        fdPlotter.setViewAndRange( minX, maxX );
        fdPlotter.setRangeY( 0, 1.05 );
        fdPlotter.bottomMargin = 0;
        fdPlotter.setLabels( "Energy - Fermi-level (eV)", "F(E)" );

        bzPlotter = new Plotter( new SolvFunction( boltzmann ) );
        bzPlotter.copyScale( fdPlotter );
        bzPlotter.setScalesEnabled( false, false );
        bzPlotter.setAxes( false );

        holePlotter = new Plotter( new SolvFunction( new Function() {
            public double evaluate( double x ) {
                return function.evaluate( function.ef - x );
            }
        } ) );
        holePlotter.copyScale( fdPlotter );
        holePlotter.setScalesEnabled( false, false );
        holePlotter.setAxes( false );


        tPlotter = new Plotter( new SolvFunction( new Function() {
            public double evaluate( double x ) {
                return function.getT();
            }
        } ) );
        tPlotter.setScalesEnabled( false, true );
        tPlotter.setLabels( " \n ", "Temperature\n(K)" );
        tPlotter.setRangeY( 0, 1.05 * MAX_T );
        tPlotter.setViewAndRange( 0, 1 );

        setLayout( new GridLayout( 2, 4 ) );

        tempField = addField( "Temperature", function.getT() );
        rangeField = addField( "Energy range ", maxX );
        add( statusLabel );
        holeBox = addCheckbox( "Hole distribution", false );
        logBox = addCheckbox( "Logarithmic scale", false );
        approxBox = addCheckbox( "Boltzmann distribution", false );

        temperatureListener = new TemperatureListener();
    }

    public void start() {
        super.start();
        addMouseListener( temperatureListener );
        addMouseMotionListener( temperatureListener );
    }

    public void stop() {
        removeMouseMotionListener( temperatureListener );
        removeMouseListener( temperatureListener );
        super.stop();
    }

    public void doPaint( Graphics g ) {
        int w = getSize().width;
        int h = getGraphHeight();

        boolean fudge = logBox.getState() && Double.isInfinite( Math.log( function.evaluate( maxX ) ) );//()<2;
        if( fudge ) {
            //fdPlotter.noUnitsY = true;
            fdPlotter.infY = true;
            fdPlotter.setRangeX( minX, function.getEF() );
        }
        else {
            fdPlotter.infY = false;
            fdPlotter.setRangeX( minX, maxX );
        }


        vSpace = tPlotter.getSpaceY( g.getFontMetrics() );
        int hSpace = fdPlotter.getSpaceX( g.getFontMetrics() );

        g.translate( 3 * w / 4, 0 );
        g.setColor( Color.red );
        tPlotter.updateSolution( w / 4 );
        tPlotter.paint( g, w / 4, h );
        g.translate( -3 * w / 4, 0 );

        int graphW = 3 * w / 4 - hSpace;

        g.setColor( Color.black );
        fdPlotter.updateSolution( graphW );

        if( logBox.getState() ) {
            if( fudge ) {
                fdPlotter.setRangeY( -1.05, 0.05 );
            }
            else {
                fdPlotter.scaleToFit();
                double min = fdPlotter.minY;
                fdPlotter.setRangeY( min, 0.05 * fdPlotter.rangeY );
            }
        }
        else {
            fdPlotter.setRangeY( 0, 1.05 );
        }

        fdPlotter.paint( g, 3 * w / 4, h );

        if( fudge ) {
            g.setColor( Color.black );
            int x = fdPlotter.toScreenX( 0, 3 * w / 4 - hSpace ) + hSpace;
            g.drawLine( x, fdPlotter.toScreenY( 0, h ), x, fdPlotter.toScreenY( fdPlotter.minY, h ) );
        }

        if( holeBox.getState() ) {
            g.setColor( Color.gray );
            g.translate( hSpace, 0 );
            holePlotter.copyScale( fdPlotter );
            holePlotter.maxX = -fdPlotter.minX;
            holePlotter.minX = -fdPlotter.maxX;
            holePlotter.updateSolution( graphW );
            holePlotter.paint( g, graphW, h - vSpace );
            g.translate( -hSpace, 0 );
        }

        if( approxBox.getState() ) {
            g.translate( hSpace, 0 );
            bzPlotter.copyScale( fdPlotter );

            if( !fudge ) {
                g.setColor( Color.red );
                bzPlotter.setRangeX( 0, maxX );
                bzPlotter.updateSolution( graphW );
                bzPlotter.paint( g, graphW, h - vSpace );
            }
            g.setColor( Color.red );
            bzPlotter.setRangeX( minX, 0 - Double.MIN_VALUE );
            bzPlotter.updateSolution( graphW );
            bzPlotter.paint( g, graphW, h - vSpace );

            g.translate( -hSpace, 0 );
        }
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        fdPlotter.setLogarithmic( logBox.getState() );
        bzPlotter.setLogarithmic( logBox.getState() );
        holePlotter.setLogarithmic( logBox.getState() );

        double temp = getDouble( tempField );
        double range = getDouble( rangeField );

        if( temp < 0 ) {
            throw new IllegalArgumentException( "Need temp >= 0" );
        }
        if( temp > MAX_T ) {
            throw new IllegalArgumentException( "Need temp <= " + MAX_T );
        }
        if( range < 0.001 ) {
            throw new IllegalArgumentException( "Need range >= 0.001" );
        }
        if( range > 10 ) {
            throw new IllegalArgumentException( "Need range <= 10" );
        }

        minX = -( maxX = range );
        fdPlotter.setView( minX, maxX );
        function.setT( temp );
        boltzmann.setT( temp );
    }

    public void actionPerformed( ActionEvent ae ) {
        String message = "";
        try {
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


    protected TextField tempField, rangeField;
    protected Checkbox holeBox, logBox, approxBox;
    /**
     * The Fermi-Dirac distribution
     */
    protected FermiDirac function;
    /**
     * The Boltzmann distribution
     */
    protected FermiDirac boltzmann;
    protected Plotter fdPlotter, holePlotter, tPlotter, bzPlotter;
    protected TemperatureListener temperatureListener;
    protected int vSpace;
    /**
     * The maximum temperature permitted (minimum is always zero Kelvin)
     */
    protected double MAX_T = 600;
    /**
     * The minimum value visible on the x axis
     */
    protected double minX = -0.1;
    /**
     * The maximum value visible on the x axis
     */
    protected double maxX = 0.1;


    /**    Listens for changes in the position of the temperature slider
     */
    protected class TemperatureListener extends MouseAdapter implements MouseMotionListener {
        public void mousePressed( MouseEvent me ) {
            setPromptEnabled( false );
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

            if( mousePressed && x >= 3 * w / 4 && x <= w ) {
                active = true;
            }

            if( !active ) {
                return;
            }

            double t = tPlotter.fromScreenY( h - vSpace - y, h - vSpace );
            t = Math.min( t, MAX_T );
            t = Math.max( t, 0 );

            tempField.setText( "" + t );
            function.setT( t );
            boltzmann.setT( t );

            refresh();
        }


        protected boolean active = false;
	}
}