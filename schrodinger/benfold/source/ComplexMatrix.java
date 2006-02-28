/**
 * Represents a 2x2 matrix of complex numbers.  The implementation is such
 * that instances of <code>ComplexMatrix</code> are immutable; operations
 * should be combined through composition and cascading of method
 * invokations.
 */
class ComplexMatrix {
    /**
     * Creates a new complex matrix.  Entries are as pictured:
     * <pre>
     * / a b \
     * \ c d /
     * </pre>
     */
    public ComplexMatrix( Complex a, Complex b, Complex c, Complex d ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }


    /**
     * Creates a real-valued complex matrix.  Entries are as pictured:
     * <pre>
     * / a b \
     * \ c d /
     * </pre>
     */
    public ComplexMatrix( double a, double b, double c, double d ) {
        this( new Complex( a ), new Complex( b ), new Complex( c ), new Complex( d ) );
    }


    /**
     * Creates a new identity matrix
     */
    public ComplexMatrix() {
        this( 1, 0, 0, 1 );
    }


    /**
     * Computes the product of two matrices.  The product M = ABC should be
     * coded as <code>M = A.multiply(B.multiply(C))</code> or
     * <code>M = A.multiply(B).multiply(C)</code>; these are equivalent due
     * to associativity.
     *
     * @return this*m
     */
    public ComplexMatrix multiply( ComplexMatrix m ) {
        return new ComplexMatrix(
                a.multiply( m.a ).add( b.multiply( m.c ) ),
                a.multiply( m.b ).add( b.multiply( m.d ) ),
                c.multiply( m.a ).add( d.multiply( m.c ) ),
                c.multiply( m.b ).add( d.multiply( m.d ) )
        );
    }


    /**
     * Scales all entries in this matric by a real number
     *
     * @return The scaled matrix
     * @param    v    The scaling factor
     */
    public ComplexMatrix multiply( double v ) {
        return new ComplexMatrix( a.multiply( v ), b.multiply( v ), c.multiply( v ), d.multiply( v ) );
    }


    /**
     * @return A string representation of this matrix, of the form
     *         <code>(a,b,c,d)</code>
     */
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ", " + d + ")";
    }


    protected Complex a, b, c, d;

    public static final ComplexMatrix IDENTITY = new ComplexMatrix();
}
