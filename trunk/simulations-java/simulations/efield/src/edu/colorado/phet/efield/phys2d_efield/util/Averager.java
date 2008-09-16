// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

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
