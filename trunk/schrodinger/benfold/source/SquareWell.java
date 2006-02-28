/**
 * A function which is constantly zero, except for exactly one finite
 * interval, in which it takes a single different constant value
 * (the &quot;depth&quot;).
 */
class SquareWell implements Function {
    /**
     * Creates a new square well function
     *
     * @param shift The <i>x</i> coordinate of the centre of the well
     * @param    depth    The depth of the well
     * @param    width    The width of the well
     */
    public SquareWell( double depth, double width, double shift ) {
        this.depth = depth;
        this.width = width;
        this.shift = shift;
    }


    /**
     * Creates a new square well function, centred on the origin.
     *
     * @param    depth    The depth of the well
     * @param    width    The width of the well
     */
    public SquareWell( double depth, double width ) {
        this( depth, width, DEFAULT_SHIFT );
    }


    /**
     * Creates a new square well function, centred on the origin, with
     * default	width.
     *
     * @param    depth    The depth of the well
     */
    public SquareWell( double depth ) {
        this( depth, DEFAULT_WIDTH );
    }


    /**
     * Creates a new square well function, centred on the origin, with
     * default	width and depth.
     */
    public SquareWell() {
        this( DEFAULT_DEPTH );
    }

    public double evaluate( double x ) {
        return ( 2 * Math.abs( x - shift ) < width ) ? depth : 0;
    }


    /**
     * Returns an array containing <i>x</i> coordinates of all
     * discontinuities within the specified interval.
     */
    public double[] getMarkers( double minX, double minY ) {
        return new double[]{shift - width / 2, shift + width / 2};
    }


    /**
     * Returns the depth of the well
     */
    public double getDepth() {
        return depth;
    }

    /**
     * Returns the width of the well
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the <i>x</i> coordinate of the centre of the well
     */
    public double getShift() {
        return shift;
    }

    /**
     * Returns the <i>x</i> coordinate of the left-hand edge of the well
     */
    public double getLeft() {
        return shift - width / 2;
    }

    /**
     * Returns the <i>x</i> coordinate of the right-hand edge of the well
     */
    public double getRight() {
        return shift + width / 2;
    }

    /**
     * Sets the width of the well
     */
    public void setWidth( double d ) {
        width = d;
    }

    /**
     * Sets the width of the well
     */
    public void setDepth( double d ) {
        depth = d;
    }

    /**
     * Sets the width of the well
     */
    public void setShift( double d ) {
        shift = d;
    }


    protected double depth, width, shift;
    public static final double
            DEFAULT_DEPTH = -1,
            DEFAULT_WIDTH = 2,
		DEFAULT_SHIFT = 0;
}
