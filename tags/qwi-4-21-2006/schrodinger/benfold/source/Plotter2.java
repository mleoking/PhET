import java.awt.*;


/**
 * A plotter augmented with extra methods to facilitate common tasks.
 * This plotter can remember a viewport (a rectangle on the screen), and
 * automatically supply the position and dimensions, simplifying coordinate
 * transformations.
 */
class Plotter2 extends Plotter {
    /**
     * Creates a new plotter
     *
     * @param eqn The Solvable object to obtain solution data from
     * @param    x    The x coordinate of the viewport
     * @param    y    The y coordinate of the viewport
     * @param    w    The width of the viewport
     * @param    h    The height of the viewport
     */
    public Plotter2( Solvable eqn, int x, int y, int w, int h ) {
        super( eqn );
        setRect( x, y, w, h );
    }


    /**
     * Creates a new plotter, with an empty (0,0,0,0) viewport
     *
     * @param eqn The Solvable object to obtain solution data from
     */
    public Plotter2( Solvable eqn ) {
        this( eqn, 0, 0, 0, 0 );
    }


    /**
     * Paints this graph, with axes and scales (if appropriate) inside the
     * viewport.
     *
     * @param    g    The Graphics surface on which to paint
     */
    public void paint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics();
        hSpace = getSpaceX( fm );
        vSpace = getSpaceY( fm );
        g.translate( x, y );
        g.setColor( graphCol );
        paint( g, w, h );
        g.translate( -x, -y );
    }


    /**
     * Fills the cache with solution data.  The number of samples taken is
     * equal to the width of the viewport, minus the width overhead for the
     * vertical axis.
     */
    public void updateSolution( Graphics g ) {
        updateSolution( w - getSpaceX( g.getFontMetrics() ) );
    }


    /**
     * Sets the viewport for this plotter.
     *
     * @param    x    The x coordinate of the viewport
     * @param    y    The y coordinate of the viewport
     * @param    w    The width of the viewport
     * @param    h    The height of the viewport
     */
    public void setRect( int x, int y, int w, int h ) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }


    /**
     * Adjusts the minimum <i>y</i> value
     */
    public void setMinY( double d ) {
        setRangeY( d, maxY );
    }

    /**
     * Adjusts the maximum <i>y</i> value
     */
    public void setMaxY( double d ) {
        setRangeY( minY, d );
    }

    /**
     * Adjusts the minimum <i>x</i> value
     */
    public void setMinX( double d ) {
        setRangeX( d, maxX );
    }

    /**
     * Adjusts the maximum <i>x</i> value
     */
    public void setMaxX( double d ) {
        setRangeX( minX, d );
    }


    /**
     * Converts an <i>x</i> value from graph space to screen space
     */
    public int toScreenX( double d ) {
        return x + hSpace + super.toScreenX( d, w - hSpace );
    }

    /**
     * Converts a <i>y</i> value from graph space to screen space
     */
    public int toScreenY( double d ) {
        return y + super.toScreenY( d, h - vSpace );
    }

    /**
     * Converts an <i>x</i> value from screen space to graph space
     */
    public double fromScreenX( int i ) {
        return super.fromScreenX( i - hSpace - x, w - hSpace );
    }

    /**
     * Converts a <i>y</i> value from screen space to graph space
     */
    public double fromScreenY( int i ) {
        return super.fromScreenY( i - y, h - vSpace );
    }


    /**
     * Returns the colour used when plotting the actual graph
     */
    public Color getGraphColor() {
        return graphCol;
    }

    /**
     * Sets the colour to use when plotting the actual graph
     */
    public void setGraphColor( Color c ) {
        graphCol = c;
    }


    /**    The colour to use when plotting the actual graph	*/
    protected Color graphCol;
	protected int x,y,w,h,hSpace,vSpace;
}