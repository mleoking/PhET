/**
 * Approximation to the Fermi-Dirac distribution, using the Boltzmann
 * distribution.
 */
class Boltzmann extends FermiDirac {
    public Boltzmann() {
    }

    /**
     * @param    x    The energy to evaluate for
     * @return        <code>exp(-(x-ef)/kT)</code> or
     * <code>1-exp((x-ef)/kT)</code>, depending on
     * the sign of <code>x-ef</code>
     */
    public double evaluate( double x ) {
        double d;
        if( ( x - ef ) >= 0 ) {
            d = Math.exp( -( x - ef ) * e_over_k / t );
        }
        else {
            d = 1 - Math.exp( ( x - ef ) * e_over_k / t );
        }

        return d;
    }
}