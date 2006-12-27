package edu.colorado.phet.common.phys2d.util;

import java.io.Serializable;

/**
 * The fitness manager keeps track of an average.  You update it's value by calling:
 * update(double),
 * then get the new fitness value:
 * getFitness(),
 * and reset with
 * reset().
 *
 * @author Sam Reid, 2000.
 */
public class Averager implements Serializable {
    double average;
    double sum;
    int num;

    public Averager() {
        reset();
    }

    public void update( double newValue ) {
        sum += newValue;
        num++;
        average = sum / num;
    }

    /**
     * Returns the present average value
     *
     * @return the present average of the Averager.
     */
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
     * Reset the Averager.
     */
    public void reset() {
        sum = 0;
        num = 0;
        average = Double.NaN; //not yet evaluated
    }
}
