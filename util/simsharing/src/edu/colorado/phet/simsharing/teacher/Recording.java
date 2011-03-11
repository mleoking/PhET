// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.simsharing.Sample;

/**
 * @author Sam Reid
 */
public class Recording implements Serializable {
    ArrayList<Sample> samples;

    public Recording( ArrayList<Sample> samples ) {
        this.samples = samples;
    }

    public ArrayList<Sample> getSamples() {
        return samples;
    }

    @Override
    public String toString() {
        return "recording, samples.size = " + samples.size();
    }
}
