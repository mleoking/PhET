import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


/**
 * Applet to plot density of states, Fermi-Dirac distribution, and carrier
 * concentration.  The {@link RotatedPlotter RotatedPlotter} class is used
 * to allow the energy axes of all three graph to be parallel.
 * <p/>
 * A slider is provided to modify the Fermi level, and a temperature scale
 * is also provided.
 */
public class CCApplet extends BaseApplet2 {
    public void init() {
        super.init();

        gap = 1.12 / 2;

        minEF = -gap;
        maxEF = gap;

        minT = 0;
        maxT = 600;

        dispRange = 2;

        function = new CarrierConcentration( maxEF, false );
        holeFunction = new CarrierConcentration( minEF, true );
        fdFunction = new FermiDirac();
        dFunction = new DensityFunction( gap );

        plotter = new RotatedPlotter( new SolvFunction( function ) );
        plotter.setViewAndRange( dispRange * minEF, dispRange * maxEF );
        plotter.setAxes( false );
        plotter.setScalesEnabled( true, true );
        plotter.noUnitsY = true;
        plotter.noUnitsX = false;
        plotter.setLabels( "N(E) x F(E) / eV", "       " );
        plotter.bottomMargin = 0;

        holePlotter = new RotatedPlotter( new SolvFunction( holeFunction ) );
        holePlotter.copyScale( plotter );
        holePlotter.setAxes( false );
        holePlotter.setScalesEnabled( false, false );
        holePlotter.bottomMargin = 0;

        fPlotter = new RotatedPlotter( new SolvFunction( fdFunction ) );
        fPlotter.copyScale( plotter );
        fPlotter.setAxes( false );
        fPlotter.setScalesEnabled( true, true );
        fPlotter.noUnitsY = true;
        fPlotter.setLabels( "F(E)", "       " );

        dPlotter = new RotatedPlotter( new SolvFunction( dFunction ) );
        dPlotter.copyScale( fPlotter );
        dPlotter.setAxes( false );
        dPlotter.setScalesEnabled( true, true );
        dPlotter.setLabels( "N(E)", "Energy\n\n(eV from\nintrinsic)" );
        dPlotter.bottomMargin = 0;


        tPlotter = new Plotter( new SolvFunction( new Function() {
            public double evaluate( double x ) {
                return getT();
            }
        } ) );
        tPlotter.setViewAndRange( minT, maxT );
        tPlotter.setRangeY( minT, maxT );
        tPlotter.setScalesEnabled( false, true );
        tPlotter.setLabels( "\n\n", "Temp.\n(K)" );

        tempListener = new TemperatureListener();
        efListener = new EFListener();

        setEF( maxEF / 5 );
        setT( 300 );

        setLayout( new GridLayout( 4, 4 ) );
        fermiField = addField( "Fermi level - intrinsic", ef );
        tempField = addField( "Temperature (K)", t );
        gapField = addField( "Band gap size (eV)", 2 * gap );
        rangeField = addField( "Display range / gap", dispRange );
        ratioField = addField( "hole/electron mass ratio", 1 );
        add( new Label() );
        add( statusLabel );
        //add(new Label());
        CheckboxGroup cbg = new CheckboxGroup();
        logScale = new Checkbox( "logarithmic scaling", cbg, false );
        autoScale = new Checkbox( "automatic scaling", cbg, true );
        tempScale = new Checkbox( "whole range", cbg, false );
        noScale = new Checkbox( "lock scaling", cbg, false );
        add( logScale );
        add( autoScale );
        add( tempScale );
        add( noScale );
        new MozillaWorkaround( logScale, cbg );
        new MozillaWorkaround( autoScale, cbg );
        new MozillaWorkaround( tempScale, cbg );
        new MozillaWorkaround( noScale, cbg );
    }

    public void start() {
        super.start();

        addMouseListener( efListener );
        addMouseMotionListener( efListener );
        addMouseListener( tempListener );
        addMouseMotionListener( tempListener );
    }

    public void stop() {
        super.stop();

        removeMouseMotionListener( tempListener );
        removeMouseListener( tempListener );
        removeMouseMotionListener( efListener );
        removeMouseListener( efListener );
    }

    public void doPaint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics();
        int w = getSize().width;
        int h = getGraphHeight();

        hSpace = plotter.getSpaceX( fm );
        vSpace = plotter.getSpaceY( fm );
        graphW = w / 4;
        int sliderW = tPlotter.getSpaceX( fm ) + 30;
        graphW = ( w - ( sliderW + 16 ) ) / 3;

        int steps = h - hSpace;

        plotter.setRangeY( ef, plotter.maxY );
        holePlotter.setRangeY( holePlotter.minY, ef );

        boolean wasLog = plotter.isLogarithmic();

        plotter.setLogarithmic( logScale.getState() );
        holePlotter.setLogarithmic( logScale.getState() );

        plotter.updateSolution( steps );
        holePlotter.updateSolution( steps );
        fPlotter.updateSolution( steps );
        dPlotter.updateSolution( steps );


        double max = Math.max( plotter.maxX, holePlotter.maxX );
        double min = 0;

        if( tempScale.getState() || logScale.getState() ) {
            double x = minEF - 0.5 / FermiDirac.e_over_k * getT();
            double e0 = minEF, ef = minEF;
            double ratio = dFunction.getRatio();
            holeFunction.setEF( minEF );
            function.setEF( maxEF );
            max = Math.max( holeFunction.evaluate( x ), function.evaluate( -x ) );
            holeFunction.setEF( this.ef );
            function.setEF( this.ef );

            //ratio = Math.sqrt(ratio*ratio*ratio);
            //max = Math.max(ratio,1)*
            //	 (Math.sqrt(e0-x)/(1+Math.exp((ef-x)*CarrierConcentration.e_over_k/t)));
        }
        if( autoScale.getState() || ( noScale.getState() && wasLog ) ) {
            plotter.scaleToFit();
            holePlotter.scaleToFit();
            max = Math.max( plotter.maxX, holePlotter.maxX );
        }

        max = Math.max( 1e-100, max );

        if( logScale.getState() ) {
            min = 0;
            max = Math.log( max );
            //max = Math.max(holePlotter.soln[0],plotter.soln[steps-1]);
            max = Math.max( 50, max );
            max = Math.min( 500, max );
            //System.err.println(min);
        }

        plotter.setRangeX( min, max );
        holePlotter.setRangeX( min, max );
        fPlotter.setRangeX( 0, 1 );
        dPlotter.scaleToFit();

        //	Plot n(E), p(E)  (rightmost, save for temp slider)
        g.translate( 2 * graphW, 0 );

        g.setColor( Color.black );
        plotter.paint( g, graphW, h );

        g.translate( hSpace, 0 );
        g.setColor( Color.red );
        holePlotter.paint( g, graphW - hSpace, h - vSpace );
        g.translate( -hSpace, 0 );

        g.drawString( "n = " + toString( getN() ) + " / cubic metre", graphW / 2, 2 * fm.getHeight() );
        g.drawString( "p = " + toString( getP() ) + " / cubic metre", graphW / 2, h - vSpace - 2 * fm.getHeight() );

        g.translate( -2 * graphW, 0 );

        //	Plot markers
        int startX = dPlotter.getSpaceX( fm );
        int endX = 3 * graphW;
        int dash = 5;
        g.setColor( Color.blue );

        int y = plotter.toScreenY( getEF(), h - vSpace );
        g.drawLine( startX, y, endX, y );

        g.setColor( Color.gray );
        paintDashedLineH( g, startX, plotter.toScreenY( minEF, h - vSpace ), endX, dash );
        paintDashedLineH( g, startX, plotter.toScreenY( 0, h - vSpace ), endX, dash );
        paintDashedLineH( g, startX, plotter.toScreenY( maxEF, h - vSpace ), endX, dash );

        //	Plot Fermi-Dirac distribution
        g.translate( graphW, 0 );
        g.setColor( Color.blue );
        fPlotter.paint( g, graphW, h );
        g.translate( -graphW, 0 );

        //	Plot...  umm... the other distribution
        dPlotter.paint( g, graphW, h );

        //	Draw slider
        g.translate( w - sliderW, 0 );
        g.setColor( Color.red );
        tPlotter.updateSolution( sliderW );
        tPlotter.paint( g, sliderW, h );
        g.translate( -w + sliderW, 0 );
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


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        double ef = getDouble( fermiField );
        double t = getDouble( tempField );
        double gap = getDouble( gapField );
        double dispRange = getDouble( rangeField );
        double ratio = getDouble( ratioField );

        if( t < minT ) {
            throw new IllegalArgumentException( "Need t >= " + minT );
        }
        if( safetyLimit && t > maxT ) {
            throw new IllegalArgumentException( "Need t <= " + maxT );
        }

        if( gap / 2 == this.gap ) {
            if( ef < minEF ) {
                throw new IllegalArgumentException( "Need fermi level >= " + minEF );
            }
            if( ef > maxEF ) {
                throw new IllegalArgumentException( "Need fermi level <= " + maxEF );
            }
        }

        if( gap < 0.001 ) {
            throw new IllegalArgumentException( "Need gap >= 0.001" );
        }
        if( safetyLimit && gap > 1000 ) {
            throw new IllegalArgumentException( "Need gap <= 1000" );
        }

        if( safetyLimit && dispRange < 1.1 ) {
            throw new IllegalArgumentException( "Need range/gap >= 1.1" );
        }
        if( safetyLimit && dispRange > 20 ) {
            throw new IllegalArgumentException( "Need range/gap <= 20" );
        }

        if( safetyLimit && ratio < 0.1 ) {
            throw new IllegalArgumentException( "Need ratio >= 0.1" );
        }
        if( safetyLimit && ratio > 10 ) {
            throw new IllegalArgumentException( "Need ratio <= 10" );
        }


        setEF( ef );
        setT( t );
        setGap( gap / 2 );
        setDispRange( dispRange );
        setRatio( ratio );
    }


    /**
     * Returns the integral of the hole distribution.  The integral is not
     * found numerically - a formula for the integral is used.
     */
    public double getP() {
        CarrierConcentration c = holeFunction;
        double kT = getT() * c.k;
        double d = kT * 2 * c.m / ( c.h_bar * c.h_bar );
        d = Math.sqrt( Math.PI ) * Math.sqrt( d * d * d ) / ( 4 * Math.PI * Math.PI );
        return d * Math.exp( -( getEF() - c.e0 ) * c.e_over_k / getT() );
    }


    /**
     * Returns the integral of the electron distribution.  The integral is not
     * found numerically - a formula for the integral is used.
     */
    public double getN() {
        CarrierConcentration c = function;
        double kT = getT() * c.k;
        double d = kT * 2 * c.m / ( c.h_bar * c.h_bar );
        d = Math.sqrt( Math.PI ) * Math.sqrt( d * d * d ) / ( 4 * Math.PI * Math.PI );
        return d * Math.exp( -( c.e0 - getEF() ) * c.e_over_k / getT() );
    }


    /**
     * Returns the selected Fermi level
     */
    public double getEF() {
        return ef;
    }

    /**
     * Sets the selected Fermi level
     */
    public void setEF( double d ) {
        function.setEF( d );
        holeFunction.setEF( d );
        fdFunction.setEF( d );
        ef = d;
    }

    /**
     * Returns the selected temperature
     */
    public double getT() {
        return t;
    }

    /**
     * Sets the selected temperature
     */
    public void setT( double d ) {
        function.setT( d );
        holeFunction.setT( d );
        fdFunction.setT( d );
        t = d;
    }


    /**
     * Returns the size of the band gap.
     * The size is measured from the centre of the gap, so the width of the
     * gap is actually twice this value.
     */
    public double getGap() {
        return gap;
    }

    /**
     * Sets the size of the band gap.
     * The size is measured from the centre of the gap, so the width of the
     * gap is actually twice this value.
     */
    public void setGap( double d ) {
        minEF = -d;
        maxEF = d;
        gap = d;
        dFunction.setGapSize( d );
        if( Math.abs( getEF() ) > d ) {
            setEF( 0 );
            fermiField.setText( "0" );
        }
        function.setE0( d );
        holeFunction.setE0( -d );
    }


    /**
     * Returns the ratio between the size of the vertical range and the size
     * of the band gap.
     * <p/>
     * If the band gap occupies a quarter of the display, this method will
     * return <code>4</code>.
     */
    public double getDispRange() {
        return dispRange;
    }

    /**
     * Sets the ratio between the size of the vertical range and the size
     * of the band gap.
     * <p/>
     * If the band gap is to occupy a quarter of the display, then the value
     * <code>4</code> should be supplied.
     */
    public void setDispRange( double d ) {
        dispRange = d;
        double min = d * minEF;
        double max = d * maxEF;
        plotter.setViewAndRange( min, max );
        holePlotter.setViewAndRange( min, max );
        fPlotter.setViewAndRange( min, max );
        dPlotter.setViewAndRange( min, max );
    }


    /**
     * Sets the hole/electron effective mass ratio.
     */
    public void setRatio( double d ) {
        dFunction.setRatio( d );
        function.setRatio( d );
        holeFunction.setRatio( d );
    }


    protected int vSpace, hSpace, graphW;
    /**
     * The selected Fermi level
     */
    protected double ef;
    /**
     * The maximum Fermi level
     */
    protected double maxEF;
    /**
     * The minimum Fermi level
     */
    protected double minEF;
    /**
     * The minimum temperature
     */
    protected double minT;
    /**
     * The maximum temperature
     */
    protected double maxT;
    /**
     * The selected temperature
     */
    protected double t;
    /**
     * The selected band gap size
     */
    protected double gap;
    /**
     * The selected display to band gap size ratio
     */
    protected double dispRange;
    /**
     * Electron concentration function
     */
    protected CarrierConcentration function;
    /**
     * Hole concentration function
     */
    protected CarrierConcentration holeFunction;
    /**
     * The Fermi-Dirac function
     */
    protected FermiDirac fdFunction;
    /**
     * The density of states function
     */
    protected DensityFunction dFunction;
    protected Plotter plotter, holePlotter, tPlotter, fPlotter, dPlotter;
    /**
     * Listener for the Fermi level slider
     */
    protected EFListener efListener;
    /**
     * Listener for the temperature slider
     */
    protected TemperatureListener tempListener;
    protected TextField fermiField, tempField, gapField, rangeField, ratioField;
    protected Checkbox autoScale, noScale, tempScale, logScale;
    //public static final double n_i = 1e16;


    /**
     * Detects changes in the position of the temperature slider.
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

            if( mousePressed && x >= 3 * graphW && x <= w ) {
                active = true;
            }

            if( !active ) {
                return;
            }

            double t = tPlotter.fromScreenY( h - vSpace - y, h - vSpace );
            t = Math.min( t, maxT );
            t = Math.max( t, minT + 0.1 );


            setT( t );
            tempField.setText( "" + t );

            refresh();
        }


        protected boolean active = false;
    }


    /**
     * Detects changes in the position of the Fermi level slider.
     */
    protected class EFListener extends MouseAdapter implements MouseMotionListener {
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

            if( mousePressed && x < 3 * graphW && x > 0 ) {
                active = true;
            }

            if( !active ) {
                return;
            }

            double ef = plotter.fromScreenY( h - vSpace - y, h - vSpace );
            ef = Math.min( ef, maxEF );
            ef = Math.max( ef, minEF );


            setEF( ef );
            fermiField.setText( "" + ef );

            refresh();
        }


        protected boolean active = false;
    }


    /**
     Provides a simple representation of the density function.  At present,
     this function does not use the correct units; it is just for show, and
     is not depended upon in any calculations.
     */
    class DensityFunction implements Function {
        public DensityFunction( double gapSize ) {
            setGapSize( gapSize );
            setRatio( 1 );
        }

        public double evaluate( double x ) {
            double e0 = gapSize;
            if( Math.abs( x ) <= e0 ) {
                return 0;
            }

            if( x > 0 ) {
                return Math.sqrt( x - e0 );
            }
            else {
                return multiplier * Math.sqrt( -x - e0 );
            }
        }

        public double getGapSize() {
            return gapSize;
        }

        public void setGapSize( double d ) {
            gapSize = d;
        }

        public double getRatio() {
            return ratio;
        }

        public void setRatio( double d ) {
            ratio = d;
            multiplier = Math.sqrt( ratio * ratio * ratio );
        }



		protected double ratio, multiplier;
		protected double gapSize;
	}
}
