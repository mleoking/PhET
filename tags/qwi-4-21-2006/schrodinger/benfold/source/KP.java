import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Applet to show the Kronig-Penney model for periodic potential.
 */
public class KP extends BaseApplet2 {
    public void init() {
        super.init();

        double P = 3 / 2;
        double maxX = 4;
        boolean fold = false, neg = false, free = false;

        this.negative = neg;

        eqn = new KronigPenney( Math.PI * P );
        cache = new SolnCache( eqn );
        plotter = new Plotter( cache );
        plotter.setViewAndRange( negative ? -maxX : 0, maxX );
        plotter.setInterpolated( false );
        plotter.setLabels( "ka/pi", "Energy" );

        freePlotter = new Plotter( new FreeElectron() );
        freePlotter.setInterpolated( true );
        freePlotter.setAxes( false );
        freePlotter.setScalesEnabled( false, false );

        massPlotter = new Plotter( new EffectiveMass( cache ) );
        massPlotter.setLabels( "ka/pi", "Eff.\nmass" );
        massPlotter.bottomMargin = 0;


        setLayout( new GridLayout( 3, 3 ) );

        pField = addField( "Potential strength/pi", P );
        freeBox = addCheckbox( "Free electron", free );
        maxXField = addField( "Max ka/pi", maxX );
        negBox = addCheckbox( "Include negative", neg );
        add( statusLabel );
        add( new Label() );
        foldBox = addCheckbox( "Fold", fold );
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        double p = Math.PI * getDouble( pField );
        double maxX = getDouble( maxXField );

        validateInput( p, maxX );

        negative = negBox.getState();
        plotter.setRangeX( negative ? -maxX : 0, maxX );
        setFolded( foldBox.getState() );
        ( (KronigPenney)eqn ).p = p;
    }


    /**
     * Performs the validation for {@link #updateInput() updateInput()}.
     */
    public void validateInput( double p, double maxX ) {
        if( p < 0 ) {
            throw new IllegalArgumentException( "Need pot. str. >= 0" );
        }

        if( maxX <= 0 ) {
            throw new IllegalArgumentException( "Need max ka > 0" );
        }

        if( !safetyLimit ) {
            return;
        }

        if( p / Math.PI > 4 ) {
            throw new IllegalArgumentException( "Need pot. str. <= 4" );
        }
        if( maxX > 40 ) {
            throw new IllegalArgumentException( "Need max ka <= 40" );
        }
    }

    public void start() {
        super.start();
    }

    public void stop() {
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

        statusLabel.setText( "" );
        refresh();
    }

    protected void updateSolution( int w, int h ) {
        //int virtualWidth = ;
        int stepsPerPixel = 1;

        if( folded ) {
            w *= stepsPerPixel * plotter.rangeX / plotter.viewRange;
        }

        freePlotter.copyScale( plotter );
        freePlotter.updateSolution( w );
        freePlotter.scaleToFit( 0, w );
        freePlotter.setRangeY( 0, freePlotter.maxY );

        plotter.copyScale( freePlotter );
        plotter.updateSolution( w );

        massPlotter.copyScale( freePlotter );
        massPlotter.updateSolution( w );

        //massPlotter.scaleToFit(0,w);
        //massPlotter.setRangeY(-5,5);
        massPlotter.setRangeY( -0.05, 1.05 );
    }


    /**
     * Used to set whether or not the <i>x</i>-axis is &quot;folded&quot;
     * onto itself.
     */
    public void setFolded( boolean b ) {
        //	Careful...
        if( this.folded = b ) {
            plotter.setView( negative ? -1 : 0, 1 );
        }
        else {
            plotter.setView( plotter.minX, plotter.maxX );
        }
        freePlotter.copyScale( plotter );
    }

    public void doPaint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics();

        int hSpace = plotter.getSpaceX( fm );
        int vSpace = plotter.getSpaceY( fm );

        cache.invalidate();
        plotter.setFold( folded, 1, negative );
        freePlotter.setFold( folded, 1, negative );
        massPlotter.setFold( folded, 1, negative );


        int w = getSize().width / 2;
        int h = getGraphHeight();

        updateSolution( w - hSpace, h - vSpace );

        g.setColor( ENERGY_COL );
        plotter.paint( g, w, h );

        if( freeBox.getState() ) {
            g.setColor( FREE_COL );

            g.translate( hSpace, 0 );
            freePlotter.paint( g, w - hSpace, h - vSpace );
            g.translate( -hSpace, 0 );
        }

        g.translate( w, 0 );
        g.setColor( Color.black );
        massPlotter.paint( g, w, h );
        g.translate( -w, 0 );
    }


    protected TextField pField, maxXField;
    protected Checkbox foldBox, negBox, freeBox;

    /**
     * <code>true</code> if the <i>x</i>-axis is folded, else
     * <code>false</code>
     */
    protected boolean folded = false;

    /**
     * <code>true</code> if the <i>x</i>-axis includes negative values, else
     * <code>false</code>
     */
    protected boolean negative = true;

    /**
     * The Kronig-Penny model
     */
    protected Solvable eqn;

    /**
     * Stores solution, so that folding/unfolding does not require
     * recalculation
     */
    protected SolnCache cache;


    protected Plotter plotter, freePlotter, massPlotter;
    protected static Color
            AXES_COL = Color.blue,
            ENERGY_COL = Color.black,
            FREE_COL = Color.red;
}
