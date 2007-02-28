
package edu.colorado.phet.quantumtunneling.rungekutta;

/**
 * Allows manipulation of complex numbers.  Implementation is such that a
 * complex number is immutable; arithmetic operations may therefore be
 * combined using any combination of composed or cascaded method invocations.
 */
class RK4Complex {
    /**
     * Creates a new complex number
     *
     * @param re The real part of this number
     * @param im The imaginary part of this number
     */
    public RK4Complex( double re, double im ) {
        this.re = re;
        this.im = im;
    }


    /**
     * Creates a new complex number with no imaginary component
     */
    public RK4Complex( double d ) {
        this( d, 0 );
    }


    /**
     * @return This number plus the specified real number
     */
    public RK4Complex add( double d ) {
        return new RK4Complex( re + d, im );
    }


    /**
     * @return This number plus the specified complex number
     */
    public RK4Complex add( RK4Complex c ) {
        return new RK4Complex( re + c.re, im + c.im );
    }


    /**
     * @return This number minus the specified real number
     */
    public RK4Complex subtract( double d ) {
        return new RK4Complex( re - d, im );
    }


    /**
     * @return This number minus the specified complex number
     */
    public RK4Complex subtract( RK4Complex c ) {
        return new RK4Complex( re - c.re, im - c.im );
    }


    /**
     * @return This number multiplied by the specified real number
     */
    public RK4Complex multiply( double d ) {
        return new RK4Complex( re * d, im * d );
    }


    /**
     * @return This number multiplied by the specified complex number
     */
    public RK4Complex multiply( RK4Complex c ) {
        return new RK4Complex( c.re * re - c.im * im, re * c.im + c.re * im );
    }


    /**
     * @return This number divided by the specified real number
     */
    public RK4Complex divide( double d ) {
        return new RK4Complex( re / d, im / d );
    }


    /**
     * @return This number divided by the specified complex number
     */
    public RK4Complex divide( RK4Complex c ) {
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
    public RK4Complex negate() {
        return new RK4Complex( -re, -im );
    }


    /**
     * @return The reciprocal of this complex number
     */
    public RK4Complex reciprocal() {
        double r = 1 / modulus();
        double theta = -argument();
        return polar( r, theta );
    }


    /**
     * @return The square root of this complex number
     */
    public RK4Complex sqrt() {
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
     * @param o The complex number to compare to this one
     * @return <code>true</code> if the specified complex number is equal to
     *         this one, else <code>false</code>
     * @throws ClassCastException   If <code>o</code> is not a complex
     *                              number
     * @throws NullPointerException If <code>o</code> is <code>null</code>
     */
    public boolean equals( Object o ) {
        RK4Complex c = (RK4Complex)o;
        return ( c.re == re ) && ( c.im == im );
    }


    /**
     * @return A string representation of this number, of the form
     *         <code>a+bi</code>, where <code>a</code> and <code>b
     *         </code> are real numbers
     */
    public String toString() {
        return re + "+" + im + "i";
    }


    /**
     * Constructs a complex number from modulus and argument components
     *
     * @param r     The modulus
     * @param theta The argument
     * @return r*exp(i*theta)
     */
    public static RK4Complex polar( double r, double theta ) {
        return new RK4Complex( r * Math.cos( theta ), r * Math.sin( theta ) );
    }


    /**
     * Returns the exponent of a complex number
     *
     * @param c The number to be exponentiated
     * @return exp(c);
     */
    public static RK4Complex e( RK4Complex c ) {
        return polar( Math.exp( c.re ), c.im );
    }


    protected double re, im;


    /**
     * Set to <code>true</code> to enable warnings
     */
    public static boolean DEBUG_WARNINGS = false;
    public static final RK4Complex
            ONE = new RK4Complex( 1 ),
            ZERO = new RK4Complex( 0 ),
            I = new RK4Complex( 0, 1 );
}
