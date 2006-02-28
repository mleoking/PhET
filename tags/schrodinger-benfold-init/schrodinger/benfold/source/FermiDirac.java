/**
 * Models the Fermi-Dirac distribution for a given temperature and Fermi
 * level.
 */
class FermiDirac implements Function, PhysicsConstants {
    public FermiDirac() {
        setT( 1 );
    }

    public double evaluate( double x ) {
        //return 0.5;
        return 1 / ( 1 + Math.exp( ( x - ef ) * e_over_k / t ) );
    }


    /**
     * @return The temperature parameter
     */
    public double getT() {
        return t;
    }

    /**
     * @return The Fermi level
     */
    public double getEF() {
        return ef;
    }

    /**
     * @param    d    The new temperature
     */
    public void setT( double d ) {
        this.t = d;
    }

    /**
     * @param    d    The new Fermi level
     */
    public void setEF( double d ) {
        this.ef = d;
    }


    /**
     * The temperature parameter
     */
    protected double t;
    /**    The Fermi level parameter	*/
    protected double ef;
}