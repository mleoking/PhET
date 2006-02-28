/**
 * Calculates capacitance as a function of voltage, with v<sub>0</sub>,
 * N<sub>d</sub> and A (cross-sectional area) as parameters.
 */

class Capacitance implements Function, PhysicsConstants {
    /**
     * @param    v0    Equilibrium voltage
     * @param    Nd    Donor density
     * @param    a    Cross-sectional area
     */
    public Capacitance( double v0, double Nd, double a ) {
        setV0( v0 );
        setNd( Nd );
        setA( a );
    }


    /**
     * Creates a new Capacitance, with cross-sectional area
     * 2.0x10<sup>-9</sup>.
     */
    public Capacitance( double v0, double Nd ) {
        this( v0, Nd, 2e-9 );
    }


    /**
     * Creates a new Capacitance, with cross-sectional area
     * 2.0 x 10<sup>-9</sup> and donor density 1.0 x 10<sup>22</sup>.
     */
    public Capacitance( double v0 ) {
        this( v0, 1e22 );
    }


    /**
     * Creates a new Capacitance, with cross-sectional area
     * 2.0 x 10<sup>-9</sup>, donor density 1.0 x 10<sup>22</sup> and
     * v<sub>0</sub> set to 1.
     */
    public Capacitance() {
        this( 1 );
    }


    /**
     * Calculates the capacitance for the specified voltage.
     */
    public double evaluate( double v ) {
        double k = 1;
        double _2qeN_d = 2 * q * epsilon * Nd;
        double A_over_2 = a / 2;
        return A_over_2 * Math.sqrt( _2qeN_d / ( v0 - v ) );
    }

    public double getV0() {
        return v0;
    }

    public void setV0( double d ) {
        v0 = d;
    }

    public double getNd() {
        return Nd;
    }

    public void setNd( double d ) {
        Nd = d;
    }

    public double getA() {
        return a;
    }

    public void setA( double d ) {
        a = d;
    }


    protected double v0,Nd,a;
}