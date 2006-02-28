/**
 * Class to calculate electron or hole concentrations
 */
class CarrierConcentration extends FermiDirac implements PhysicsConstants {
    /**
     * @param    holes    <code>true</code> if the hole concentration is
     * required, </code>false</code> for electrons
     * @param    e0        the applicable edge of the band gap - E<sub>c</sub>
     * for electrons, E<sub>v</sub> for holes
     */
    public CarrierConcentration( double e0, boolean holes ) {
        this.e0 = e0;
        this.holes = holes;
        double d = 2 * m / ( h_bar * h_bar );
        //	Add in extra factor of e, as y-scale is in eV.  This should be
        //	mentioned on the x-axis label
        multiplier = Math.sqrt( e * e * e ) * Math.sqrt( d * d * d ) / ( 2 * Math.PI * Math.PI );
        setRatio( 1 );
    }

    public double evaluate( double x ) {
        return holes
               ? holeMultiplier * Math.sqrt( e0 - x ) * Math.exp( -( ef - x ) * e_over_k / t )
               : multiplier * Math.sqrt( x - e0 ) * Math.exp( -( x - ef ) * e_over_k / t );
        //	? holeMultiplier*(Math.sqrt(e0-x)/(1+Math.exp((ef-x)*e_over_k/t)))
        //	: multiplier*(Math.sqrt(x-e0)/(1+Math.exp((x-ef)*e_over_k/t)));
    }

    public double getE0() {
        return e0;
    }

    public void setE0( double d ) {
        e0 = d;
    }


    /**
     * Returns the ratio between the effective masses of holes and electrons
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Sets the ratio between the effective masses of holes and electrons.
     * This causes the result of any hole density calculation to be
     * multiplied by (ratio)<sup>3/2</sup>.
     */
    public void setRatio( double ratio ) {
        this.ratio = ratio;
        holeMultiplier = multiplier * Math.sqrt( ratio * ratio * ratio );
    }


    /**
     * The ratio between the effective mass of an electron and a hole.
     */
    protected double ratio;
    /**
     * A premultiplier combining most of the constants
     */
    protected double multiplier;
    /**
     * Modified premultiplier for holes, taking into account the mass ratio.
     */
    protected double holeMultiplier;
    /**
     * <code>true</code> if this CarrierConcentration object is calculating
     * hole densities, <code>false</code> for electrons.
     */
    protected boolean holes;
    /**    The appropriate edge of the band gap (depending on whether the hole
     or electron concentration is being calculated).
     */
    protected double e0;
}