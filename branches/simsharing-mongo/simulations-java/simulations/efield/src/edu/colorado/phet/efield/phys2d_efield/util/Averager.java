// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.util;

import java.io.Serializable;

public class Averager
        implements Serializable {

    public Averager() {
        reset();
    }

    public void update( double d ) {
        sum += d;
        num++;
        average = sum / (double) num;
    }

    public double value() {
        return average;
    }

    public void reset() {
        sum = 0.0D;
        num = 0;
        average = ( 0.0D / 0.0D );
    }

    double average;
    double sum;
    int num;
}
