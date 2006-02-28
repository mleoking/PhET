/**
 * Calculates current as a function of voltage, with temperature as a
 * parameter.
 */

class Current implements Function {
    /**
     * Creates a new Current object, with temperature set to 1K.
     */
    public Current() {
        t = 1;
    }


    /**
     * Calculates the current at the specified voltage.
     */
    public double evaluate( double v ) {
        double q = 1;
        double k = 1;
        return q * ( Math.exp( q * v / ( k * t ) ) - 1 );
    }


    /**
     * Returns the current temperature setting
     */
    public double getT() {
        return t;
    }

    /**
     * Sets the temperature
     */
    public void setT( double d ) {
        this.t = d;
    }


    protected double t;
}