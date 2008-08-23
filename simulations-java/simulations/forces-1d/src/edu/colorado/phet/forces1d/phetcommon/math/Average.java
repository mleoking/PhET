package edu.colorado.phet.forces1d.phetcommon.math;

/**
 * Author: Sam Reid
 * Aug 25, 2007, 1:33:38 AM
 */
public class Average {
    private double sum;
    private int num;

    public Average() {
        reset();
    }

    public void update( double newValue ) {
        sum += newValue;
        num++;
    }

    public double value() {
        return sum / num;
    }

    /**
     * Returns the number of values since last reset().
     *
     * @return the number of values since last reset().
     */
    public int numValues() {
        return num;
    }

    /**
     * Reset the Average.
     */
    public void reset() {
        sum = 0;
        num = 0;
    }
}
