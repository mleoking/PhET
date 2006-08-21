/**
 * Calculates effective mass of an electron, given an energy-momentum model
 * (such as the {@link KronigPenney KronigPenny} model).
 * <p/>
 * <b>Not sure if this implementation is correct!</b>
 */
class EffectiveMass implements Solvable {
    /**
     * Creates a new effective mass calculator
     *
     * @param    energyMomentum    The energy-momentum model
     */
    public EffectiveMass( Solvable energyMomentum ) {
        this.energyMomentum = energyMomentum;
    }

    public void solve( double x0, double step, double[] vals ) {
        double[] d = new double[vals.length];
        energyMomentum.solve( x0, step, d );

        double stepSquared = step * step;

        for( int i = 1; i < d.length - 1; i++ ) {
            double x = ( x0 + i * step );
            vals[i] = x * x / d[i];
            //	Just do finite difference (reciprocal of)
            //vals[i] = (stepSquared/(-2*d[i] + d[i-1] + d[i+1]));
        }
    }


    /**    The energy-momentum model	*/
    protected Solvable energyMomentum;
}