/**
 * Allows manipulation of complex numbers.  Implementation is such that a
 * complex number is immutable; arithmetic operations may therefore be
 * combined using any combination of composed or cascaded method invocations.
 */
class Complex {
    /**
     * Creates a new complex number
     *
     * @param    re    The real part of this number
     * @param    im    The imaginary part of this number
     */
    public Complex( double re, double im ) {
        this.re = re;
        this.im = im;
    }


    /**
     * Creates a new complex number with no imaginary component
     *
     * @param    re    The real part of this number
     */
    public Complex( double d ) {
        this( d, 0 );
    }


    /**
     * @return This number plus the specified real number
     */
    public Complex add( double d ) {
        return new Complex( re + d, im );
    }


    /**
     * @return This number plus the specified complex number
     */
    public Complex add( Complex c ) {
        return new Complex( re + c.re, im + c.im );
    }


    /**
     * @return This number minus the specified real number
     */
    public Complex subtract( double d ) {
        return new Complex( re - d, im );
    }


    /**
     * @return This number minus the specified complex number
     */
    public Complex subtract( Complex c ) {
        return new Complex( re - c.re, im - c.im );
    }


    /**
     * @return This number multiplied by the specified real number
     */
    public Complex multiply( double d ) {
        return new Complex( re * d, im * d );
    }


    /**
     * @return This number multiplied by the specified complex number
     */
    public Complex multiply( Complex c ) {
        return new Complex( c.re * re - c.im * im, re * c.im + c.re * im );
    }


    /**
     * @return This number divided by the specified real number
     */
    public Complex divide( double d ) {
        return new Complex( re / d, im / d );
    }


    /**
     * @return This number divided by the specified complex number
     */
    public Complex divide( Complex c ) {
        return multiply( c.reciprocal() );
    }


    /**
     * @return The modulus of this complex number
     */
    public double modulus() {
        return Math.sqrt( re * re + im * im );
    }


    /**
     * If the modulus of the number is zero, then the argument returned will
     * be zero.
     *
     * @return The argument, in radians, of this complex number
     */
    public double argument() {
        if( re == 0 && im == 0 ) {
            if( DEBUG_WARNINGS ) {
                System.err.println( "attempt to eval arg(0); will return 0" );
            }
            return 0;
        }
        return Math.atan2( im, re );
    }


    /**
     * @return The negation of this complex number
     */
    public Complex negate() {
        return new Complex( -re, -im );
    }


    /**
     * @return The reciprocal of this complex number
     */
    public Complex reciprocal() {
        double r = 1 / modulus();
        double theta = -argument();
        return polar( r, theta );
    }


    /**
     * @return The square root of this complex number
     */
    public Complex sqrt() {
        double r = Math.sqrt( modulus() );
        double theta = argument() / 2;
        return polar( r, theta );
    }


    /**
     * @return The square of the modulus of this complex number
     */
    public double modulusSquared() {
        return re * re + im * im;
    }


    /**
     * @return <code>true</code> if the specified complex number is equal to
     *         this one, else <code>false</code>
     * @throws ClassCastException If <code>o</code> is not a complex
     *                            number
     * @param    o    The complex number to compare to this one
     * @throws NullPointerException    If <code>o</code> is <code>null</code>
     */
    public boolean equals( Object o ) {
        Complex c = (Complex)o;
        return ( c.re == re ) && ( c.im == im );
    }


    /**
     * @return A string representation of this number, of the form
     * <code>a+bi</code>, where <code>a</code> and <code>b
     * </code> are real numbers
     */
    public String toString() {
        return re + "+" + im + "i";
    }


    /**
     * Constructs a complex number from modulus and argument components
     *
     * @return r*exp(i*theta)
     * @param    r        The modulus
     * @param    theta    The argument
     */
    public static Complex polar( double r, double theta ) {
        return new Complex( r * Math.cos( theta ), r * Math.sin( theta ) );
    }


    /**
     * Returns the exponent of a complex number
     *
     * @param    c    The number to be exponentiated
     * @return exp(c);
     */
    public static Complex e( Complex c ) {
        return polar( Math.exp( c.re ), c.im );
    }


    protected double re, im;


    /**
     * Set to <code>true</code> to enable warnings
     */
    public static boolean DEBUG_WARNINGS = false;
    public static final Complex
            ONE = new Complex(1),
		ZERO = new Complex(0),
		I = new Complex(0,1);
}