import java.awt.*;
import java.awt.event.*;


/**
 * Applet to illustrate the shooting method.  Displays a schematic of the
 * potential function, with a marker indicating the selected energy level.
 * The Schrodinger equation is &quot;solved&quot; for this energy level,
 * and the resulting function is displayed.  (The function is not actually
 * a solution unless it tends to zero towards the extremes of the <i>x</i>
 * axis.
 * <p/>
 * Features include a quadratic potential, the option to plot psi or
 * psi<sup>2</sup>, and the ability to solve the equation (ie. find energy
 * for which the above condition holds).
 */
public class Shoot extends BaseApplet2 implements EnergyListener {
    public void init() {
        super.init();

        double intWidth = 20;


        functions = createFunctions();
        String[] names = createFunctionNames();
        chooser = new Choice();
        for( int i = 0; i < names.length; i++ ) {
            chooser.addItem( names[i] );
        }

        eqn = new ImprovedSchrodinger( function );

        plotter = new Plotter2( eqn );
        plotter.setRangeX( -intWidth / 2, intWidth / 2 );
        plotter.noUnitsY = true;

        potPlotter = new Plotter2( new SolvFunction( function ) );
        plotter.bottomMargin = potPlotter.bottomMargin = 0;
        potPlotter.setAxes( false );
        plotter.setLabels( "x", "wave fn\n \n(arb.\nunits)" );
        potPlotter.setLabels( "x", "potential" );

        setLayout( new GridLayout( 5, 4 ) );

        depthField = addField( "Depth of well", "" );
        energyField = addField( "Energy", Schrodinger.DEFAULT_ENERGY );
        widthField = addField( "Width of well", "" );
        intWidthField = addField( "Integration width", intWidth );
        nWellsField = addField( "Number of wells", "" );
        stepsField = addField( "Steps", steps );
        periodField = addField( "Period", "" );

        add( statusLabel );

        solveButton = addButton( "Solve" );
        squareValues = addCheckbox( "psi squared", true );
        add( chooser );
        chooser.addItemListener( this );
        add( new Label() );
        drawButton = addButton( "Draw new function" );

        setFunction( functions[0] );

        //refreshButton = addButton("Refresh");

        wellResizeListener = new WellResizeListener();
        energyChangeListener = new EnergyChangeListener();
        freehandListener = new GraphClickListener() {
            public void update( double x, double y, boolean mouseDown, boolean mouseUp ) {
                if( function instanceof FreehandFunction ) {
                    if( !freehandEnabled ) {
                        return;
                    }

                    if( mouseDown ) {
                        setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
                    }

                    ( (FreehandFunction)function ).update( x, y, mouseDown );
                    ( (SolnCache)potPlotter.eqn ).invalidate();

                    if( mouseUp ) {
                        setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                        potentialChanged();
                        freehandEnabled = false;
                    }


                    refresh();
                }
            }

            public void update( double x, double y ) {

            }
        };
        freehandListener.p = potPlotter;

        potPlotter.setViewAndRange( -5, 5 );
    }

    public Function[] createFunctions() {
        return new Function[]{
                new MultipleWells( 1 ),
                new Quadratic( 0.25 ),
                new ParsedFunction( "0-5*exp(0-x*x*sin(x)*sin(x))" ),
                new FreehandFunction( -5, 5, 100 ),
        };
    }

    public String[] createFunctionNames() {
        return new String[]{
                "Square wells",
                "Quadratic",
                "Custom expression",
                "Freehand function",
        };
    }

    protected void potentialChanged() {
        ( (SolnCache)potPlotter.eqn ).invalidate();
        if( eqn instanceof ImprovedSchrodinger ) {
            ( (ImprovedSchrodinger)eqn ).cache.invalidate();
        }
    }

    public void setFunction( Function f ) {
        this.function = f;

        freehandEnabled = f instanceof FreehandFunction;
        drawButton.setVisible( f instanceof FreehandFunction );

        eqn.setPotentialFunction( function );
        potPlotter.eqn = new SolnCache( new SolvFunction( function ) );
        potentialChanged();
        refresh();

        if( f instanceof SquareWell ) {
            SquareWell sw = (SquareWell)f;
            depthField.setText( "" + -sw.getDepth() );
            widthField.setText( "" + sw.getWidth() );
            depthField.setEnabled( true );
            widthField.setEnabled( true );
        }
        else {
            widthField.setEnabled( false );
            depthField.setEnabled( false );
        }

        if( f instanceof MultipleWells ) {
            MultipleWells mw = (MultipleWells)f;
            nWellsField.setText( "" + mw.getWellCount() );
            periodField.setText( "" + mw.getPeriod() );
            nWellsField.setEnabled( true );
            periodField.setEnabled( true );
        }
        else {
            nWellsField.setEnabled( false );
            periodField.setEnabled( false );
        }

        if( f instanceof ParsedFunction ) {
            ParsedFunction pf = (ParsedFunction)f;
            depthField.setText( "" + f );
            depthField.setEnabled( true );
        }
    }


    /**
     * Reads in (and validates) input from the text fields.
     *
     * @throws IllegalArgumentException    if any input fields are invalid
     */
    public void updateInput() {
        abortSolving();


        double energy, depth, width = 0, intWidth, period;
        int nWells, steps;

        energy = getDouble( energyField );
        intWidth = getDouble( intWidthField );
        steps = getInt( stepsField );


        if( intWidth <= 0 ) {
            throw new IllegalArgumentException( "range must be positive" );
        }

        if( intWidth < plotter.viewRange ) {
            throw new IllegalArgumentException( "int. range smaller than view" );
        }

        if( steps <= 0 ) {
            throw new IllegalArgumentException( "need steps > 0" );
        }

        if( safetyLimit && steps > 50000 ) {
            throw new IllegalArgumentException( "steps should be < 50000" );
        }

        if( safetyLimit && steps < 100 ) {
            throw new IllegalArgumentException( "steps should be >= 100" );
        }

        if( safetyLimit && intWidth > 100 ) {
            throw new IllegalArgumentException( "int. width should be <= 100" );
        }

        if( safetyLimit && energy > 100 ) {
            throw new IllegalArgumentException( "energy should be <= 100" );
        }

        if( safetyLimit && energy < -100 ) {
            throw new IllegalArgumentException( "energy should be >= -100" );
        }


        if( function instanceof SquareWell ) {
            SquareWell well = (SquareWell)function;

            depth = -getDouble( depthField );
            width = getDouble( widthField );

            if( depth == 0 ) {
                throw new IllegalArgumentException( "well depth should be " + ( safetyLimit ? "> 0" : "nonzero" ) );
            }

            if( width <= 0 ) {
                throw new IllegalArgumentException( "well width must be > 0" );
            }

            //	We reverse the sign!
            if( safetyLimit && depth > 0 ) {
                throw new IllegalArgumentException( "well depth should be > 0" );
            }

            if( safetyLimit && depth < -100 ) {
                throw new IllegalArgumentException( "well depth should be <= 100" );
            }


            if( plotter.viewRange <= width ) {
                throw new IllegalArgumentException( "well wider than range" );
            }

            well.depth = depth;
            well.width = width;
        }

        if( function instanceof MultipleWells ) {
            MultipleWells mw = (MultipleWells)function;

            nWells = getInt( nWellsField );
            period = getDouble( periodField );

            if( nWells > 1 && width > mw.getPeriod() ) {
                width = 0.5 * mw.getPeriod();
                widthField.setText( "" + width );
            }

            if( function instanceof MultipleWells && nWells > 1 ) {
                if( width > period ) {
                    throw new IllegalArgumentException( "well width must be < period" );
                }
            }

            if( nWells < 0 ) {
                throw new IllegalArgumentException( "need wells >= 0" );
            }

            if( nWells > MAX_WELLS ) {
                throw new IllegalArgumentException( "need wells <= " + MAX_WELLS );
            }

            if( safetyLimit && period < 0.1 ) {
                throw new IllegalArgumentException( "period should be >= 0.1" );
            }

            if( safetyLimit && period > 10 ) {
                throw new IllegalArgumentException( "period should be <= 10" );
            }


            mw.setWellCount( nWells );
            mw.setPeriod( period );
            mw.reCentre();
            //	Read adjusted width back in
            double w = mw.getWidth();
            if( w != width ) {
                widthField.setText( "" + w );
            }
        }

        if( function instanceof ParsedFunction ) {
            String s = depthField.getText();
            ( (ParsedFunction)function ).setString( s );
        }


        eqn.setEnergy( energy );
        this.steps = steps;
        plotter.minX = -( plotter.maxX = intWidth / 2 );
        plotter.rangeX = intWidth;

        potentialChanged();
    }


    public void start() {
        super.start();

        addMouseListener( wellResizeListener );
        addMouseMotionListener( wellResizeListener );
        addMouseListener( energyChangeListener );
        addMouseMotionListener( energyChangeListener );
        addMouseListener( freehandListener );
        addMouseMotionListener( freehandListener );
    }

    public void stop() {
        super.stop();

        removeMouseListener( wellResizeListener );
        removeMouseMotionListener( wellResizeListener );
        removeMouseListener( energyChangeListener );
        removeMouseMotionListener( energyChangeListener );
        removeMouseListener( freehandListener );
        removeMouseMotionListener( freehandListener );
    }

    public void itemStateChanged( ItemEvent ie ) {
        if( ie.getSource() == chooser ) {
            selectedY1 = selectedY2;
        }
        super.itemStateChanged( ie );
    }

    public void actionPerformed( ActionEvent ae ) {
        if( ae == null ) {
            setFunction( functions[chooser.getSelectedIndex()] );
            return;
        }


        if( ae.getSource() == solveButton ) {
            solve();
            return;
        }

        if( ae.getSource() == drawButton ) {
            freehandEnabled = true;
        }

        safeUpdateInput();
    }


    /**
     * Attempts to update the parameters from the text fields.  If this
     * fails, an error message is displayed in the statusLabel.
     */
    protected void safeUpdateInput() {
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

    public void doPaint( Graphics g ) {
        //hSpace = g.getFontMetrics().stringWidth("-8.888");
        //vSpace = g.getFontMetrics().getHeight();
        FontMetrics fm = g.getFontMetrics();
        hSpace1 = potPlotter.getSpaceX( fm );
        hSpace2 = plotter.getSpaceX( fm );
        vSpace1 = potPlotter.getSpaceY( fm );
        vSpace2 = plotter.getSpaceY( fm );


        int w1 = getSize().width / 2 - hSpace1;
        int w2 = getSize().width / 2 - hSpace2;
        int h1 = getGraphHeight() - vSpace1;
        int h2 = getGraphHeight() - vSpace2;

        //int steps = w*10;
        //System.err.println("steps = "+steps);

        int left = 0;//(int)(steps*(function.getLeft()-minX)/rangeX);
        int right;
        if( isSquare() ) {
            SquareWell sw = (SquareWell)function;
            right = (int)( steps * ( sw.getRight() - plotter.minX ) / plotter.rangeX );
        }
        else {
            right = (int)( steps * ( 0.75 * plotter.viewRight + 0.25 * plotter.viewLeft - plotter.minX ) / plotter.rangeX );
        }

        plotter.updateSolution( steps );
        if( ( squareValues == null ) || squareValues.getState() ) {
            plotter.squareValues();
        }

        plotter.scaleToFit( left, right );

        //potPlotter.setViewAndRange(plotter.viewLeft, plotter.viewRight);
        potPlotter.setViewAndRange( -5, 5 );
        potPlotter.updateSolution( steps );
        if( function instanceof FreehandFunction ) {
            potPlotter.setRangeY( -5, 0 );
        }
        else {
            potPlotter.scaleToFit();
        }

        //	Draw selected region
        g.setColor( new Color( 0xffe0e0 ) );
        int y1 = potPlotter.toScreenY( selectedY1, h1 );
        int y2 = potPlotter.toScreenY( selectedY2, h1 );
        int d = y2 - y1;

        if( d < 0 ) {
            d *= -1;
            y1 = y2;
        }

        g.translate( hSpace1, 0 );
        if( d != 0 ) {
            g.fillRect( 0, y1, w1, d );
        }
        g.translate( -hSpace1, 0 );

        //	Potential graph
        g.setColor( POT_COL );
        potPlotter.setRect( 0, 0, getSize().width / 2, getGraphHeight() );
        //potPlotter.paint(g,getSize().width/2,getGraphHeight());
        potPlotter.paint( g );


        g.setColor( ENERGY_COL );
        int energyH = potPlotter.toScreenY( eqn.energy, h1 );
        g.drawLine( hSpace1, energyH, hSpace1 + w1, energyH );


        g.translate( w1 + hSpace1 + hSpace2, 0 );

        //	Mark edges of well
        if( isSquare() && function instanceof SquareWell ) {
            g.setColor( WELL_BOUNDS_COL );
            plotter.paintVerticalMarkers( g, w2, h2, ( (SquareWell)function ).getMarkers( plotter.minX, plotter.minY ) );
        }

        // Plot wave function
        g.setColor( WAVE_FN_COL );


        g.translate( -hSpace2, 0 );
        plotter.paint( g, getSize().width / 2, getGraphHeight() );
    }


    /**
     * Attempts to find an energy for which the Schrodinger equation has a
     * solution.  The region searched will be the selected region; if no
     * region is selected, the whole interval will be searched.
     * <p/>
     * If a search is already in progress, this method will stop the search
     * and perform no other action.
     * <p/>
     * This method will also alternate the label of the <code>Solve</code>
     * button between &quot;Solve&quot; and &quot;Cancel&quot; as
     * appropriate.
     */
    public synchronized void solve() {
        if( solveThread != null && !solveThread.isFinished() ) {
            solveThread.kill();
            //unlockControls();
            setPromptEnabled( true );
            return;
        }

        solveButton.setLabel( "Cancel" );
        statusLabel.setText( "Solving..." );

        safeUpdateInput();

        double min, max;

        if( selectedY1 == selectedY2 ) {
            if( isSquare() ) {
                min = ( (SquareWell)function ).getDepth();
                max = 0;
            }
            else {
                min = potPlotter.minY;
                max = potPlotter.maxY;
            }
        }
        else {
            min = Math.min( selectedY1, selectedY2 );
            max = Math.max( selectedY1, selectedY2 );
        }

        double intRange = plotter.rangeX;


        solveThread = new SolnSearcher( this, eqn, min, max, intRange, steps );
        setPromptEnabled( false );
        //lockControls();
        solveThread.start();
    }


    /**
     * Stops a running search.  If no search is in progress, no action will
     * be taken.
     */
    public synchronized void abortSolving() {
        if( solveThread != null && !solveThread.isFinished() ) {
            solveThread.kill();
            setPromptEnabled( true );
        }
    }

    public void energyChanged( double newEnergy ) {
        energyField.setText( "" + newEnergy );
        refresh();
    }

    public void searchFinished() {
        solveButton.setLabel( "Solve" );
        statusLabel.setText( "" );
        //unlockControls();
        setPromptEnabled( true );
        solveThread = null;
    }


    /**
     * Returns <code>true</code> if the potential model consists of square
     * wells.
     */
    public boolean isSquare() {
        return function instanceof SquareWell;
    }


    protected int hSpace1, hSpace2, vSpace1, vSpace2;

    /**
     * The number of <i>x</i> values to sample
     */
    protected int steps = DEFAULT_STEPS;

    protected TextField energyField, depthField, widthField, intWidthField, nWellsField, stepsField, periodField;
    protected Checkbox squareValues, quadBox;
    protected Button refreshButton, solveButton, drawButton;
    protected Choice chooser;

    /**
     * Listener for changes to the position of the &quot;well width&quot;
     * slider.
     */
    protected WellResizeListener wellResizeListener;

    /**
     * Listener for changes to the position of the energy slider.
     */
    protected EnergyChangeListener energyChangeListener;

    protected GraphClickListener freehandListener;

    /**
     * Instance of Schrodinger to use to generate solution data
     */
    protected Schrodinger eqn;

    /**
     * Thread currently searching for a solution, or <code>null</code> if
     * there is no search in progress.
     */
    protected SolnSearcher solveThread;

    /**
     * The first limit for the search interval.
     */
    protected double selectedY1;

    /**
     * The second limit for the search interval.
     */
    protected double selectedY2;

    protected boolean freehandEnabled;

    protected Plotter2 plotter, potPlotter;

    /**
     * The (piecewise-constant) potential function
     */
    protected Function function;

    /**
     * The (quadratic) potential function
     */
    protected Function[] functions;

    protected static Color
            POT_COL = Color.blue,
            ENERGY_COL = Color.red,
            AXES_COL = Color.blue,
            WAVE_FN_COL = Color.black,
            WELL_BOUNDS_COL = Color.red;

    protected static int
            DEFAULT_STEPS = 4000,
            DEFAULT_NWELLS = 1,
            MAX_WELLS = 5;


    /**
     * Listener for changes to the position of the &quot;well width&quot;
     * slider.
     */
    protected class WellResizeListener extends MouseAdapter implements MouseMotionListener {
        public void mousePressed( MouseEvent me ) {
            update( me );
        }

        public void mouseReleased( MouseEvent me ) {
            update( me );
        }

        public void mouseDragged( MouseEvent me ) {
            update( me );
        }

        public void mouseMoved( MouseEvent me ) {
        }

        public void update( MouseEvent me ) {
            abortSolving();
            // Return if not in this region
            if( me.getX() < getSize().width / 2 ) {
                return;
            }


            if( !isSquare() ) {
                return;
            }

            if( function instanceof MultipleWells ) {
                if( ( (MultipleWells)function ).nWells != 1 ) {
                    return;
                }
            }

            SquareWell sw = (SquareWell)function;

            double x = plotter.viewRange * ( me.getX() - hSpace2 - getSize().width / 2 ) / ( getSize().width / 2 - hSpace2 ) + plotter.viewLeft;
            double width = 2 * Math.abs( x - sw.getShift() );

            if( x <= plotter.viewLeft || x >= plotter.viewRight ) {
                return;
            }

            if( width != 0 ) {
                sw.setWidth( width );

                boolean b = enablePrompt;
                synchronized( this ) {
                    setPromptEnabled( false );
                    widthField.setText( "" + width );
                    setPromptEnabled( b );
                }
            }

            potentialChanged();
            refresh();
        }
    }


    /**    Listener for changes to the position of the energy slider.
     */
    protected class EnergyChangeListener extends MouseAdapter implements MouseMotionListener {
        public void mousePressed( MouseEvent me ) {
            update( me );
        }

        public void mouseReleased( MouseEvent me ) {
            update( me );
        }

        public void mouseDragged( MouseEvent me ) {
            update( me );
        }

        public void mouseMoved( MouseEvent me ) {
        }

        public void update( MouseEvent me ) {
            abortSolving();

            //	Test to see if we're drawing a function
            if( function instanceof FreehandFunction ) {
                if( freehandEnabled ) {
                    return;
                }

                /*
                    if(((me.getModifiers()&me.BUTTON1_MASK)!=0)
                    // IRIX workaround
                    || 	((me.getModifiers()&(me.BUTTON2_MASK | me.BUTTON3_MASK))==0))
                        return;*/
            }

            // Return if not in this region
            if( me.getX() > getSize().width / 2 ) {
                return;
            }

            int h = getGraphHeight() - vSpace1;

            double y = potPlotter.rangeY * ( h - me.getY() ) / h + potPlotter.minY;

            //System.err.println(y);

            if( y <= potPlotter.minY || y >= potPlotter.maxY ) {
                return;
            }


            if( ( me.getModifiers() & ( me.BUTTON2_MASK | me.BUTTON3_MASK ) ) != 0 ) {
                if( me.getID() == me.MOUSE_PRESSED ) {
                    selectedY2 = selectedY1 = y;
                }
                else {
                    selectedY2 = y;
                }
            }


            eqn.setEnergy( y );
            boolean b = enablePrompt;
            synchronized( this ) {
                setPromptEnabled( false );
                energyField.setText( "" + y );
                setPromptEnabled( b );
            }
			//updateInput();
			refresh();
		}
	}
}
