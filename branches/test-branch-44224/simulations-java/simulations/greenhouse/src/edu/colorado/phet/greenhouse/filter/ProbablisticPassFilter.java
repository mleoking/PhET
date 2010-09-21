/**
 * Class: ProbablisticPassFilter
 * Package: edu.colorado.phet.filter
 * Author: Another Guy
 * Date: Oct 15, 2003
 */
package edu.colorado.phet.greenhouse.filter;

public class ProbablisticPassFilter extends Filter1D {
    private double probability;

    public ProbablisticPassFilter( double probability ) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability( double probability ) {
        this.probability = probability;
    }

    public boolean passes( double value ) {
        return Math.random() <= probability;
    }
}
