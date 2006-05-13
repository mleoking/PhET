import java.awt.*;


/**
 * Responsible for plotting graphs.  As many applets require special
 * functionality, this class has grown a little too feature-rich.
 * The first step to improve this class would be to take out some of the
 * specialised behaviour (such as folding the x-axis) into several subclasses
 * (the strategy employed with {@link RotatedPlotter RotatedPlotter}).
 * <p/>
 * {@link Plotter2 Plotter2} went some way towards improving this class,
 * providing extra methods to simplify common tasks; the subclassing
 * was mostly to break up the code, rather than to separate the
 * functionality.
 */
class Plotter {
    //	Used this test harness for checking behaviour with strange data
    /*public static void main(String[] args)
     {
         double[] vals = {
             +0.0,
             -0.0,
             Double.MAX_VALUE,
             -Double.MAX_VALUE,
             Double.MIN_VALUE,
             -Double.MIN_VALUE,
             Double.POSITIVE_INFINITY,
             Double.NEGATIVE_INFINITY,
             Double.NaN,
         };

         for(int i=0; i<vals.length; i++)
             System.err.println("Math.log("+vals[i]+") = "+Math.log(vals[i]));

         for(int i=0; i<vals.length; i++)
             System.err.println("(int)("+vals[i]+") = "+(int)(vals[i]));


     } */


    /**
     * Creates a new plotter to graph the specifed Solvable object.
     */
    public Plotter( Solvable s ) {
        this.eqn = s;
    }


    /**
     * Plots vertical markers at the specified coordinates.  Each marker
     * will be a vertical line running across the whole <i>y</i>-range.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     * @param    markers    An array of <i>x</i> coordinates at which markers
     */
    public void paintVerticalMarkers( Graphics g, int w, int h, double[] markers ) {
        for( int i = 0; i < markers.length; i++ ) {
            int x = toScreenX( markers[i], w );
            if( x >= 0 && x <= w ) {
                g.drawLine( x, 0, x, h );
            }
        }
    }


    /**
     * Paints a set of axes (the lines x=0, y=0).
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     */
    public void paintAxes( Graphics g, int w, int h ) {
        double xscl = w / rangeX;
        double yscl = h / rangeY;

        if( Double.isNaN( xscl ) || Double.isNaN( yscl ) ) {
            return;
        }

        int ox = toScreenX( 0, w );
        int oy = toScreenY( 0, h );

        if( oy >= 0 && oy <= h ) {
            g.drawLine( 0, oy, w, oy );
        }
        if( ox >= 0 && ox <= w ) {
            g.drawLine( ox, 0, ox, h );
        }
    }


    /**
     * Labels the <i>x</i> axis.  Major and minor ticks will be drawn,
     * with a numerical label at each major tick.  The label for this axis
     * will also be painted.  Note that &quot;axis&quot; here refers to the
     * scale across the bottom of the graph, rather than the line
     * <i>y</i> = 0.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     */
    public void labelAxisX( Graphics g, int w, int h ) {
        FontMetrics fm = g.getFontMetrics();

        int ox = 0; //toScreenX(0,w);
        int oy = h; //toScreenY(0,h);

        double minorSpacing = chooseTickSize( viewRange );
        if( minorSpacing == 0 ) {
            minorSpacing = viewRange / 7;
        }

        double x = snap( fromScreenX( 0, w ), minorSpacing );
        int i = (int)Math.rint( x / minorSpacing );

        for( ; toScreenX( x, w ) < w; x += minorSpacing, i++ ) {
            x = snap( x, minorSpacing );
            int tx = toScreenX( x, w );
            int tickSize = ( i % tickRatio == 0 ) ? majorTickSize : minorTickSize;
            if( !noUnitsX ) {
                g.drawLine( tx, oy, tx, oy - tickSize );
            }
            if( !noUnitsX && ( i % tickRatio == 0 ) ) {
                paintLabelBelow( g, toString( x ), tx, oy );
            }
        }

        if( !noUnitsX ) {
            oy += g.getFontMetrics().getHeight();
        }

        if( xLabel != null )
        //paintLabelBelow(g,xLabel,w/2,oy+g.getFontMetrics().getHeight());
        {
            xLabel.paintBelow( g, w / 2, oy );
        }
    }


    /**
     * Labels the <i>y</i> axis.  Major and minor ticks will be drawn,
     * with a numerical label at each major tick.  The label for this axis
     * will also be painted.  Note that &quot;axis&quot; here refers to the
     * scale to the left of the graph, rather than the line <i>x</i> = 0.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     */
    public void labelAxisY( Graphics g, int w, int h ) {
        FontMetrics fm = g.getFontMetrics();

        int ox = 0; //toScreenX(0,w);
        int oy = h; //toScreenY(0,h);

        g.drawLine( ox, oy, w, oy );
        g.drawLine( ox, oy, ox, 0 );

        if( infY ) {
            paintLabelToLeft( g, "-Infinity", ox, toScreenY( minY + 0.1 * rangeY, h ) );
            ox -= fm.stringWidth( "-8.888" );
            if( yLabel != null ) {
                yLabel.paintToLeft( g, ox, h / 2 );
            }
            return;
        }

        double minorSpacing = chooseTickSize( rangeY );
        if( minorSpacing == 0 || noUnitsY ) {
            //	Just put in one mark at the top
            double originalRange = rangeY / ( 1 + bottomMargin + topMargin );
            minorSpacing = originalRange * ( 1 + bottomMargin ) / tickRatio;
            //minorSpacing = rangeY/7;
        }

        double y = snap( fromScreenY( 0, h - getSpaceY( fm ) ), minorSpacing );
        int i = (int)Math.rint( y / minorSpacing );

        for( ; toScreenY( y, h ) > 0; y += minorSpacing, i++ ) {
            y = snap( y, minorSpacing );
            int ty = toScreenY( y, h );
            int tickSize = ( ( i % tickRatio ) == 0 ) ? majorTickSize : minorTickSize;
            if( !noUnitsY ) {
                g.drawLine( ox, ty, ox + tickSize, ty );
            }
            if( !noUnitsY && ( i % tickRatio == 0 ) ) {
                paintLabelToLeft( g, toString( y ), ox, ty );
            }
        }

        if( !noUnitsY ) {
            ox -= g.getFontMetrics().stringWidth( "-8.888" );
        }

        if( yLabel != null )
        //paintLabelToLeft(g,yLabel,ox-g.getFontMetrics().stringWidth("-8.888"),h/2);
        {
            yLabel.paintToLeft( g, ox, h / 2 );
        }
    }


    /**
     * Paints a string just below the specified point, centred horizontally.
     */
    protected void paintLabelBelow( Graphics g, String s, int x, int y ) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString( s, x - fm.stringWidth( s ) / 2, y + fm.getHeight() );
    }


    /**
     * Paints a string just to the left of specified point, centred vertically.
     * (The right-hand end of the text will appear one pixel to the left of
     * the specified point.)
     */
    protected void paintLabelToLeft( Graphics g, String s, int x, int y ) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString( s, x - fm.stringWidth( s ), y + fm.getAscent() / 2 );
    }


    /**
     * Convenience method to format a <code>double</code> for display.
     * Implementation is via a call to {@link BaseApplet2#toString(double)
     * BaseApplet2.toString()}.
     */
    protected String toString( double d ) {
        return BaseApplet2.toString( d );
        //String s = ""+d;
        //return s;
    }


    /**
     * Paints the graph, axes and scales.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     */
    public void paint( Graphics g, int w, int h ) {
        if( viewRange == 0 || rangeX == 0 || rangeY == 0 ) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        int hSpace = getSpaceX( fm );
        int vSpace = getSpaceY( fm );

        int _w = w - hSpace;
        int _h = h - vSpace;

        g.translate( +hSpace, 0 );

        Color graphCol = g.getColor();

        g.setColor( Color.blue );
        if( axes ) {
            paintAxes( g, _w, _h );
        }

        g.setColor( graphCol );
        if( fold ) {
            paintGraphFolded( g, _w, _h, foldSize, foldSym );
        }
        else {
            paintGraph( g, _w, _h );
        }

        g.setColor( Color.black );
        if( xScale ) {
            labelAxisX( g, _w, _h );
        }
        if( yScale ) {
            labelAxisY( g, _w, _h );
        }

        g.translate( -hSpace, 0 );
    }


    /**
     * Calculates the size of the gap between the left edge of the display
     * and the start of the actual graph area.  In other words, the space
     * allocated for labels and scales on the vertical axis.
     *
     * @param    fm    The FontMetrics with which the graph will be rendered
     */
    public int getSpaceX( FontMetrics fm ) {
        return 0
               + ( ( yScale && ( !noUnitsY || infY ) ) ? fm.stringWidth( "-8.888" ) : 0 )
               + ( ( yLabel != null ) ? yLabel.getWidth( fm ) : 0 )
                ;
    }


    /**
     * Calculates the size of the gap between the bottom edge of the display
     * and the start of the actual graph area.  In other words, the space
     * allocated for labels and scales on the horizontal axis.
     *
     * @param    fm    The FontMetrics with which the graph will be rendered
     */
    public int getSpaceY( FontMetrics fm ) {
        return ( ( ( xScale && !noUnitsX ) ? 1 : 0 ) + ( ( xLabel != null ) ? xLabel.countLines() : 0 ) ) * fm.getHeight();
    }

    /*
         Scale values to fit into graph area;
         Plotted region extends from (viewLeft, minY) to (viewRight, maxY)
         This should now all be independent of integration range [minX,maxX]
     */

    /**
     * Paints the graph (without scales, labels or axes).
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     */
    public void paintGraph( Graphics g, int w, int h ) {
        double xscl = w / viewRange;
        double yscl = h / rangeY;
        double stepSize = (double)rangeX / ( soln.length - 1 );
        double x = viewLeft;
        int steps = soln.length - 1;
        int interpolation = this.interpolate ? 1 : 0;

        int left = (int)( steps * ( viewLeft - minX ) / rangeX );
        int right = (int)( steps * ( viewRight - minX ) / rangeX );

        for( int i = left; i < right; i++ ) {
            //double y1 = soln[i], y2 = soln[i+interpolation];
            if( i < 0 || i >= steps || Double.isNaN( soln[i] ) || Double.isNaN( soln[i + interpolation] ) || soln[i] > maxY || soln[i] < minY )// || y1<minY || y2>maxY || Double.isInfinite(y1) || Double.isInfinite(y2) || Double.isNaN(y1) || Double.isNaN(y2))
            {
                x += stepSize;
                continue;
            }

            double newX = x + stepSize;
            g.drawLine(
                    (int)( xscl * ( x - viewLeft ) ),
                    h - (int)( yscl * ( soln[i] - minY ) ),
                    (int)( xscl * ( newX - viewLeft ) ),
                    h - (int)( yscl * ( soln[i + interpolation] - minY ) )
            );
            x = newX;
        }
    }


    /**
     * Paints the graph (without scales, labels or axes), with a folded
     * <i>x</i> axis.  The folding will be on the interval [0,fold] or
     * [-fold,fold], depending on the <code>sym</code> parameter.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the available display area
     * @param    h    The height (in pixels) of the available display area
     * @param    fold    The width (in graph units) of the interval over which
     * to fold.
     * @param    sym        <code>true</code> if the graph should be folded onto
     * an interval centred at the origin, else
     * <code>false</code>
     */
    public void paintGraphFolded( Graphics g, int w, int h, double fold, boolean sym ) {
        double xscl = w / viewRange;
        double yscl = h / rangeY;
        double stepSize = (double)rangeX / ( soln.length - 1 );
        double x = minX;
        int steps = soln.length - 1;
        int interpolation = this.interpolate ? 1 : 0;

        for( int i = 0; i < steps; i++ ) {
            double newX = x + stepSize;
            if( !interpolate ) {
                x = newX;
            }
            g.drawLine(
                    (int)( xscl * ( fold( x, fold, sym ) - viewLeft ) ),
                    h - (int)( yscl * ( soln[i] - minY ) ),
                    (int)( xscl * ( fold( newX, fold, sym ) - viewLeft ) ),
                    h - (int)( yscl * ( soln[i + interpolation] - minY ) )
            );
            x = newX;
        }
    }


    /**
     * Returns the folded version of an <i>x</i> coordinate.
     *
     * @param    x    The value to fold
     * @param    f    The width (in graph units) of the interval over which
     * to fold.
     * @param    sym    <code>true</code> if the graph should be folded onto an
     * interval centred at the origin, else <code>false</code>
     */
    protected double fold( double x, double f, boolean sym ) {
        if( sym ) {
            //f /= 2;
            while( x > f || x < -f ) {
                x = ( x > f ) ? ( 2 * f - x ) : ( -2 * f - x );
            }
            return x;
        }
        else {
            if( (int)( x / f ) % 2 == 0 ) {
                return x % f;
            }
            else {
                return f - x % f;
            }
        }
    }


    /**
     * Updates this plotter's cache of solution data.
     *
     * @param    steps    The number of <i>x</i> values at which the solution
     * should be sampled
     */
    protected void updateSolution( int steps ) {
        //	Choose step size, prepare array
        soln = new double[steps];
        double step = rangeX / steps;

        //	Get function
        eqn.solve( minX, step, soln );

        if( log ) {
            for( int i = 0; i < steps; i++ ) {
                //if(Double.isNaN(Math.log(soln[i])) || Double.isInfinite(soln[i]))

                soln[i] = Math.log( soln[i] );
                //System.err.println(soln[i]);
            }
        }
    }


    /**
     * Adjusts the <code>minY</code>, <code>maxY</code> and
     * <code>rangeY</code> parameters so that the <i>y</i> axis range
     * is set to the minimum interval into which all graph data falls.
     */
    protected void scaleToFit() {
        int left = (int)( ( viewLeft - minX ) * soln.length / rangeX );
        int right = (int)( ( viewRight - minX ) * soln.length / rangeX );
        scaleToFit( left, right );
    }


    /**
     * Adjusts the <code>minY</code>, <code>maxY</code> and
     * <code>rangeY</code> parameters so that the <i>y</i> axis range
     * is set to the minimum interval into which all graph data <i>within
     * the specified interval</i> falls.
     *
     * @param    left    The minimum bound for the <i>x</i>-axis interval
     * @param    right    The maximum bound for the <i>x</i>-axis interval
     */
    protected void scaleToFit( int left, int right ) {
        if( left < 0 ) {
            left = 0;
        }
        if( right >= soln.length ) {
            right = soln.length - 1;
        }
        //System.err.println(soln.length);

        //	Decide on region to search
        minY = getMin( soln, left, right );
        maxY = getMax( soln, left, right );
        rangeY = maxY - minY;

        //infY = maxY==ABS_MAX;

        //	Adjust limits to leave a small margin
        maxY += topMargin * rangeY;
        minY -= bottomMargin * rangeY;
        rangeY = maxY - minY;
    }


    /**
     * Converts the specified <i>x</i> value from graph space to screen space
     *
     * @param    x    The value
     * @param    w    The width (in pixels) of the available display area
     */
    public int toScreenX( double x, int w ) {
        double xscl = w / viewRange;
        return (int)( xscl * ( x - viewLeft ) );
    }


    /**
     * Converts the specified <i>y</i> value from graph space to screen space
     *
     * @param    y    The value
     * @param    h    The height (in pixels) of the available display area
     */
    public int toScreenY( double y, int h ) {
        double yscl = h / rangeY;
        return h - (int)( yscl * ( y - minY ) );
    }


    /**
     * Converts the specified <i>x</i> value from screen space to graph space
     *
     * @param    x    The value
     * @param    w    The width (in pixels) of the available display area
     */
    public double fromScreenX( int x, int w ) {
        double xscl = w / viewRange;
        return x / xscl + viewLeft;
    }


    /**
     * Converts the specified <i>y</i> value from screen space to graph space
     *
     * @param    y    The value
     * @param    h    The height (in pixels) of the available display area
     */
    public double fromScreenY( int y, int h ) {
        double yscl = h / rangeY;
        return y / yscl + minY;
    }


    /**
     * Helper method to round off <code>d</code> to the nearest multiple of
     * <code>base</code>
     */
    protected double snap( double d, double base ) {
        return base * Math.rint( d / base );
    }


    /**
     * Sets the range for the <i>x</i> axis.  This is not necessarily the
     * range of values displayed on the screen (see
     * {@link #setView(double,double) setView()}), but does determine the
     * range of values for which the solution is evaluated.  For graphs
     * where the values are not independed (eg. numerical integration), this
     * is often set larger than than the view range.
     */
    public void setRangeX( double min, double max ) {
        minX = min;
        maxX = max;
        rangeX = max - min;
    }


    /**
     * Sets the portion of the <i>x</i> axis displayed on the graph.  This
     * will normally be contained within the <i>x</i> axis bounds (see
     * {@link #setRangeX(double,double) setRange()}), but may overlap or be
     * entirely outside that interval (although it is difficult to think of
     * a useful application of the latter).
     */
    public void setView( double min, double max ) {
        viewLeft = min;
        viewRight = max;
        viewRange = max - min;
    }


    /**
     * Combines a call to {@link #setView(double,double) setView()} and
     * {@link #setRangeX(double,double) setRangeX()}, passing the same
     * interval bounds to each.
     */
    public void setViewAndRange( double min, double max ) {
        minX = viewLeft = min;
        maxX = viewRight = max;
        rangeX = viewRange = max - min;
    }


    /**
     * Sets the portion of the <i>x</i> axis displayed on the graph.
     */
    public void setRangeY( double min, double max ) {
        minY = min;
        maxY = max;
        rangeY = max - min;
    }


    /**
     * Sets the view range, <i>x</i> range and <i>y</i> range of this plotter
     * according to the settings of another.
     *
     * @param    p    The plotter, whose example this shall follow
     */
    public void copyScale( Plotter p ) {
        setView( p.viewLeft, p.viewRight );
        setRangeX( p.minX, p.maxX );
        setRangeY( p.minY, p.maxY );
        infY = p.infY;
    }


    /**
     * Convenience method to square all the <i>y</i> values.
     */
    protected void squareValues() {
        for( int i = 0; i < soln.length; i++ ) {
            soln[i] *= soln[i];
        }
    }


    /**
     * Returns <code>true</code> if <i>y</i> values between samples will be
     * interpolated, else <code>false</code>
     */
    public boolean isInterpolated() {
        return interpolate;
    }

    /**
     * Set to <code>true</code> if <i>y</i> values between samples should be
     * interpolated, else <code>false</code>
     */
    public void setInterpolated( boolean b ) {
        interpolate = b;
    }


    /**
     * Returns the maximum value from an array
     *
     * @param    vals    The data
     * @param    left    The index at which to begin searching
     * @param    right    The index at which to stop searching
     */
    protected static double getMax( double[] vals, int left, int right ) {
        int max = left;
        for( ; left < right; left++ ) {
            if( !Double.isNaN( vals[left] ) && ( Double.isNaN( vals[max] ) || vals[max] < vals[left] ) ) {
                max = left;
            }
        }

        if( Double.isInfinite( vals[max] ) || Double.isNaN( vals[max] ) ) {
            return ABS_MAX;
        }
        return vals[max];
    }


    /**
     * Returns the minimum value from an array
     *
     * @param    vals    The data
     * @param    left    The index at which to begin searching
     * @param    right    The index at which to stop searching
     */
    protected static double getMin( double[] vals, int left, int right ) {
        int min = left;
        for( ; left < right; left++ ) {
            if( !Double.isNaN( vals[left] ) && ( Double.isNaN( vals[min] ) || vals[min] > vals[left] ) ) {
                min = left;
            }
        }

        if( Double.isInfinite( vals[min] ) || Double.isNaN( vals[min] ) ) {
            return ABS_MIN;
        }
        return vals[min];
    }


    /**
     * Attempts to calculate an approprite tick size (distance between
     * minor ticks) based on the size of the axis.
     * <p/>
     * <em>Bug:</em> does not take size of display area into account
     *
     * @param    range    The size of the axis
     */
    //	A bit crude, but...
    protected double chooseTickSize( double range ) {
        double l = Math.log( range ) / Math.log( 10 );
        double size = 0.1;
        while( l >= 1 ) {
            size *= 10;
            l --;
        }

        while( l < 0 ) {
            size /= 10;
            l++;
        }

        if( l > 0.5 ) {
            size *= 4;
        }

        //	Bail out if marks cause labels to become too big
        if( 5 * size < 0.001 || 5 * size >= 1000 ) {
            return 0;
        }

        return size;
    }


    /**
     * Set to <code>true</code> if axes (<i>x</i> = 0, <i>y</i> = 0) should
     * be displayed, else <code>false</code>
     */
    protected void setAxes( boolean b ) {
        axes = b;
    }


    /**
     * Sets the labels for the axes.
     *
     * @param    x    The label for the <i>x</i> axis
     * @param    y    The label for the <i>y</i> axis
     */
    protected void setLabels( String x, String y ) {
        xLabel = ( x == null ) ? null : new GraphLabel( x );
        yLabel = ( y == null ) ? null : new GraphLabel( y );
    }


    /**
     * Enables or disables the x and y scales
     *
     * @param    x    <code>true</code> if the <i>x</i> axis is to have a scale
     * @param    y    <code>true</code> if the <i>y</i> axis is to have a scale
     */
    protected void setScalesEnabled( boolean x, boolean y ) {
        xScale = x;
        yScale = y;
    }


    /**
     * Used to enable or disable folding
     */
    protected void setFold( boolean b ) {
        fold = b;
    }


    /**
     * Used to enable or disable folding, and sets the folding parameters.
     *
     * @param    b    <code>true</code> if folding should be enabled, else
     * <code>false</code>
     * @param    size    The size of interval over which to fold
     * @param    sym    <code>true</code> if the interval extends in both
     * directions from the origin, else <code>false</code>
     */
    protected void setFold( boolean b, double size, boolean sym ) {
        this.foldSize = size;
        this.foldSym = sym;
        setFold( b );
    }


    /**
     * Enables folding, and sets the folding parameters.
     *
     * @param    size    The size of interval over which to fold
     * @param    sym    <code>true</code> if the interval extends in both
     * directions from the origin, else <code>false</code>
     */
    protected void setFold( double size, boolean sym ) {
        setFold( true, size, sym );
    }


    /**
     * Returns whether graph data should be log()'d before plotting
     */
    public boolean isLogarithmic() {
        return log;
    }

    /**
     * Sets whether graph data should be log()'d before plotting
     */
    public void setLogarithmic( boolean b ) {
        log = b;
    }


    /**
     * The length of the line drawn to indicate a minor tick
     */
    protected int minorTickSize = 5;

    /**
     * The length of the line drawn to indicate a major tick
     */
    protected int majorTickSize = 10;

    /**
     * The number of minor ticks per major tick
     */
    protected int tickRatio = 5;


    /**
     * <code>true</code> if the vertical scale is logarithmic, else
     * <code>false</code>
     */
    protected boolean log = false;

    /**
     * <code>true</code> if values will be interpolated, else
     * <code>false</code>
     */
    protected boolean interpolate = true;

    /**
     * <code>true</code> if axes will be drawn, else <code>false</code>
     */
    protected boolean axes = true;

    /**
     * <code>true</code> if the horizontal scale will be drawn, else
     * <code>false</code>
     */
    protected boolean xScale = true;

    /**
     * <code>true</code> if the vertical scale will be drawn, else
     * <code>false</code>
     */
    protected boolean yScale = true;

    /**
     * <code>true</code> if the <i>x</i> axis will be folded, else
     * <code>false</code>
     */
    protected boolean fold = false;

    /**
     * <code>true</code> if the folding interval is centred on the origin,
     * else <code>false</code>
     */
    protected boolean foldSym = false;

    /**
     * Set this to <code>true</code> to suppress numerical labels, but still
     * draw the main label for the axis.
     */
    public boolean noUnitsX = false;

    /**
     * Set this to <code>true</code> to suppress numerical labels, but still
     * draw the main label for the axis.
     */
    public boolean noUnitsY = false;

    /**
     * Set this to <code>true</code> if the <i>y</i> axis is to be infinite.
     * A messy hack; if a graph is to be scaled to fit, and the data is known
     * to become infinite, setting this flag will provide a meaningful label
     * (eg. &quot;Infinity&quot;) on the <i>y</i> axis.
     */
    public boolean infY = false;


    /**
     * The main label for the <i>x</i> axis
     */
    protected GraphLabel xLabel = null;

    /**
     * The main label for the <i>y</i> axis
     */
    protected GraphLabel yLabel = null;

    /**
     * The cache of graph data to be plotted
     */
    protected double[] soln;

    /**
     * The Solvable object providing solution data
     */
    protected Solvable eqn;


    /**
     * The size of the folding interval
     */
    protected double foldSize = 1;

    /**
     * When {@link #scaleToFit() scaling to fit}, the upper bound of the
     * <i>y</i> range will be extended by this proportion.
     */
    protected double topMargin = 0.05;

    /**
     * When {@link #scaleToFit() scaling to fit}, the upper bound of the
     * <i>y</i> range will be extended by this proportion.
     */
    protected double bottomMargin = 0.05;

    /**
     * The minimum <i>x</i> value for which the {@link Solvable Solvable}
     * object will be evaluated.
     */
    protected double minX = -5;

    /**
     * The maximum <i>x</i> value for which the {@link Solvable Solvable}
     * object will be evaluated.
     */
    protected double maxX = +5;

    /**
     * The minimum <i>x</i> value to be included on the graph
     */
    protected double viewLeft = -5;

    /**
     * The maximum <i>x</i> value to be included on the graph
     */
    protected double viewRight = +5;

    /**
     * The minimum <i>y</i> value to be included on the graph
     */
    protected double minY = -3.0;

    /**
     * The maximum <i>y</i> value to be included on the graph
     */
    protected double maxY = +3.0;

    /**
     * The difference <code>viewRight - viewLeft</code>
     */
    protected double viewRange = viewRight - viewLeft;

    /**    The difference <code>maxX - minX</code>	*/
	protected double rangeX = maxX-minX;

	/**	The difference <code>maxY - minY</code>	*/
	protected double rangeY = maxY-minY;


	protected static double
		ABS_MIN = -1e100,
		ABS_MAX = 1e100;
}
