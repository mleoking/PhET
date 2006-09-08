package edu.colorado.phet.common_cck.math;

public class Average {
    private double average;
    private double sum;
    private int num;

    public Average() {
        reset();
    }

    public void update( double newValue ) {
        sum += newValue;
        num++;
        average = sum / num;
    }

    public double value() {
        return average;
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
        average = Double.NaN; //not yet evaluated
    }
}
