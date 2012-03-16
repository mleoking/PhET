// Copyright 2002-2011, University of Colorado
package jass.generators;

/**
 * Interface defining a filter with one input and one output.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public interface Filter {
    /**
     * Proces input.
     *
     * @param output      user provided buffer for returned result.
     * @param input       user provided input buffer.
     * @param nsamples    number of samples written to output buffer.
     * @param inputOffset where to start in circular buffer input.
     */
    public void filter( float[] output, float[] input, int nsamples, int inputOffset );
}
