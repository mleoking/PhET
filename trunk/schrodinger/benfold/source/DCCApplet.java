import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Applet to display <b>D</b>epletion, <b>C</b>urrent and
 * <b>C</b>apacitance.
 * <p/>
 * A {@link DepletionWidth DepletionWidth} object is used to show depletion
 * at a given voltage, which may be selected using the voltage sliders
 * (both graphs have a voltage slider; these are connected and equivalent).
 */
public class DCCApplet extends BaseApplet2 {
    public void init() {
        super.init();

        v0 = 0.7;
        maxV = 2;
        minV = -2;
        selectedV = 0.1;

        //	Values are for Silicon
        double Nd = 1e22;
        double Na = 1e24;

        double A = 1e-7;

        //Nd = 2e-2;
        //Na = 1e-1;

        depletion = new DepletionWidth( Na, Nd, minV );
        depletion.setV0( v0 );


        cur = new Current();
        cap = new Capacitance( v0, Nd, A );

        curPlotter = new Plotter2( new SolvFunction( cur ) );
        capPlotter = new Plotter2( new SolvFunction( cap ) );

        curPlotter.setGraphColor( Color.black );
        curPlotter.setViewAndRange( minV, maxV );
        curPlotter.setLabels( "Voltage (V)", "Current" );
        curPlotter.setAxes( false );
        curPlotter.bottomMargin = 0;

        capPlotter.setGraphColor( Color.black );
        capPlotter.setViewAndRange( minV, maxV );
        capPlotter.setLabels( "Voltage (V)", "Capacitance\n    (F)" );
        capPlotter.setAxes( false );
        capPlotter.bottomMargin = 0;

        setLayout( new GridLayout( 2, 4 ) );
        acceptorField = addField( "Acceptor density (m^-3)", depletion.getNa() );
        voltageField = addField( "Voltage (V)", selectedV );
        donorField = addField( "Donor density (m^-3)", depletion.getNd() );

        add( statusLabel );

        vListener1 = new GraphClickListener() {
            public void update( double x, double y ) {
                selectedV = x;
                voltageField.setText( "" + x );
                refresh();
            }
        };
        vListener2 = new GraphClickListener() {
            public void update( double x, double y ) {
                selectedV = x;
                voltageField.setText( "" + x );
                refresh();
            }
        };
        vListener1.setPlotter( curPlotter );
        vListener2.setPlotter( capPlotter );


    }

    public void doPaint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics();
        int w = getSize().width;
        int h = getGraphHeight();
        int margin = 8;
        int graphH = h / 2 - margin;
        int depletionH = 70;

        if( w == 0 ) {
            return;
        }

        int graphX = w / 3;
        int graphW = w - graphX;

        int align = capPlotter.getSpaceX( fm ) - curPlotter.getSpaceX( fm );

        curPlotter.setRect( graphX + align, h / 2 + margin / 2, graphW - align, graphH );
        curPlotter.updateSolution( g );
        curPlotter.scaleToFit();
        curPlotter.setRangeY( -1, 3 * ( cur.evaluate( selectedV ) + 1 ) - 1 );
        curPlotter.paint( g );

        capPlotter.setRect( graphX, margin / 2, graphW, graphH );
        capPlotter.updateSolution( g );
        capPlotter.scaleToFit();
        capPlotter.setMinY( 0 );
        capPlotter.paint( g );

        g.setColor( Color.red );
        int markerX = capPlotter.toScreenX( selectedV );

        paintDashedLineV( g, markerX, margin / 2, capPlotter.toScreenY( capPlotter.minY ), 500 );
        paintDashedLineV( g, markerX, h / 2 + margin / 2, curPlotter.toScreenY( curPlotter.minY ), 500 );


        g.translate( 0, ( h - depletionH ) / 2 );
        depletion.paint( g, selectedV, graphX, depletionH );
        g.translate( 0, -( h - depletionH ) / 2 );
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        double v = getDouble( voltageField );
        double Na = getDouble( acceptorField );
        double Nd = getDouble( donorField );

        if( v < minV ) {
            throw new IllegalArgumentException( "Need v >= " + minV );
        }
        if( v > maxV ) {
            throw new IllegalArgumentException( "Need v <= " + maxV );
        }

        depletion.setN( Na, Nd );
        cap.setNd( Nd );
        selectedV = v;
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

    public void start() {
        super.start();

        addMouseListener( vListener1 );
        addMouseListener( vListener2 );
        addMouseMotionListener( vListener1 );
        addMouseMotionListener( vListener2 );
    }

    public void stop() {
        removeMouseMotionListener( vListener1 );
        removeMouseMotionListener( vListener2 );
        removeMouseListener( vListener1 );
        removeMouseListener( vListener2 );

        super.stop();
    }


    /**
     * The current function
     */
    protected Current cur;
    /**
     * The capacitance function
     */
    protected Capacitance cap;

    protected Plotter2 curPlotter, capPlotter;
    /**
     * Listener for the voltage slider on the current graph
     */
    protected GraphClickListener vListener1;
    /**
     * Listener for the voltage slider on the capacitance graph
     */
    protected GraphClickListener vListener2;

    protected double selectedV;
    /**
     * The minimum permitted voltage
     */
    protected double minV;
    /**
     * The maximum permitted voltage
     */
    protected double maxV;
    /**
     * The value of v<sub>0</sub>
     */
    protected double v0;
    protected TextField voltageField, acceptorField, donorField;
    /**    The depletion width diagram	*/
    protected DepletionWidth depletion;
}