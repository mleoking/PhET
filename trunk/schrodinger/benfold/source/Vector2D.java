/**
 * Represents a vector containing two components.
 * <p/>
 * All of the methods execpt for {@link #setX(double) setX()},
 * {@link #setY(double) setY()} and {@link #set(double,double) set()} will
 * leave the vector unchanged; the result will be returned rather than
 * assigned.	Although it is possible	to modify the components, one
 * should bear in mind that other classes may store vectors, and may not
 * anticipate a change.
 */
class Vector2D {
    /**
     * Creates a new vector.
     *
     * @param    x    The 'x' component
     * @param    y    The 'y' component
     */
    public Vector2D( double x, double y ) {
        this.x = x;
        this.y = y;
    }


    /**
     * Creates a new zero vector.
     */
    public Vector2D() {
        this( 0, 0 );
    }


    /**
     * Adds the supplied vector to this one, and returns the result.
     */
    public Vector2D add( Vector2D v ) {
        return new Vector2D( x + v.x, y + v.y );
    }


    /**
     * Subtracts the supplied vector from this one, and returns the result.
     */
    public Vector2D subtract( Vector2D v ) {
        return new Vector2D( x - v.x, y - v.y );
    }


    /**
     * Multiplies this vector by the supplied constant, and returns the result.
     */
    public Vector2D multiply( double d ) {
        return new Vector2D( x * d, y * d );
    }


    /**
     * Returns the scalar product of the supplied vector with the this one.
     */
    public double scalarProduct( Vector2D v ) {
        return x * v.x + y * v.y;
    }


    /**
     * Returns the modulus of this vector.
     */
    public double modulus() {
        return Math.sqrt( x * x + y * y );
    }


    /**
     * Returns the square of the modulus of this vector.
     */
    public double modulusSquared() {
        return x * x + y * y;
    }


    /**
     * Returns the argument of this vector (radians widdershins from (1,0)).
     */
    public double argument() {
        return Math.atan2( y, x );
    }


    /**
     * Returns a vector with modulus 1, and the same argument as this vector.
     */
    public Vector2D unitVector() {
        double mod = modulus();
        return new Vector2D( x / mod, y / mod );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public void setY( double y ) {
        this.y = y;
    }


    /**
     * Sets both components of this vector.
     *
     * @param    x    The new 'x' component
     * @param    y    The new 'y' component
     */
    public void set( double x, double y ) {
        this.x = x;
        this.y = y;
    }


    /**
     Returns <code>true</code> if either of the components are
     <code>NaN</code>s, else <code>false</code>
     */
    public boolean isNaN() {
        return Double.isNaN( x ) || Double.isNaN(y);
	}


	protected double x,y;
}